package com.urbanpark.parking.dto.request;

import com.urbanpark.parking.domain.enums.IncidentType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateIncidentRequest {
    @NotBlank
    private String description;
    @NotNull
    private IncidentType type;
    private Long accessEventId;
}
