package com.urbanpark.parking.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OccupancyReportResponse {
    private long totalSpaces;
    private long available;
    private long occupied;
    private long reserved;
    private long maintenance;
    private double occupancyRate;
}
