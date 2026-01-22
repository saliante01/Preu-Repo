package com.backend.backendpreu.auth.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Handles unauthorized access attempts in a Spring Security context.
 * <p>
 * This entry point is invoked when a user tries to access a secured REST endpoint
 * without proper authentication (e.g., missing or invalid JWT). It sends a 401 Unauthorized
 * response.
 */
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    /**
     * Commences an authentication scheme.
     * <p>
     * This method is called when an unauthenticated user attempts to access a protected resource.
     * It sends an HTTP 401 Unauthorized error response to the client.
     *
     * @param request The {@link HttpServletRequest} that resulted in an {@link AuthenticationException}.
     * @param response The {@link HttpServletResponse} to send the error response.
     * @param authException The {@link AuthenticationException} that caused the commencement.
     * @throws IOException If an input or output error occurs.
     * @throws ServletException If a servlet-specific error occurs.
     */
    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {

        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized: Missing or invalid token");
    }
}
