package com.urbanpark.parking.dto.response;

import com.urbanpark.parking.domain.enums.SpaceStatus;
import com.urbanpark.parking.domain.enums.SpaceType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ParkingSpaceResponse {
    private Long id;
    private String code;
    private String zone;
    private SpaceType type;
    private SpaceStatus status;
    private Long assignedUserId;
}
