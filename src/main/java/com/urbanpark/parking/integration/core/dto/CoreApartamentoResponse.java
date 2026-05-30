package com.urbanpark.parking.integration.core.dto;

import lombok.Data;

@Data
public class CoreApartamentoResponse {
    private Long id;
    private Integer numero;
    private Boolean derechoEstacionamiento;
    private Double metraje;
    private Long pisoId;
    private String propietarioId;
}
