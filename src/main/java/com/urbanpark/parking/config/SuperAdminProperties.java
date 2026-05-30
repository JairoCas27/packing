package com.urbanpark.parking.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "app.saas.superadmin")
@Getter
@Setter
public class SuperAdminProperties {
    private String email;
    private String password;
}
