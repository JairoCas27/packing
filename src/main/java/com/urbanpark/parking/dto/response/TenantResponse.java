package com.urbanpark.parking.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class TenantResponse {
    private Long id;
    private String name;
    private Long externalCondominioId;
    private String apiBaseUrl;
    private boolean active;
    private String country;
    private String city;
    private String address;
    private LocalDateTime createdAt;
}
