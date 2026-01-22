package com.backend.backendpreu.auth.security;

import com.backend.backendpreu.users.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.function.Function;

/**
 * Service for generating, validating, and extracting information from JSON Web Tokens (JWT).
 * <p>
 * This class handles the cryptographic operations and claims processing related to JWTs
 * used for authentication in the application.
 */
@Service
public class JwtService {

    /**
     * The expiration time for JWT tokens in milliseconds.
     */
    private final long expiration;
    /**
     * The secret key used for signing and verifying JWT tokens.
     */
    private final SecretKey key;

    /**
     * Constructs a {@code JwtService} with the specified secret key and expiration time.
     *
     * @param secretKey The base64-encoded string representation of the secret key.
     * @param expiration The expiration time for tokens in milliseconds.
     */
    public JwtService(
            @Value("${application.security.jwt.secret-key}") String secretKey,
            @Value("${application.security.jwt.expiration}") long expiration
    ) {
        this.expiration = expiration;
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Generates a new JWT token for a given user.
     * The token includes the user's email as the subject,
     * current issue time, and an expiration time.
     *
     * @param user The {@link User} for whom the token is generated.
     * @return A signed JWT token string.
     */
    public String generateToken(User user) {
        return Jwts.builder()
                .setSubject(user.getEmail())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(key)
                .compact();
    }

    /**
     * Extracts the username (subject) from a JWT token.
     *
     * @param token The JWT token string.
     * @return The username (email) extracted from the token's subject claim.
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extracts a specific claim from a JWT token using a claims resolver function.
     *
     * @param token The JWT token string.
     * @param claimsResolver A function to resolve the desired claim from the {@link Claims}.
     * @param <T> The type of the claim to be extracted.
     * @return The extracted claim.
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Extracts all claims from a JWT token.
     *
     * @param token The JWT token string.
     * @return A {@link Claims} object containing all claims from the token.
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Validates a JWT token against a user's details.
     * Checks if the username in the token matches the user details' username
     * and if the token has not expired.
     *
     * @param token The JWT token string.
     * @param userDetails The {@link UserDetails} to validate against.
     * @return {@code true} if the token is valid for the given user, {@code false} otherwise.
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    /**
     * Checks if a JWT token has expired.
     *
     * @param token The JWT token string.
     * @return {@code true} if the token's expiration date is before the current date, {@code false} otherwise.
     */
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Extracts the expiration date from a JWT token.
     *
     * @param token The JWT token string.
     * @return The {@link Date} representing the token's expiration.
     */
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
}
