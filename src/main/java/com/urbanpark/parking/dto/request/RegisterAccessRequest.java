package com.urbanpark.parking.dto.request;

import com.urbanpark.parking.domain.enums.AccessMethod;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RegisterAccessRequest {
    @NotBlank
    private String placa;
    private AccessMethod method = AccessMethod.MANUAL;
    private boolean forceAllow;
    private String notes;
}
