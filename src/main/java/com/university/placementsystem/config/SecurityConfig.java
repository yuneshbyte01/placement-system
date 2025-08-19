package com.university.placementsystem.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Base Spring Security configuration.
 * - Permits all requests (for now).
 * - Provides a PasswordEncoder bean for hashing passwords.
 */
@Configuration
public class SecurityConfig {

    /**
     * Defines the base security filter chain.
     * Currently, permits all requests for development purposes.
     *
     * @param http HttpSecurity configuration
     * @return SecurityFilterChain
     * @throws Exception in case of config errors
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)   // Disable CSRF for APIs
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll()  // Allow all endpoints (no authentication yet)
                );
        return http.build();
    }

    /**
     * Password encoder bean using BCrypt.
     * Will be used for hashing and verifying user passwords.
     *
     * @return PasswordEncoder instance
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
