package com.university.placementsystem.security;

import com.university.placementsystem.entity.UserRole;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.Map;

/**
 * Utility class for generating and validating JWT tokens.
 * <p>
 * Features:
 * - Generates JWT with custom claims (id, email, role, name)
 * - Extracts claims from JWT
 * - Validates expiration and signature
 */
@Component
public class JwtUtil {

    // ==== Constants ====
    private static final String CLAIM_ID = "id";
    private static final String CLAIM_ROLE = "role";
    private static final String CLAIM_NAME = "name";

    // Secret key (should be loaded from configuration in production!)
    private static final String SECRET =
            "adfghjngbvsawqsedyfvthjmknhbgdfcxsawqedrhnjbgvfdctsexwraxdcthbj";

    // Token expiration: 24 hours
    private static final long EXPIRATION_TIME_MS = 1000 * 60 * 60 * 24;

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET.getBytes());
    }

    // ==== Token Generation ====

    /**
     * Generate a JWT token with user claims.
     *
     * @param id    User ID
     * @param email User email (used as a subject)
     * @param role  User role
     * @param name  User display name
     * @return JWT token string
     */
    public String generateToken(String id, String email, UserRole role, String name) {
        return Jwts.builder()
                .setClaims(Map.of(
                        CLAIM_ID, id,
                        CLAIM_ROLE, role.name(),
                        CLAIM_NAME, name
                ))
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME_MS))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // ==== Claim Extraction ====

    private Claims getAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String extractUsername(String token) {
        return getAllClaims(token).getSubject();
    }

    public String extractRole(String token) {
        return (String) getAllClaims(token).get(CLAIM_ROLE);
    }

    public String extractName(String token) {
        return (String) getAllClaims(token).get(CLAIM_NAME);
    }

    public String extractId(String token) {
        return (String) getAllClaims(token).get(CLAIM_ID);
    }

    public Date extractExpiration(String token) {
        return getAllClaims(token).getExpiration();
    }

    // ==== Validation ====

    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public boolean validateToken(String token, String email) {
        try {
            return extractUsername(token).equals(email) && !isTokenExpired(token);
        } catch (JwtException | IllegalArgumentException e) {
            return false; // invalid token
        }
    }
}
