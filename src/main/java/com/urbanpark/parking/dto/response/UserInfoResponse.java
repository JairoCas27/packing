package com.urbanpark.parking.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserInfoResponse {
    private String id;
    private String email;
    private String nombres;
    private String apellidos;
    private String role;
    private Long tenantId;
    private String tenantName;
    private boolean saasUser;
}
