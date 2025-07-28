package com.crm.ticket.api.controller;

import com.crm.ticket.api.dto.request.AssignTicketRequest;
import com.crm.ticket.api.dto.request.CreateTicketRequest;
import com.crm.ticket.api.dto.request.UpdateStatusRequest;
import com.crm.ticket.api.dto.response.TicketResponse;
import com.crm.ticket.service.TicketService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {

    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('CUSTOMER')")
    public Mono<TicketResponse> createTicket(@Valid @RequestBody CreateTicketRequest request) {
        return ticketService.createTicket(request);
    }

    @PutMapping("/assign")
    @PreAuthorize("hasAnyAuthority('SUPPORT_AGENT','ADMIN')")
    public Mono<TicketResponse> assignTicket(@Valid @RequestBody AssignTicketRequest request) {
        return ticketService.assignTicket(request);
    }

    @PutMapping("/status")
    @PreAuthorize("hasAnyAuthority('SUPPORT_AGENT','ADMIN')")
    public Mono<TicketResponse> updateStatus(@Valid @RequestBody UpdateStatusRequest request) {
        return ticketService.updateStatus(request);
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('CUSTOMER','SUPPORT_AGENT','ADMIN')")
    public Flux<TicketResponse> getTicketsByCustomer(@RequestParam("customerId") Long customerId) {
        return ticketService.findByCustomer(customerId);
    }
} 