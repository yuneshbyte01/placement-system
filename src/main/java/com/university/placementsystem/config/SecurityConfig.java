package com.university.placementsystem.config;

import com.university.placementsystem.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security configuration for the Placement System.
 *
 * <p>Configures:</p>
 * <ul>
 *     <li>JWT authentication filter</li>
 *     <li>Role-based access control for endpoints</li>
 *     <li>Password hashing with BCrypt</li>
 * </ul>
 */
@Configuration
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    /**
     * Defines the application security filter chain.
     *
     * <p>Key settings:</p>
     * <ul>
     *     <li>Disable CSRF for REST APIs</li>
     *     <li>Permit all requests to {@code /api/auth/**} (login/registration)</li>
     *     <li>Restrict access to endpoints by role</li>
     *     <li>Add JWT filter before {@link UsernamePasswordAuthenticationFilter}</li>
     * </ul>
     *
     * @param http the {@link HttpSecurity} builder
     * @return a configured {@link SecurityFilterChain}
     * @throws Exception if an error occurs during configuration
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Disable CSRF (stateless REST API)
                .csrf(AbstractHttpConfigurer::disable)

                // Configure endpoint authorization
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll()                     // public: login, register
                        .requestMatchers("/api/student/**").hasRole("STUDENT")           // only STUDENT role
                        .requestMatchers("/api/organization/**").hasRole("ORGANIZATION") // only ORGANIZATION role
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")               // only ADMIN role
                        .requestMatchers("index.html", "/pages/*","/css/*", "/js/*").permitAll() // static resources
                        .anyRequest().authenticated()                                   // all other endpoints need login
                )

                // Add JWT authentication filter before default username-password filter
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * BCrypt password encoder for secure password hashing.
     *
     * @return a {@link PasswordEncoder} instance
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
