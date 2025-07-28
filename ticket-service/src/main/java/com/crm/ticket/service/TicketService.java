package com.crm.ticket.service;

import com.crm.ticket.api.dto.request.AssignTicketRequest;
import com.crm.ticket.api.dto.request.CreateTicketRequest;
import com.crm.ticket.api.dto.request.UpdateStatusRequest;
import com.crm.ticket.api.dto.response.TicketResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface TicketService {

    Mono<TicketResponse> createTicket(CreateTicketRequest request);

    Mono<TicketResponse> assignTicket(AssignTicketRequest request);

    Mono<TicketResponse> updateStatus(UpdateStatusRequest request);

    Flux<TicketResponse> findByCustomer(Long customerId);
} 