package com.urbanpark.parking.dto.request;

import lombok.Data;

import java.time.LocalTime;

@Data
public class UpdateParkingRuleRequest {
    private Integer maxVehiclesPerUser;
    private Integer maxVisitorsPerDay;
    private LocalTime accessStartTime;
    private LocalTime accessEndTime;
    private Boolean allowVisitorsOutsideHours;
    private Integer maxAutos;
    private Integer maxMotos;
}
