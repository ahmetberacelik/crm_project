package com.crm.ticket.infrastructure.client;

import com.crm.ticket.infrastructure.client.dto.DepartmentResponse;
import com.crm.ticket.infrastructure.client.dto.PersonResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class DepartmentClient {

    private final WebClient departmentWebClient;

    public Mono<PersonResponse> getPersonById(Long personId) {
        return departmentWebClient.get()
                .uri("/api/persons/{id}", personId)
                .retrieve()
                .bodyToMono(PersonResponse.class)
                .onErrorResume(e -> Mono.empty());
    }

    public Mono<DepartmentResponse> getDepartmentById(Long departmentId) {
        return departmentWebClient.get()
                .uri("/api/departments/{id}", departmentId)
                .retrieve()
                .bodyToMono(DepartmentResponse.class)
                .onErrorResume(e -> Mono.empty());
    }
}
