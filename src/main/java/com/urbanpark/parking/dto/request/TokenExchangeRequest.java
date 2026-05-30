package com.urbanpark.parking.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TokenExchangeRequest {
    @NotNull
    private Long tenantId;
    @NotBlank
    private String condominioToken;
}
