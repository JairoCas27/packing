package com.urbanpark.parking.integration.core.dto;

import lombok.Data;

import java.util.List;

@Data
public class CorePaginacionResponse<T> {
    private List<T> contenido;
    private Integer pagina;
    private Integer tamanio;
    private Long totalElementos;
    private Integer totalPaginas;
}
