package com.backend.backendpreu.config.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

/**
 * A Spring Boot filter that adds a unique request ID to each incoming request.
 * This ID is added to the SLF4J MDC (Mapped Diagnostic Context) for logging purposes
 * and also sent back to the client in the `X-Request-ID` HTTP header.
 * This helps in tracing requests across different services and logs.
 * <p>
 * This filter runs with the highest precedence, ensuring the request ID is available
 * from the very beginning of request processing.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
@Slf4j
public class RequestTrackingFilter extends OncePerRequestFilter {

    /**
     * The key used to store the request ID in the SLF4J MDC.
     */
    private static final String MDC_KEY = "requestId";
    /**
     * The name of the HTTP header used to transmit the request ID to the client.
     */
    private static final String HEADER_NAME = "X-Request-ID";

    /**
     * Performs the internal filtering logic for each request.
     * <p>
     * Generates a unique request ID, puts it into the MDC, sets it as an HTTP response header,
     * logs the start of the request, and then proceeds with the filter chain.
     * The request ID is removed from the MDC in a `finally` block to prevent leakage.
     *
     * @param request The {@link HttpServletRequest} for the current request.
     * @param response The {@link HttpServletResponse} for the current response.
     * @param filterChain The {@link FilterChain} to proceed with after processing the request.
     * @throws ServletException If a servlet-specific error occurs.
     * @throws IOException If an input or output error occurs.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String requestId = UUID.randomUUID().toString();

        try {
            MDC.put(MDC_KEY, requestId);
            response.setHeader(HEADER_NAME, requestId);
            log.info("Starting request to: {}", request.getRequestURI());
            filterChain.doFilter(request, response);

        } finally {
            // Always remove the requestId from MDC to prevent memory leaks or incorrect context for subsequent requests
            MDC.remove(MDC_KEY);
        }
    }
}
