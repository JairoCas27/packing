package com.urbanpark.parking.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateTenantRequest {
    @NotBlank
    private String name;
    @NotNull
    private Long externalCondominioId;
    private String apiBaseUrl;
    private String apiKey;
    private String country;
    private String city;
    private String address;
}
