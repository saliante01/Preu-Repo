package com.backend.backendpreu.auth.security;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Custom Spring Security filter to intercept incoming requests and validate JWT tokens.
 * <p>
 * This filter attempts to extract a JWT from the 'Authorization' header or an 'accessCookie' cookie.
 * If a valid token is found, it authenticates the user and sets the security context.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    /**
     * Performs the actual filtering logic for each request.
     * <p>
     * It checks for a JWT in the Authorization header or a specific cookie.
     * If a valid token is present, it authenticates the user and sets the Spring Security context.
     *
     * @param request The {@link HttpServletRequest} for the current request.
     * @param response The {@link HttpServletResponse} for the current response.
     * @param filterChain The {@link FilterChain} to proceed with if authentication is successful or no token is found.
     * @throws ServletException If a servlet-specific error occurs.
     * @throws IOException If an input or output error occurs.
     */
    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        String jwt = null;
        final String authHeader = request.getHeader("Authorization");

        // 1. Attempt A: Look in the "Authorization: Bearer ..." Header
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            jwt = authHeader.substring(7);
        }
        // 2. Attempt B: If not in the Header, look in the COOKIES
        else if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("accessCookie".equals(cookie.getName())) { // The name defined in AuthController
                    jwt = cookie.getValue();
                    log.info("Token found in accessCookie");
                    break;
                }
            }
        }

        // If no JWT is found, proceed with the filter chain without authentication
        if (jwt == null) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            final String userEmail = jwtService.extractUsername(jwt);

            // If username is extracted and no authentication is currently set in the context
            if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);

                // If the token is valid for the user details
                if (jwtService.isTokenValid(jwt, userDetails)) {
                    // Create an authentication token
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );
                    // Set authentication details
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    // Set the authentication in the security context
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (ExpiredJwtException e) {
            log.warn("Expired token: {}", e.getMessage());
        } catch (JwtException e) {
            log.error("Invalid or malformed token: {}", e.getMessage());
        } catch (Exception e) {
            log.error("Error processing the JWT token", e);
        }

        filterChain.doFilter(request, response);
    }
}
