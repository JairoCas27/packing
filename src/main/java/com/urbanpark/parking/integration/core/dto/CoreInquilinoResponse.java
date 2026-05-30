package com.urbanpark.parking.integration.core.dto;

import lombok.Data;

@Data
public class CoreInquilinoResponse {
    private Long id;
    private String nombres;
    private String apellidos;
    private String dni;
    private Long apartamentoId;
}
