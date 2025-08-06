package com.crm.ticket.api.controller;

import com.crm.ticket.api.dto.response.CategoryResponse;
import com.crm.ticket.service.TicketCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/public")
@RequiredArgsConstructor
public class PublicController {

    private final TicketCategoryService categoryService;

    /**
     * Returns a list of active ticket categories.
     * This endpoint is public and used by the frontend to build the category selection dropdown.
     */
    @GetMapping("/categories")
    public Flux<CategoryResponse> getActiveCategories() {
        return categoryService.getActiveCategories();
    }
}
