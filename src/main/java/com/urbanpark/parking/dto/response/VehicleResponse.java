package com.urbanpark.parking.dto.response;

import com.urbanpark.parking.domain.enums.VehicleType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class VehicleResponse {
    private Long id;
    private String placa;
    private String marca;
    private String modelo;
    private String color;
    private VehicleType tipo;
    private Long ownerId;
    private Long parkingSpaceId;
    private boolean active;
}
