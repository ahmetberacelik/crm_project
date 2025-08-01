package com.crm.department.service;

import com.crm.department.api.dto.request.DepartmentCreateRequest;
import com.crm.department.api.dto.request.DepartmentUpdateRequest;
import com.crm.department.api.dto.response.DepartmentTreeResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface DepartmentService {
    Flux<DepartmentTreeResponse> getDepartmentTree();
    Mono<DepartmentTreeResponse> createDepartment(DepartmentCreateRequest request);
    Mono<DepartmentTreeResponse> updateDepartment(Long id, DepartmentUpdateRequest request);
    Mono<Void> deleteDepartment(Long id);
}
