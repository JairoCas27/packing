package com.urbanpark.parking.dto.request;

import com.urbanpark.parking.domain.enums.SpaceType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateParkingSpaceRequest {
    @NotBlank
    private String code;
    private String zone;
    @NotNull
    private SpaceType type;
}
