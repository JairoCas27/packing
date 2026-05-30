package com.urbanpark.parking.dto.response;

import com.urbanpark.parking.domain.enums.VisitorStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class VisitorResponse {
    private Long id;
    private String placa;
    private String nombre;
    private String documento;
    private Long hostUserId;
    private LocalDateTime validFrom;
    private LocalDateTime validUntil;
    private VisitorStatus status;
}
