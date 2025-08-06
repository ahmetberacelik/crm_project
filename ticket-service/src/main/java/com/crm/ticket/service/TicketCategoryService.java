package com.crm.ticket.service;

import com.crm.ticket.api.dto.request.CreateCategoryRequest;
import com.crm.ticket.api.dto.response.CategoryResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface TicketCategoryService {
    Flux<CategoryResponse> getAllCategories();
    Mono<CategoryResponse> createCategory(CreateCategoryRequest request);
    Mono<CategoryResponse> updateCategoryStatus(Long id, boolean isActive);
    Flux<CategoryResponse> getActiveCategories();
}
