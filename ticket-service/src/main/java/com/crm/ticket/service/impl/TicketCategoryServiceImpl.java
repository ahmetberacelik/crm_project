package com.crm.ticket.service.impl;

import com.crm.ticket.api.dto.request.CreateCategoryRequest;
import com.crm.ticket.api.dto.response.CategoryResponse;
import com.crm.ticket.data.entity.TicketCategory;
import com.crm.ticket.data.repository.TicketCategoryRepository;
import com.crm.ticket.service.TicketCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class TicketCategoryServiceImpl implements TicketCategoryService {

    private final TicketCategoryRepository categoryRepository;

    @Override
    public Flux<CategoryResponse> getAllCategories() {
        return categoryRepository.findAll().map(this::toResponse);
    }

    @Override
    public Mono<CategoryResponse> createCategory(CreateCategoryRequest request) {
        TicketCategory newCategory = TicketCategory.builder()
                .categoryKey(request.getCategoryKey().toUpperCase())
                .displayName(request.getDisplayName())
                .targetDepartmentId(request.getTargetDepartmentId())
                .isActive(true)
                .build();
        return categoryRepository.save(newCategory).map(this::toResponse);
    }

    @Override
    public Mono<CategoryResponse> updateCategoryStatus(Long id, boolean isActive) {
        return categoryRepository.findById(id)
                .flatMap(category -> {
                    category.setIsActive(isActive);
                    return categoryRepository.save(category);
                })
                .map(this::toResponse)
                .switchIfEmpty(Mono.error(new RuntimeException("Category not found with id: " + id))); // Bunu sonra iyileştireceğiz
    }

    @Override
    public Flux<CategoryResponse> getActiveCategories() {
        return categoryRepository.findAllByIsActive(true).map(this::toResponse);
    }

    private CategoryResponse toResponse(TicketCategory entity) {
        return CategoryResponse.builder()
                .id(entity.getId())
                .key(entity.getCategoryKey())
                .displayName(entity.getDisplayName())
                .targetDepartmentId(entity.getTargetDepartmentId())
                .isActive(entity.getIsActive())
                .build();
    }
}
