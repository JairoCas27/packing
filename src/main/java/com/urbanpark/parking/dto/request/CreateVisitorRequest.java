package com.urbanpark.parking.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CreateVisitorRequest {
    @NotBlank
    private String placa;
    private String nombre;
    private String documento;
    @NotNull
    private LocalDateTime validFrom;
    @NotNull
    private LocalDateTime validUntil;
}
