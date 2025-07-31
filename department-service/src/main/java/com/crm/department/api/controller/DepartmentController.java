package com.crm.department.api.controller;

import com.crm.department.api.dto.response.DepartmentTreeResponse;
import com.crm.department.service.DepartmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/departments")
@RequiredArgsConstructor
public class DepartmentController {

    private final DepartmentService departmentService;

    @GetMapping("/tree")
    public Flux<DepartmentTreeResponse> getTree() {
        return departmentService.getDepartmentTree();
    }
}
