package com.crm.department.service;

import com.crm.department.api.dto.request.DepartmentCreateRequest;
import com.crm.department.api.dto.request.DepartmentUpdateRequest;
import com.crm.department.api.dto.response.DepartmentResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface DepartmentService {
    Flux<DepartmentResponse> getDepartmentTree();
    Mono<DepartmentResponse> getById(Long id);
    Mono<DepartmentResponse> create(DepartmentCreateRequest request);
    Mono<DepartmentResponse> update(Long id, DepartmentUpdateRequest request);
    Mono<Void> softDelete(Long id);
}


