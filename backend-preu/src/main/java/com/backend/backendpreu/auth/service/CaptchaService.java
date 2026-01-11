package com.backend.backendpreu.auth.service;

import com.backend.backendpreu.auth.dto.CaptchaResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@Service
@RequiredArgsConstructor
@Slf4j // Agregamos logs para ver qué pasa
public class CaptchaService {

    @Value("${google.recaptcha.secret}")
    private String recaptchaSecret;

    @Value("${google.recaptcha.verify-url}")
    private String recaptchaVerifyUrl;

    private final RestTemplate restTemplate;

    public boolean verify(String token) {
        // Elimina o comenta este bloque en PRODUCCIÓN

        if ("PRUEBA".equals(token)) {
            log.warn("BYPASS DE CAPTCHA DETECTADO: Usando token de prueba.");
            return true;
        }

        if (token == null || token.isBlank()) {
            log.error("Captcha token vacío o nulo.");
            return false;
        }

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("secret", recaptchaSecret);
        params.add("response", token);

        try {

            CaptchaResponse apiResponse = restTemplate.postForObject(
                    recaptchaVerifyUrl,
                    params,
                    CaptchaResponse.class
            );

            if (apiResponse != null && apiResponse.isSuccess()) {
                log.info("Captcha verificado exitosamente con Google.");
                return true;
            } else {
                log.warn("Google rechazó el captcha. Respuesta: {}", apiResponse);
                return false;
            }

        } catch (Exception e) {
            log.error("Error al conectar con Google Recaptcha", e);
            return false;
        }
    }
}