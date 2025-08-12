package com.crm.department.service.impl;

import com.crm.department.api.dto.request.DepartmentCreateRequest;
import com.crm.department.api.dto.request.DepartmentUpdateRequest;
import com.crm.department.api.dto.response.DepartmentResponse;
import com.crm.department.data.entity.Department;
import com.crm.department.data.repository.DepartmentRepository;
import com.crm.department.service.DepartmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentRepository departmentRepository;

    @Override
    public Flux<DepartmentResponse> getDepartmentTree() {
        // Minimal: tüm aktif departmanları düz liste döner; ağaç kurma UI'ya bırakılabilir.
        return departmentRepository.findAllByIsActiveTrue().map(this::toResponse);
    }

    @Override
    public Mono<DepartmentResponse> getById(Long id) {
        // Özel @Query yerine built-in findById kullanarak parametre karışıklığını engelle
        return departmentRepository.findById(id).map(this::toResponse);
    }

    @Override
    public Mono<DepartmentResponse> create(DepartmentCreateRequest request) {
        Department d = Department.builder()
                .name(request.getName())
                .parentDepartmentId(request.getParentDepartmentId())
                .isActive(true)
                .isDeleted(false)
                .isUpdated(false)
                .build();
        return departmentRepository.save(d).map(this::toResponse);
    }

    @Override
    public Mono<DepartmentResponse> update(Long id, DepartmentUpdateRequest request) {
        return departmentRepository.findById(id)
                .flatMap(d -> {
                    d.setName(request.getName());
                    d.setParentDepartmentId(request.getParentDepartmentId());
                    d.setIsUpdated(true);
                    return departmentRepository.save(d);
                })
                .map(this::toResponse);
    }

    @Override
    public Mono<Void> softDelete(Long id) {
        return departmentRepository.findById(id)
                .flatMap(d -> {
                    d.setIsActive(false);
                    d.setIsDeleted(true);
                    return departmentRepository.save(d);
                })
                .then();
    }

    private DepartmentResponse toResponse(Department department) {
        return DepartmentResponse.builder()
                .departmentId(department.getDepartmentId())
                .name(department.getName())
                .parentDepartmentId(department.getParentDepartmentId())
                .build();
    }
}


