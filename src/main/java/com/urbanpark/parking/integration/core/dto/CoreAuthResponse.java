package com.urbanpark.parking.integration.core.dto;

import lombok.Data;

@Data
public class CoreAuthResponse {
    private String accessToken;
    private String tokenType;
    private Long expiresIn;
    private Long expiresAt;
    private String refreshToken;
    private CoreUsuarioResponse user;
}
