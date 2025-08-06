package com.crm.ticket.api.controller;

import com.crm.ticket.api.dto.request.CreateCategoryRequest;
import com.crm.ticket.api.dto.response.CategoryResponse;
import com.crm.ticket.service.TicketCategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequestMapping("/api/management/categories")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')") // Bu controller'daki tüm metotlar ADMIN rolü gerektirir
public class TicketCategoryController {

    private final TicketCategoryService categoryService;

    @GetMapping
    public Flux<CategoryResponse> getAllCategories() {
        return categoryService.getAllCategories();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<CategoryResponse> createCategory(@Valid @RequestBody CreateCategoryRequest request) {
        return categoryService.createCategory(request);
    }

    @PatchMapping("/{id}/status")
    public Mono<CategoryResponse> updateCategoryStatus(@PathVariable Long id, @RequestBody Map<String, Boolean> status) {
        Boolean isActive = status.get("isActive");
        if (isActive == null) {
            return Mono.error(new IllegalArgumentException("Request body must contain 'isActive' field."));
        }
        return categoryService.updateCategoryStatus(id, isActive);
    }
}
