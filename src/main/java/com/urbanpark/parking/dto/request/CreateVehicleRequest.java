package com.urbanpark.parking.dto.request;

import com.urbanpark.parking.domain.enums.VehicleType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateVehicleRequest {
    @NotBlank
    private String placa;
    private String marca;
    private String modelo;
    private String color;
    @NotNull
    private VehicleType tipo;
}
