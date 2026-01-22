package com.backend.backendpreu.auth.service;

import com.backend.backendpreu.auth.dto.CaptchaResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

/**
 * Service class for verifying Google reCAPTCHA tokens.
 * Communicates with the Google reCAPTCHA API to validate user responses,
 * helping to protect against bots and automated attacks.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CaptchaService {

    /**
     * The secret key for Google reCAPTCHA, loaded from application properties.
     */
    @Value("${google.recaptcha.secret}")
    private String recaptchaSecret;

    /**
     * The URL for the Google reCAPTCHA verification endpoint, loaded from application properties.
     */
    @Value("${google.recaptcha.verify-url}")
    private String recaptchaVerifyUrl;

    private final RestTemplate restTemplate;

    /**
     * Verifies a reCAPTCHA token by sending it to Google's verification service.
     * Includes a bypass for testing environments using a "PRUEBA" token.
     *
     * @param token The reCAPTCHA token received from the client-side.
     * @return {@code true} if the token is successfully verified by Google (or the bypass is active), {@code false} otherwise.
     */
    public boolean verify(String token) {

        // BYPASS for testing purposes. Should be removed or secured in production.
        if ("PRUEBA".equals(token)) {
            log.warn("CAPTCHA BYPASS DETECTED: Using test token.");
            return true;
        }

        if (token == null || token.isBlank()) {
            log.error("Captcha token is empty or null.");
            return false;
        }

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("secret", recaptchaSecret);
        params.add("response", token);

        try {
            // Make a POST request to the Google reCAPTCHA verification URL
            CaptchaResponse apiResponse = restTemplate.postForObject(
                    recaptchaVerifyUrl,
                    params,
                    CaptchaResponse.class
            );

            if (apiResponse != null && apiResponse.isSuccess()) {
                log.info("Captcha successfully verified with Google.");
                return true;
            } else {
                log.warn("Google rejected the captcha. Response: {}", apiResponse);
                return false;
            }

        } catch (Exception e) {
            log.error("Error connecting to Google Recaptcha", e);
            return false;
        }
    }
}
