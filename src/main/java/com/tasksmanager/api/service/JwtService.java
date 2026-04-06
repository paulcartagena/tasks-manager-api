package com.tasksmanager.api.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;

/**
 * Service responsible for JWT token generation and validation.
 * Handles creating signed tokens, extracting claims, and verifying token.
 */
@Service
public class JwtService {
    @Value("${jwt.secret}")
    private String jwtSecret;

    /**
     * Generate a signed JWT token for the given user.
     * Token expires in 24 hours and includes the username as subject.
     */
    public String generateToken(UserDetails userDetails) {
        Instant now = Instant.now();

        return Jwts.builder()
                .id(UUID.randomUUID().toString())
                .subject(userDetails.getUsername())
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(24L, ChronoUnit.HOURS)))
                .signWith(getSigningKey())
                .compact();
    }

    public String extractUsername(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    public Date extractExpiration(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getExpiration();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        String username = userDetails.getUsername();

        return username.equals(extractUsername(token)) &&
                !extractExpiration(token).before(new Date());
    }

    /**
     * Builds the signing key from base64-encoded secret.
     * Used internally to sign and verify JWT tokens.
     */
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }
}
