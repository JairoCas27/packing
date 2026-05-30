package com.urbanpark.parking.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CondominioLoginRequest {
    @NotNull
    private Long tenantId;
    @NotBlank @Email
    private String email;
    @NotBlank
    private String password;
}
