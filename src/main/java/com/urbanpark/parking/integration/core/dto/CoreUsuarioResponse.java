package com.urbanpark.parking.integration.core.dto;

import lombok.Data;

@Data
public class CoreUsuarioResponse {
    private String id;
    private String nombres;
    private String apellidos;
    private String correo;
    private String telefono;
    private String rol;
    private Boolean activo;
    private Long condominioId;
    private Boolean correoVerificado;
}
