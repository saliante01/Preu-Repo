package com.backend.backendpreu.auth.dto;

import lombok.Data;

/**
 * DTO for the response received from the Google reCAPTCHA verification API.
 * Contains information about the success of the captcha verification.
 */
@Data
public class CaptchaResponse {
    /**
     * Indicates whether the reCAPTCHA verification was successful.
     * True if the user is verified as human, false otherwise.
     */
    private boolean success;
    /**
     * Timestamp of the challenge load (ISO format yyyy-MM-dd'T'HH:mm:ssZZZ).
     */
    private String challenge_ts;
    /**
     * The hostname of the site where the reCAPTCHA was solved.
     */
    private String hostname;
}
