package com.crm.ticket.infrastructure.client;

import com.crm.ticket.infrastructure.client.dto.DepartmentResponse;
import com.crm.ticket.infrastructure.client.dto.PersonResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders; // HttpHeaders import'u eklenmeli
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class DepartmentClient {

    private final WebClient departmentWebClient;

    // Metoda String token parametresi eklendi
    public Mono<PersonResponse> getPersonById(Long personId, String token) {
        return departmentWebClient.get()
                .uri("/api/persons/{id}", personId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token) // Token başlığa eklendi
                .retrieve()
                .bodyToMono(PersonResponse.class)
                .onErrorResume(e -> Mono.empty()); // Hata durumunda boş dönerek fallback'i tetikler
    }

    // Metoda String token parametresi eklendi
    public Mono<DepartmentResponse> getDepartmentById(Long departmentId, String token) {
        return departmentWebClient.get()
                .uri("/api/departments/{id}", departmentId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token) // Token başlığa eklendi
                .retrieve()
                .bodyToMono(DepartmentResponse.class)
                .onErrorResume(e -> Mono.empty()); // Hata durumunda boş dönerek fallback'i tetikler
    }
}