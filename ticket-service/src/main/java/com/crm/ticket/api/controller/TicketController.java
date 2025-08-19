package com.crm.ticket.api.controller;

import com.crm.ticket.api.dto.request.CreateTicketEmployeeRequest;
import com.crm.ticket.api.dto.request.CreateTicketPublicRequest;
import com.crm.ticket.api.dto.request.ReassignTicketRequest;
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
import reactor.core.publisher.Flux;

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

    /**
     * Bir çalışanın, kendi departman havuzundaki bir ticket'ı üstlenmesini sağlar.
     * @param ticketId Üstlenilecek ticket'ın ID'si.
     * @param employeeIdStr Token'dan gelen ve ticket'ı üstlenen çalışanın ID'si.
     * @return Güncellenmiş TicketResponse.
     */
    @PostMapping("/{ticketId}/assignments/claim")
    @PreAuthorize("hasRole('USER')")
    public Mono<TicketResponse> claimTicket(
            @PathVariable Long ticketId,
            @AuthenticationPrincipal String employeeIdStr) {

        Long employeeId = Long.valueOf(employeeIdStr);
        return ticketService.claimTicket(ticketId, employeeId);
    }

    /**
     * Bir çalışanın, kendisine atanmış olan bir ticket'ı kapatmasını sağlar.
     * @param ticketId Kapatılacak ticket'ın ID'si.
     * @param employeeIdStr Token'dan gelen ve ticket'ı kapatan çalışanın ID'si.
     * @return Güncellenmiş TicketResponse.
     */
    @PostMapping("/{ticketId}/assignments/close")
    @PreAuthorize("hasRole('USER')")
    public Mono<TicketResponse> closeTicket(
            @PathVariable Long ticketId,
            @AuthenticationPrincipal String employeeIdStr) {

        Long employeeId = Long.valueOf(employeeIdStr);
        return ticketService.closeTicket(ticketId, employeeId);
    }

    /**
     * Bir çalışanın, kendisine atanmış olan bir ticket'ı başka bir departmanın havuzuna yönlendirmesini sağlar.
     * @param ticketId Yönlendirilecek ticket'ın ID'si.
     * @param request Yeni departman ID'sini içeren request body.
     * @param employeeIdStr Token'dan gelen ve işlemi yapan çalışanın ID'si.
     * @return Güncellenmiş TicketResponse.
     */
    @PostMapping("/{ticketId}/assignments/reassign")
    @PreAuthorize("hasRole('USER')")
    public Mono<TicketResponse> reassignTicket(
            @PathVariable Long ticketId,
            @Valid @RequestBody ReassignTicketRequest request,
            @AuthenticationPrincipal String employeeIdStr) {

        Long employeeId = Long.valueOf(employeeIdStr);
        return ticketService.reassignTicket(ticketId, request.getNewDepartmentId(), employeeId);
    }

    @GetMapping("/pool")
    @PreAuthorize("hasRole('USER')")
    public Flux<TicketResponse> getDepartmentPool(@AuthenticationPrincipal String employeeIdStr) {
        Long employeeId = Long.valueOf(employeeIdStr);
        return ticketService.getDepartmentPoolTickets(employeeId);
    }
}