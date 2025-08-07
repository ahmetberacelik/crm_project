package com.crm.ticket.service;

import com.crm.ticket.api.dto.request.CreateTicketEmployeeRequest;
import com.crm.ticket.api.dto.request.CreateTicketPublicRequest;
import com.crm.ticket.api.dto.response.TicketResponse;
import reactor.core.publisher.Mono;

// Not: Güvenlik için Principal nesnesini daha sonra ekleyeceğiz.
public interface TicketService {

    /**
     * Dışarıdan bir müşteri tarafından yeni bir ticket oluşturur.
     * @param request Müşteri ve ticket bilgileri.
     * @return Oluşturulan ticket'ın detayları.
     */
    Mono<TicketResponse> createTicketFromCustomer(CreateTicketPublicRequest request);

    /**
     * Sisteme giriş yapmış bir çalışan tarafından yeni bir ticket oluşturur.
     * @param request Ticket bilgileri.
     * @param creatorId İsteği yapan çalışanın ID'si.
     * @return Oluşturulan ticket'ın detayları.
     */
    Mono<TicketResponse> createTicketFromEmployee(CreateTicketEmployeeRequest request, Long creatorId);

    /**
     * ID'ye göre bir ticket'ın tüm detaylarını getirir.
     * @param id Ticket ID'si.
     * @return Ticket detayları.
     */
    Mono<TicketResponse> getTicketById(Long id);

    Mono<TicketResponse> claimTicket(Long ticketId, Long employeeId);
}
