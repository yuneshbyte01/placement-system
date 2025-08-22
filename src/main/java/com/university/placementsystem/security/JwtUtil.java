package com.university.placementsystem.security;

import com.university.placementsystem.entity.User;
import com.university.placementsystem.entity.UserRole;
import com.university.placementsystem.repository.UserRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.Map;

/**
 * Utility class for generating, parsing, and validating JWT tokens.
 *
 * <p>Features:</p>
 * <ul>
 *   <li>Generates JWT tokens with user claims (id, email, role, name)</li>
 *   <li>Parses claims from existing tokens</li>
 *   <li>Validates expiration and signature</li>
 *   <li>Provides a helper to load users from DB by ID</li>
 * </ul>
 */
@Component
@RequiredArgsConstructor
public class JwtUtil {

    // === Claim Keys ===
    private static final String CLAIM_ID = "id";     // user ID
    private static final String CLAIM_ROLE = "role"; // user role
    private static final String CLAIM_NAME = "name"; // user display name

    // === Token Config ===
    // TODO: move secret to configuration (env var / application.yml) and rotate periodically
    private static final String SECRET =
            "adfghjngbvsawqsedyfvthjmknhbgdfcxsawqedrhnjbgvfdctsexwraxdcthbj"; // HMAC secret (demo only)
    private static final long EXPIRATION_TIME_MS = 1000 * 60 * 60 * 24; // 24 hours

    private final UserRepository userRepository;

    /**
     * Builds the signing key used for JWT operations.
     *
     * @return HMAC-SHA key
     */
    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET.getBytes());
    }

    /**
     * Generates a JWT token with user-specific claims.
     *
     * @param id    user ID
     * @param email user email (subject)
     * @param role  user role
     * @param name  display name
     * @return signed JWT string
     */
    public String generateToken(String id, String email, UserRole role, String name) {
        return Jwts.builder()
                .setClaims(Map.of(
                        CLAIM_ID, id,
                        CLAIM_ROLE, role.name(),
                        CLAIM_NAME, name
                ))
                .setSubject(email) // subject is the email
                .setIssuedAt(new Date()) // issued now
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME_MS)) // expiry time
                .signWith(getSigningKey(), SignatureAlgorithm.HS256) // HMAC-SHA256
                .compact();
    }

    /**
     * Extracts all claims from a token.
     *
     * @param token JWT token
     * @return parsed {@link Claims}
     */
    private Claims getAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // === Claim Getters ===

    /** Returns subject (email) from token. */
    public String extractUsername(String token) {
        return getAllClaims(token).getSubject();
    }

    /** Returns role from token. */
    public String extractRole(String token) {
        return (String) getAllClaims(token).get(CLAIM_ROLE);
    }

    /** Returns display name from token. */
    public String extractName(String token) {
        return (String) getAllClaims(token).get(CLAIM_NAME);
    }

    /** Returns user ID from token. */
    public String extractId(String token) {
        return (String) getAllClaims(token).get(CLAIM_ID);
    }

    /** Returns expiration time from token. */
    public Date extractExpiration(String token) {
        return getAllClaims(token).getExpiration();
    }

    // === Validation Helpers ===

    /**
     * Checks whether the token has expired.
     *
     * @param token JWT token
     * @return true if expired, false otherwise
     */
    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Validates token against email and expiration.
     *
     * @param token JWT token
     * @param email expected email
     * @return true if valid, false otherwise
     */
    public boolean validateToken(String token, String email) {
        try {
            return extractUsername(token).equals(email) && !isTokenExpired(token);
        } catch (JwtException | IllegalArgumentException e) {
            return false; // invalid signature/format/expired/etc.
        }
    }

    /**
     * Loads a {@link User} entity from the database by ID.
     *
     * @param id user ID
     * @return {@link User} entity
     * @throws RuntimeException if user not found
     */
    public User loadUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
