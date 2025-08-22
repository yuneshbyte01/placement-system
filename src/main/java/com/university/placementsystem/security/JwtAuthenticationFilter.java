package com.university.placementsystem.security;

import com.university.placementsystem.dto.UserDTO;
import com.university.placementsystem.entity.User;
import com.university.placementsystem.entity.UserRole;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * JWT Authentication Filter.
 *
 * <p>Executes once per request to:</p>
 * <ul>
 *   <li>Extract and validate the JWT from the {@code Authorization} header</li>
 *   <li>Load user details and ensure the account is active</li>
 *   <li>Set the authenticated {@link UserDTO} into the {@link SecurityContextHolder}</li>
 * </ul>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    // Header name for JWT
    private static final String AUTH_HEADER = "Authorization";

    // Bearer prefix in the Authorization header
    private static final String BEARER_PREFIX = "Bearer ";

    // Utility for generating and parsing JWTs
    private final JwtUtil jwtUtil;

    /**
     * Main filter logic.
     *
     * <p>Steps:</p>
     * <ol>
     *   <li>Skip filtering for public endpoints</li>
     *   <li>Extract JWT from the Authorization header</li>
     *   <li>Parse claims and validate user</li>
     *   <li>Set authentication in the context if valid</li>
     * </ol>
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {

        // Allow public endpoints to pass through without authentication
        if (isPublic(request)) {
            chain.doFilter(request, response);
            return;
        }

        // Skip if already authenticated
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            chain.doFilter(request, response);
            return;
        }

        // Extract token from the Authorization header
        final String token = getBearerToken(request.getHeader(AUTH_HEADER));
        if (!StringUtils.hasText(token)) {
            chain.doFilter(request, response); // no token found, continue
            return;
        }

        try {
            // Extract claims
            final String idStr   = jwtUtil.extractId(token);
            final String email   = jwtUtil.extractUsername(token);
            final String roleStr = jwtUtil.extractRole(token);
            final String name    = jwtUtil.extractName(token);

            // Ensure required claims are present
            if (!StringUtils.hasText(idStr) || !StringUtils.hasText(email) || !StringUtils.hasText(roleStr)) {
                log.debug("JWT missing required claims (id/email/role)");
                chain.doFilter(request, response);
                return;
            }

            final Long id = Long.parseLong(idStr);

            // Verify that the user account is active
            final User user = jwtUtil.loadUserById(id);
            if (user == null || !user.isActive()) {
                writeJsonError(response);
                return;
            }

            // Parse role safely
            final UserRole role;
            try {
                role = UserRole.valueOf(roleStr.toUpperCase());
            } catch (IllegalArgumentException ex) {
                log.warn("Unknown role '{}' in JWT for user id={}", roleStr, id);
                chain.doFilter(request, response);
                return;
            }

            // Build authenticated principal and set into context
            final UserDTO principal = new UserDTO(id, name, email, role);
            SecurityContextHolder.getContext().setAuthentication(buildAuthentication(principal, role));

        } catch (Exception ex) {
            // Any error -> log and continue without authentication
            log.warn("Invalid JWT token: {}", ex.getMessage());
            // (Optionally could short-circuit with 401)
        }

        chain.doFilter(request, response);
    }

    // -------------------- Helpers --------------------

    // Extracts the bearer token from the Authorization header
    private static String getBearerToken(String authHeader) {
        if (StringUtils.hasText(authHeader) && authHeader.startsWith(BEARER_PREFIX)) {
            return authHeader.substring(BEARER_PREFIX.length()).trim();
        }
        return null;
    }

    // Builds the authentication object from user details
    private static UsernamePasswordAuthenticationToken buildAuthentication(UserDTO principal, UserRole role) {
        return new UsernamePasswordAuthenticationToken(
                principal,
                null,
                List.of(new SimpleGrantedAuthority(role.asAuthority()))
        );
    }

    // Writes a JSON error response for deactivated accounts
    private static void writeJsonError(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write("{\"message\":\"Your account is deactivated\"}");
        response.getWriter().flush();
    }

    /**
     * Determines whether the request should be treated as public.
     * Adjust this to match your `permitAll()` paths in {@link com.university.placementsystem.config.SecurityConfig}.
     */
    private boolean isPublic(HttpServletRequest request) {
        final String path = request.getRequestURI();
        return path.startsWith("/api/auth/");
    }
}
