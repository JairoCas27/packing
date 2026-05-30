package com.urbanpark.parking.integration.core.dto;

import lombok.Data;

@Data
public class CoreCondominioResponse {
    private Long id;
    private String nombre;
    private String pais;
    private String ciudad;
    private String direccion;
}
