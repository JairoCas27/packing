package com.urbanpark.parking.dto.response;

import com.urbanpark.parking.domain.enums.*;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class AccessEventResponse {
    private Long id;
    private String placa;
    private AccessType type;
    private AccessMethod method;
    private AccessStatus status;
    private Long vehicleId;
    private Long visitorId;
    private Long parkingUserId;
    private Long parkingSpaceId;
    private String denialReason;
    private LocalDateTime entryTimestamp;
    private LocalDateTime exitTimestamp;
    private LocalDateTime createdAt;
}
