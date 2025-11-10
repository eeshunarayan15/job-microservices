package com.micro.apigateway.security;

import com.micro.apigateway.util.JwtService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHeaders;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class JwtAuthFilter extends AbstractGatewayFilterFactory<JwtAuthFilter.Config> {

    private final RouteValidator routeValidator;
    private final JwtService jwtService;

    public JwtAuthFilter(RouteValidator routeValidator, JwtService jwtService) {
        super(Config.class);
        this.routeValidator = routeValidator;
        this.jwtService = jwtService;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {

            if(routeValidator.isSecured.test(exchange.getRequest())) {
                log.debug("Processing secured route: {}", exchange.getRequest().getURI());

                // Check Authorization header exists
                if(!exchange.getRequest().getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                    log.warn("Missing Authorization header");
                    return onError(exchange, "Missing Authorization header", HttpStatus.UNAUTHORIZED);
                    //     ^^^^^^^ Calling the helper method below
                }

                // Extract Authorization header
                String authHeader = exchange.getRequest().getHeaders()
                        .get(HttpHeaders.AUTHORIZATION).get(0);

                // Validate Bearer format
                if(authHeader == null || !authHeader.startsWith("Bearer ")) {
                    log.warn("Invalid Authorization header format");
                    return onError(exchange, "Invalid Authorization header format", HttpStatus.UNAUTHORIZED);
                    //     ^^^^^^^ Calling the helper method below
                }

                // Extract token
                String token = authHeader.substring(7);

                // Validate JWT token
                try {
                    jwtService.validateToken(token);
                    log.debug("JWT token validated successfully");
                } catch (ExpiredJwtException e) {
                    log.error("JWT token expired: {}", e.getMessage());
                    return onError(exchange, "JWT token has expired", HttpStatus.UNAUTHORIZED);
                    //     ^^^^^^^ Calling the helper method below
                } catch (SignatureException e) {
                    log.error("Invalid JWT signature: {}", e.getMessage());
                    return onError(exchange, "Invalid JWT signature", HttpStatus.UNAUTHORIZED);
                } catch (MalformedJwtException e) {
                    log.error("Malformed JWT token: {}", e.getMessage());
                    return onError(exchange, "Malformed JWT token", HttpStatus.UNAUTHORIZED);
                } catch (Exception e) {
                    log.error("JWT validation failed: {}", e.getMessage());
                    return onError(exchange, "JWT authentication failed", HttpStatus.UNAUTHORIZED);
                }
            }

            return chain.filter(exchange);
        };
    }

    // ========================================
    // 👇 ADD THIS HELPER METHOD HERE 👇
    // ========================================
    /**
     * Helper method to create error response with JSON body
     * @param exchange The server web exchange
     * @param message The error message to return
     * @param status The HTTP status code
     * @return Mono<Void> representing the completed response
     */
    private Mono<Void> onError(ServerWebExchange exchange, String message, HttpStatus status) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(status);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        // Create JSON error response
        String errorJson = String.format(
                "{\"error\": \"%s\", \"status\": %d, \"timestamp\": \"%s\"}",
                message,
                status.value(),
                java.time.Instant.now().toString()
        );

        DataBuffer buffer = response.bufferFactory().wrap(errorJson.getBytes());
        return response.writeWith(Mono.just(buffer));
    }
    // ========================================
    // 👆 END OF HELPER METHOD 👆
    // ========================================

    public static class Config {
        // Configuration properties can be added here if needed
    }
}