package com.urbanpark.parking.integration.core.dto;

import lombok.Data;

@Data
public class CoreVehiculoResponse {
    private Long id;
    private String marca;
    private String color;
    private String modelo;
    private String placa;
    private String tipo;
    private String propietarioId;
    private Long inquilinoId;
}
