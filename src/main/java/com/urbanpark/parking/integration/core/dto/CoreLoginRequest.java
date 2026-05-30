package com.urbanpark.parking.integration.core.dto;

import lombok.Data;

@Data
public class CoreLoginRequest {
    private String email;
    private String password;
}
