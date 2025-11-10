package com.micro.apigateway.util;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}") // 30 minutes in milliseconds
    private long jwtExpiration;

    public void validateToken(final String token) {
        log.debug("Validating JWT token");
        try {
            Jwts.parser()
                    .verifyWith(getSignKey())
                    .build()
                    .parseSignedClaims(token);
            log.info("JWT token validation successful");
        } catch (Exception e) {
            log.error("JWT token validation failed: {}", e.getMessage());
            throw e;
        }
    }
//    private SecretKey getSignKey() {
//        byte[] keyBytes = Decoders.BASE64.decode(secret);
//        return Keys.hmacShaKeyFor(keyBytes);
//    }

    private SecretKey getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        // For HS384, need a longer key (48 bytes minimum)
        return Keys.hmacShaKeyFor(keyBytes);
    }
}