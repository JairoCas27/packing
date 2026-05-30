package com.urbanpark.parking.integration.core.dto;

import lombok.Data;

@Data
public class CoreConfiguracionResponse {
    private Long id;
    private Integer maxAutos;
    private Integer maxMotos;
    private Integer maxEstacionamientosPorApartamento;
    private Integer maxVehiculosPorPropietario;
    private Long condominioId;
}
