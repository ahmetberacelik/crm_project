package com.crm.user.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Value("${DEPARTMENT_SERVICE_URL:http://department-service:8082}")
    private String departmentBaseUrl;

    @Bean
    public WebClient departmentWebClient(WebClient.Builder builder) {
        return builder.baseUrl(departmentBaseUrl).build();
    }
}
