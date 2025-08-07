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

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('USER')")
    public Mono<TicketResponse> createEmployeeTicket(
            @Valid @RequestBody CreateTicketEmployeeRequest request,
            @AuthenticationPrincipal String username // DOĞRUSU: String bekleniyor
    ) {
        // Artık 'username' (veya ID), filtrenizin sağladığı String değerini içeriyor.
        Long creatorId = Long.valueOf(username);
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