package com.common.security.filter;

import com.common.security.springsecurity.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWT Authentication Filter - Middleware that intercepts every request
 *
 * Flow:
 * 1. Extract JWT token from Authorization header
 * 2. Validate the token
 * 3. Load user details from database
 * 4. Set authentication in SecurityContext
 * 5. Pass request to next filter/controller
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    /**
     * Main filter method - executes for every request
     */
    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        // Log incoming request
        log.debug("Processing request: {} {}", request.getMethod(), request.getRequestURI());

        // Step 1: Extract Authorization header
        final String authHeader = request.getHeader("Authorization");

        // If no Authorization header or doesn't start with "Bearer ", skip JWT processing
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.debug("No JWT token found in request headers");
            filterChain.doFilter(request, response);
            return;
        }

        try {
            // Step 2: Extract token (remove "Bearer " prefix)
            final String jwt = authHeader.substring(7);
            log.debug("JWT token extracted from header");

            // Step 3: Extract username from token
            final String userEmail = jwtService.extractUsername(jwt);
            log.debug("Extracted username from JWT: {}", userEmail);

            // Step 4: Check if user is not already authenticated
            if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                // Step 5: Load user details from database
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);
                log.debug("User details loaded for: {}", userEmail);
                log.debug("User authorities from UserDetails: {}", userDetails.getAuthorities());

                // Step 6: Validate token (check expiration and signature)
                if (!jwtService.isTokenExpired(jwt)) {
                    log.debug("JWT token is valid");

                    // Step 7: Create authentication token
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null, // credentials (password) - not needed after authentication
                            userDetails.getAuthorities() // user roles
                    );

                    log.debug("Authentication token created with authorities: {}", authToken.getAuthorities());

                    // Step 8: Set additional details (IP address, session ID, etc.)
                    authToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );

                    // Step 9: Set authentication in SecurityContext
                    // This is THE KEY STEP - now Spring Security knows user is authenticated
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    log.info("User authenticated successfully: {}", userEmail);

                } else {
                    log.warn("JWT token has expired for user: {}", userEmail);
                }
            }

            // Step 10: Continue filter chain (pass to next filter or controller)
            filterChain.doFilter(request, response);

        } catch (Exception e) {
            // Handle any JWT-related exceptions
            log.error("Cannot set user authentication: {}", e.getMessage());

            // Clear SecurityContext in case of error
            SecurityContextHolder.clearContext();

            // Send error response
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write(
                    "{\"status\":\"error\",\"message\":\"Invalid or expired JWT token\"}"
            );
        }
    }

    /**
     * Optional: Skip filter for certain endpoints (like /auth/login, /auth/register)
     * Uncomment if you want to explicitly skip authentication for public endpoints
     */
    /*
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        return path.startsWith("/auth/") || 
               path.startsWith("/public/") ||
               path.equals("/error");
    }
    */
}