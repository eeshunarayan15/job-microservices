package com.common.security.controller;

import com.common.security.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Test controller to demonstrate JWT Filter working
 * All endpoints here require authentication (JWT token)
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class TestProtectedController {

    /**
     * Test endpoint - requires valid JWT token
     * Try calling without token → 401 Unauthorized
     * Try calling with valid token → 200 OK with user info
     */
    @GetMapping("/test")
    public ResponseEntity<ApiResponse<Map<String, Object>>> testEndpoint() {
        
        // Get authenticated user from SecurityContext
        // This works because JWT Filter set the authentication!
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        Map<String, Object> data = new HashMap<>();
        data.put("message", "You are authenticated!");
        data.put("username", authentication.getName());
        data.put("authorities", authentication.getAuthorities());
        
        ApiResponse<Map<String, Object>> response = new ApiResponse<>(
                "success",
                "Protected endpoint accessed successfully",
                data
        );
        
        return ResponseEntity.ok(response);
    }

    /**
     * Get current user info
     */
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getCurrentUser() {
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        Map<String, Object> userData = new HashMap<>();
        userData.put("email", authentication.getName());
        userData.put("roles", authentication.getAuthorities());
        userData.put("authenticated", authentication.isAuthenticated());
        
        ApiResponse<Map<String, Object>> response = new ApiResponse<>(
                "success",
                "Current user details",
                userData
        );
        
        return ResponseEntity.ok(response);
    }

    /**
     * Admin only endpoint - demonstrates role-based access
     * Requires ROLE_ADMIN
     */
    @GetMapping("/admin/dashboard")
    @PreAuthorize("hasRole('ADMIN')") // Only ADMIN can access
    public ResponseEntity<ApiResponse<String>> adminDashboard() {
        
        ApiResponse<String> response = new ApiResponse<>(
                "success",
                "Welcome to admin dashboard",
                "Admin only content here"
        );
        
        return ResponseEntity.ok(response);
    }

    /**
     * User endpoint - accessible by any authenticated user
     */
    @GetMapping("/user/profile")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')") // USER or ADMIN can access
    public ResponseEntity<ApiResponse<String>> userProfile() {
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        ApiResponse<String> response = new ApiResponse<>(
                "success",
                "User profile for: " + authentication.getName(),
                "Profile data here"
        );
        
        return ResponseEntity.ok(response);
    }
}