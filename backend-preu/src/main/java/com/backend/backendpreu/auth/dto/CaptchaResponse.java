package com.backend.backendpreu.auth.dto;

import lombok.Data;

@Data
public class CaptchaResponse {
    private boolean success;
    private String challenge_ts;
    private String hostname;
}
