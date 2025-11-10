package com.micro.apigateway.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
public class CorsConfig {

    @Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration config = new CorsConfiguration();

        // ✅ Allow your frontend origin
        config.setAllowedOrigins(List.of("http://localhost:3000"));

        // ✅ Allow all main HTTP methods
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));

        // ✅ Allow ALL headers (important for preflight requests)
        config.setAllowedHeaders(List.of("*"));

        // ✅ Expose headers that frontend can read
        config.setExposedHeaders(Arrays.asList("Authorization", "Content-Type"));

        // ✅ Allow credentials (cookies, Authorization headers)
        config.setAllowCredentials(true);

        // ✅ Cache preflight requests for 1 hour
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return new CorsWebFilter(source);
    }
}