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

@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 1) // Se ejecuta muy temprano en la cadena
@Slf4j
public class RateLimitingFilter extends OncePerRequestFilter {

    // Cache para endpoints sensibles (Login)
    private final Map<String, Bucket> loginCache = new ConcurrentHashMap<>();

    // Cache para el resto de la API (General)
    private final Map<String, Bucket> generalCache = new ConcurrentHashMap<>();

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // 1. Obtener la IP Real (Soporte para Proxy/Cloudflare/Nginx)
        String clientIp = getClientIp(request);
        String path = request.getRequestURI();
        Bucket bucket;

        // 2. Seleccionar la estrategia según la ruta
        if (path.startsWith("/api/auth/login")) {
            // Estrategia estricta para autenticación
            bucket = loginCache.computeIfAbsent(clientIp, k -> createLoginBucket());
        } else {
            // Estrategia relajada para navegación general
            bucket = generalCache.computeIfAbsent(clientIp, k -> createGeneralBucket());
        }

        // 3. Intentar consumir un token
        if (bucket.tryConsume(1)) {
            // ✅ Tiene token: Pasa al siguiente filtro
            filterChain.doFilter(request, response);
        } else {
            // ⛔ No tiene token: Bloqueo y respuesta 429
            log.warn("Rate limit excedido para IP: {} en ruta: {}", clientIp, path);

            response.setStatus(429); // Too Many Requests
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");

            String mensaje = path.contains("login")
                    ? "{\"error\": \"Demasiados intentos de inicio de sesión. Por seguridad, espera 1 minuto.\"}"
                    : "{\"error\": \"Has realizado muchas peticiones muy rápido. Espera un momento.\"}";

            response.getWriter().write(mensaje);
        }
    }

    /**
     * Extrae la IP real del cliente, manejando casos de Proxies (X-Forwarded-For).
     * Si no hay proxy, usa la IP directa de la conexión.
     */
    private String getClientIp(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null || xfHeader.isEmpty()) {
            return request.getRemoteAddr();
        }
        // El header puede venir como "ip_cliente, proxy1, proxy2". Tomamos la primera.
        return xfHeader.split(",")[0].trim();
    }

    /**
     * BUCKET DE LOGIN (Anti Fuerza Bruta + Soporte Aula)
     * Capacidad: 60 peticiones.
     * Recarga: 60 peticiones cada 1 minuto.
     * Explicación: Permite que ~30 alumnos se logueen simultáneamente desde el WiFi del colegio,
     * pero detiene scripts que prueben miles de contraseñas.
     */
    private Bucket createLoginBucket() {
        Bandwidth limit = Bandwidth.classic(60, Refill.greedy(60, Duration.ofMinutes(1)));
        return Bucket.builder().addLimit(limit).build();
    }

    /**
     * BUCKET GENERAL (Anti DDoS + Navegación Fluida)
     * Capacidad: 300 peticiones.
     * Recarga: 300 peticiones cada 1 minuto.
     * Explicación: Permite navegación intensiva en Angular sin bloquear al usuario legítimo,
     * pero protege al servidor de saturación masiva.
     */
    private Bucket createGeneralBucket() {
        Bandwidth limit = Bandwidth.classic(300, Refill.greedy(300, Duration.ofMinutes(1)));
        return Bucket.builder().addLimit(limit).build();
    }
}