package com.university.placementsystem.security;

import com.university.placementsystem.dto.UserDTO;
import com.university.placementsystem.entity.UserRole;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * JWT Authentication Filter.
 * <p>
 * Executes once per request to:
 * - Validate JWT tokens from the Authorization header
 * - Extract user details and role
 * - Set an authenticated {@link UserDTO} in the Spring SecurityContext
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");

        // Process only if a Bearer token is present
        if (StringUtils.hasText(authHeader) && authHeader.startsWith("Bearer ")) {
            final String token = authHeader.substring(7);

            try {
                final Long id = Long.parseLong(jwtUtil.extractId(token));
                final String email = jwtUtil.extractUsername(token);
                final String roleStr = jwtUtil.extractRole(token);
                final String name = jwtUtil.extractName(token);

                // Set authentication only if not already done
                if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    final UserRole role = UserRole.valueOf(roleStr.toUpperCase());
                    final UserDTO userDto = new UserDTO(id, name, email, role);

                    final UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    userDto,
                                    null,
                                    List.of(new SimpleGrantedAuthority(role.asAuthority()))
                            );

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }

            } catch (Exception ex) {
                logger.warn("Invalid JWT token: {}");
            }
        }

        // Continue the filter chain
        filterChain.doFilter(request, response);
    }
}
