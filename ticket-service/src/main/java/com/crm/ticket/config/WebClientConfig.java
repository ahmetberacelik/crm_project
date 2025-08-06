package com.crm.ticket.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    // Bu değer application-dev.properties dosyasından okunur.
    // Docker Compose'da .env dosyasındaki DEPARTMENT_SERVICE_URL'den gelir.
    @Value("${department.base-url}")
    private String departmentServiceUrl;

    /**
     * department-service'e istek atmak için kullanılacak olan WebClient bean'ini oluşturur.
     * Bu bean, Spring tarafından yönetilecek ve ihtiyaç duyan diğer bileşenlere
     * (örneğin DepartmentClient) otomatik olarak enjekte edilecektir.
     */
    @Bean
    public WebClient departmentWebClient() {
        return WebClient.builder()
                .baseUrl(departmentServiceUrl)
                .build();
    }
}
