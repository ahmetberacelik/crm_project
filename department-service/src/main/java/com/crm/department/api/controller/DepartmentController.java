package com.crm.department.api.controller;

import com.crm.department.api.dto.request.DepartmentCreateRequest;
import com.crm.department.api.dto.request.DepartmentUpdateRequest;
import com.crm.department.api.dto.response.DepartmentResponse;
import com.crm.department.service.DepartmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/departments")
@RequiredArgsConstructor
public class DepartmentController {

    private final DepartmentService departmentService;

    @GetMapping("/tree")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public Flux<DepartmentResponse> getTree() {
        return departmentService.getDepartmentTree();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public Mono<DepartmentResponse> getById(@PathVariable Long id) {
        return departmentService.getById(id);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<DepartmentResponse> create(@Valid @RequestBody DepartmentCreateRequest request) {
        return departmentService.create(request);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Mono<DepartmentResponse> update(@PathVariable Long id, @Valid @RequestBody DepartmentUpdateRequest request) {
        return departmentService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> delete(@PathVariable Long id) {
        return departmentService.softDelete(id);
    }
}


