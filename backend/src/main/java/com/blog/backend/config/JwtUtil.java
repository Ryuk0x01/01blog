package com.blog.backend.config;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import com.blog.backend.entity.User;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.crypto.SecretKey;

@Component
public class JwtUtil {

    private final String SECRET = "MY_SUPER_SECRET_KEY_12345678901234567890";
    private final long EXPIRATION = 1000 * 60 * 60 * 24; // 24H

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET.getBytes());
    }

    // ------------------------------
    // Generate JWT
    // ------------------------------
    public String generateToken(User user) {

        Map<String, Object> claims = new HashMap<>();
        claims.put("username", user.getUsername());
        claims.put("role", user.getRole());

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(user.getEmail())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // ------------------------------
    // Extract Email
    // ------------------------------
    public String extractEmail(String token) {
        return extractAllClaims(token).getSubject();
    }

    // ------------------------------
    // Extract Role
    // ------------------------------
    public String extractRole(String token) {
        return extractAllClaims(token).get("role", String.class);
    }

    // ------------------------------
    // Validate Token
    // ------------------------------
    public boolean validateToken(String token) {
        try {
            extractAllClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // ------------------------------
    // Parse Token
    // ------------------------------
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
