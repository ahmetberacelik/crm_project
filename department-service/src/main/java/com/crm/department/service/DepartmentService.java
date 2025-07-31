package com.crm.department.service;

import com.crm.department.api.dto.response.DepartmentTreeResponse;
import reactor.core.publisher.Flux;

public interface DepartmentService {
    Flux<DepartmentTreeResponse> getDepartmentTree();
}
