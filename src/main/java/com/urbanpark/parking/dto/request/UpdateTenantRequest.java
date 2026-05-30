package com.urbanpark.parking.dto.request;

import lombok.Data;

@Data
public class UpdateTenantRequest {
    private String name;
    private String apiBaseUrl;
    private String apiKey;
    private Boolean active;
    private String country;
    private String city;
    private String address;
}
