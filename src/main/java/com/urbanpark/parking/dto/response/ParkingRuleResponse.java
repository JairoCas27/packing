package com.urbanpark.parking.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalTime;

@Data
@Builder
public class ParkingRuleResponse {
    private Long id;
    private int maxVehiclesPerUser;
    private int maxVisitorsPerDay;
    private LocalTime accessStartTime;
    private LocalTime accessEndTime;
    private boolean allowVisitorsOutsideHours;
    private int maxAutos;
    private int maxMotos;
}
