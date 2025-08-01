package com.crm.department.api.controller;

import com.crm.department.api.dto.request.DepartmentCreateRequest;
import com.crm.department.api.dto.request.DepartmentUpdateRequest;
import com.crm.department.api.dto.response.DepartmentTreeResponse;
import com.crm.department.service.DepartmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/departments")
@RequiredArgsConstructor
public class DepartmentController {

    private final DepartmentService departmentService;

    @GetMapping("/tree")
    public Flux<DepartmentTreeResponse> getTree() {
        return departmentService.getDepartmentTree();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<DepartmentTreeResponse> create(@Valid @RequestBody DepartmentCreateRequest request) {
        return departmentService.createDepartment(request);
    }

    @PutMapping("/{id}")
    public Mono<DepartmentTreeResponse> update(@PathVariable Long id, @Valid @RequestBody DepartmentUpdateRequest request) {
        return departmentService.updateDepartment(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> delete(@PathVariable Long id) {
        return departmentService.deleteDepartment(id);
    }
}
