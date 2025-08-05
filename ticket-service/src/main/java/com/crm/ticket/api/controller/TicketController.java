package com.crm.ticket.api.controller;

import com.crm.ticket.api.dto.request.CreateTicketEmployeeRequest;
import com.crm.ticket.api.dto.request.CreateTicketPublicRequest;
import com.crm.ticket.api.dto.response.TicketResponse;
import com.crm.ticket.service.TicketService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/tickets")
@RequiredArgsConstructor
public class TicketController {

    private final TicketService ticketService;

    /**
     * Halka açık, kimlik doğrulaması gerektirmeyen ticket oluşturma endpoint'i.
     */
    @PostMapping("/public")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<TicketResponse> createPublicTicket(@Valid @RequestBody CreateTicketPublicRequest request) {
        return ticketService.createTicketFromCustomer(request);
    }

    /**
     * Sadece kimliği doğrulanmış ve 'ROLE_USER' yetkisine sahip çalışanlar için ticket oluşturma endpoint'i.
     * @param jwt İstekle gelen ve doğrulanan JWT. Creator ID'yi buradan alacağız.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('USER')")
    public Mono<TicketResponse> createEmployeeTicket(@Valid @RequestBody CreateTicketEmployeeRequest request, @AuthenticationPrincipal Jwt jwt) {
        Long creatorId = Long.valueOf(jwt.getSubject()); // JWT'nin 'sub' (subject) alanı user ID'yi tutar.
        return ticketService.createTicketFromEmployee(request, creatorId);
    }

    /**
     * ID'ye göre bir ticket'ın detaylarını getirir. Sadece çalışanlar erişebilir.
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public Mono<TicketResponse> getTicketById(@PathVariable Long id) {
        return ticketService.getTicketById(id);
    }
}
