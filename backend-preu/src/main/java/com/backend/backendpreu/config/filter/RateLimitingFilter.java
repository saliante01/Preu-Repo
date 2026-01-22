package com.backend.backendpreu.config.filter;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A Spring Boot filter that implements IP-based rate limiting using the Bucket4j library.
 * It applies different rate limiting strategies based on the request path to protect
 * sensitive endpoints (like login) from brute-force attacks and the entire API from DDoS.
 * <p>
 * This filter runs with high precedence to intercept requests early in the filter chain.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 1)
@Slf4j
public class RateLimitingFilter extends OncePerRequestFilter {

    /**
     * Cache for sensitive endpoints (e.g., Login) to store {@link Bucket} instances per client IP.
     */
    private final Map<String, Bucket> loginCache = new ConcurrentHashMap<>();

    /**
     * Cache for general API endpoints to store {@link Bucket} instances per client IP.
     */
    private final Map<String, Bucket> generalCache = new ConcurrentHashMap<>();

    /**
     * Performs the internal filtering logic for each request.
     * <p>
     * It extracts the client IP, selects a rate limiting strategy (login vs. general)
     * based on the request URI, and attempts to consume a token from the corresponding bucket.
     * If a token is consumed, the request proceeds; otherwise, a 429 Too Many Requests response is sent.
     *
     * @param request The {@link HttpServletRequest} for the current request.
     * @param response The {@link HttpServletResponse} for the current response.
     * @param filterChain The {@link FilterChain} to proceed with if the rate limit is not exceeded.
     * @throws ServletException If a servlet-specific error occurs.
     * @throws IOException If an input or output error occurs.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // 1. Get the Real IP (Support for Proxy/Cloudflare/Nginx)
        String clientIp = getClientIp(request);
        String path = request.getRequestURI();
        Bucket bucket;

        // 2. Select the strategy based on the path
        if (path.startsWith("/api/auth/login")) {
            // Strict strategy for authentication
            bucket = loginCache.computeIfAbsent(clientIp, k -> createLoginBucket());
        } else {
            // Relaxed strategy for general navigation
            bucket = generalCache.computeIfAbsent(clientIp, k -> createGeneralBucket());
        }

        // 3. Try to consume a token
        if (bucket.tryConsume(1)) {
            // ✅ Token available: Proceed to the next filter
            filterChain.doFilter(request, response);
        } else {
            // ⛔ No token: Block and send 429 response
            log.warn("Rate limit exceeded for IP: {} on path: {}", clientIp, path);

            response.setStatus(429); // Too Many Requests
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");

            String message = path.contains("login")
                    ? "{\"error\": \"Too many login attempts. For security reasons, please wait 1 minute.\"}"
                    : "{\"error\": \"You have made too many requests too quickly. Please wait a moment.\"}";

            response.getWriter().write(message);
        }
    }

    /**
     * Extracts the real client IP address, handling cases with proxies (e.g., X-Forwarded-For).
     * If no proxy header is present, it uses the direct connection's remote address.
     *
     * @param request The {@link HttpServletRequest} to extract the IP from.
     * @return The client's IP address as a {@link String}.
     */
    private String getClientIp(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null || xfHeader.isEmpty()) {
            return request.getRemoteAddr();
        }
        // The header can come as "client_ip, proxy1, proxy2". We take the first one.
        return xfHeader.split(",")[0].trim();
    }

    /**
     * Creates a {@link Bucket} for login endpoints.
     * <p>
     * Capacity: 60 requests.
     * Refill: 60 requests every 1 minute.
     * This allows approximately 30 users to log in simultaneously from a shared network
     * but prevents scripts from attempting thousands of passwords.
     *
     * @return A configured {@link Bucket} instance for login rate limiting.
     */
    private Bucket createLoginBucket() {
        Bandwidth limit = Bandwidth.classic(60, Refill.greedy(60, Duration.ofMinutes(1)));
        return Bucket.builder().addLimit(limit).build();
    }

    /**
     * Creates a {@link Bucket} for general API navigation.
     * <p>
     * Capacity: 300 requests.
     * Refill: 300 requests every 1 minute.
     * This allows intensive browsing without blocking legitimate users
     * but protects the server from massive saturation.
     *
     * @return A configured {@link Bucket} instance for general API rate limiting.
     */
    private Bucket createGeneralBucket() {
        Bandwidth limit = Bandwidth.classic(300, Refill.greedy(300, Duration.ofMinutes(1)));
        return Bucket.builder().addLimit(limit).build();
    }
}
