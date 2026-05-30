package com.urbanpark.parking.dto.request;

import com.urbanpark.parking.domain.enums.SpaceStatus;
import com.urbanpark.parking.domain.enums.SpaceType;
import lombok.Data;

@Data
public class UpdateParkingSpaceRequest {
    private String code;
    private String zone;
    private SpaceType type;
    private SpaceStatus status;
    private Long assignedUserId;
}
