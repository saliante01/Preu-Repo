package com.backend.backendpreu.auth.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;


import java.util.List;

/**
 * Main Spring Security configuration class for the application.
 * <p>
 * This class defines the security filter chain, CORS configuration,
 * and authorization rules for different endpoints. It uses JWT for stateless authentication.
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity(securedEnabled = true) // Enables @Secured annotation for method-level security
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    /**
     * Configures the security filter chain.
     * <ul>
     *     <li>Enables CORS with a specific configuration.</li>
     *     <li>Disables CSRF protection (common for stateless REST APIs).</li>
     *     <li>Authorizes requests:
     *         <ul>
     *             <li>Permits all access to `/api/auth/login` and `/h2-console/**`.</li>
     *             <li>Requires authentication for all other requests.</li>
     *         </ul>
     *     </li>
     *     <li>Handles authentication exceptions using {@link JwtAuthenticationEntryPoint}.</li>
     *     <li>Sets session management to stateless (no HTTP session is created or used).</li>
     *     <li>Disables frame options for H2 console compatibility.</li>
     *     <li>Adds the custom {@link JwtAuthenticationFilter} before {@link UsernamePasswordAuthenticationFilter}.</li>
     * </ul>
     *
     * @param http The {@link HttpSecurity} object to configure.
     * @return A configured {@link SecurityFilterChain}.
     * @throws Exception If an error occurs during configuration.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/login", "/h2-console/**").permitAll()
                        .anyRequest().authenticated()
                )
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .headers(headers -> headers.frameOptions(frame -> frame.disable())) // Required for H2 console frame
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Configures CORS (Cross-Origin Resource Sharing) for the application.
     * <p>
     * Allows requests from `http://localhost:4200` with specified HTTP methods and headers.
     * Credentials (like cookies) are allowed, and `X-Request-ID` header is exposed.
     *
     * @return A {@link CorsConfigurationSource} bean.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:4200")); // Adjust for production origin(s)
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS","PATCH"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type", "X-Request-ID"));
        configuration.setAllowCredentials(true);
        configuration.setExposedHeaders(List.of("X-Request-ID")); // Expose custom headers
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // Apply this CORS config to all paths
        return source;
    }
}
