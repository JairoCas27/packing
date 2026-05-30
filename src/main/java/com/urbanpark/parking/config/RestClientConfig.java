package com.urbanpark.parking.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Bean
    public RestClient restClient() {
        return RestClient.builder().build();
    }

    @Bean
    public String defaultCoreApiUrl(@Value("${core.api.url}") String coreApiUrl) {
        return coreApiUrl;
    }
}
