package com.crm.department.service.impl;

import com.crm.department.api.dto.request.DepartmentCreateRequest;
import com.crm.department.api.dto.request.DepartmentUpdateRequest;
import com.crm.department.api.dto.response.DepartmentTreeResponse;
import com.crm.department.data.entity.Department;
import com.crm.department.data.repository.DepartmentRepository;
import com.crm.department.service.DepartmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentRepository departmentRepository;

    @Override
    public Flux<DepartmentTreeResponse> getDepartmentTree() {
        return departmentRepository.findAllByIsDeletedFalse()
                .collectList()
                .flatMapMany(list -> Flux.fromIterable(buildTree(list)));
    }

    @Override
    public Mono<DepartmentTreeResponse> createDepartment(DepartmentCreateRequest request) {
        Department department = Department.builder()
                .name(request.getName())
                .parentDepartmentId(request.getParentDepartmentId())
                .isActive(true)
                .isDeleted(false)
                .isUpdated(false)
                .build();
        return departmentRepository.save(department)
                .map(saved -> DepartmentTreeResponse.builder()
                        .departmentId(saved.getDepartmentId())
                        .name(saved.getName())
                        .build());
    }

    @Override
    public Mono<DepartmentTreeResponse> updateDepartment(Long id, DepartmentUpdateRequest request) {
        return departmentRepository.findByDepartmentIdAndIsDeletedFalse(id)
                .switchIfEmpty(Mono.error(new RuntimeException("Department not found with id: " + id)))
                .flatMap(department -> {
                    department.setName(request.getName());
                    department.setParentDepartmentId(request.getParentDepartmentId());
                    department.setIsUpdated(true);
                    // In a real app, you would also set the updated_user_id here
                    return departmentRepository.save(department);
                })
                .map(saved -> DepartmentTreeResponse.builder()
                        .departmentId(saved.getDepartmentId())
                        .name(saved.getName())
                        .build());
    }

    @Override
    public Mono<Void> deleteDepartment(Long id) {
        return departmentRepository.findByDepartmentIdAndIsDeletedFalse(id)
                .switchIfEmpty(Mono.error(new RuntimeException("Department not found with id: " + id)))
                .flatMap(department -> {
                    department.setIsDeleted(true);
                    // In a real app, you would also set the deleted_user_id here
                    return departmentRepository.save(department);
                })
                .then();
    }

    private List<DepartmentTreeResponse> buildTree(List<Department> deps) {
        Map<Long, DepartmentTreeResponse> map = new HashMap<>();
        // create dto objects
        deps.forEach(d -> map.put(d.getDepartmentId(),
                DepartmentTreeResponse.builder()
                        .departmentId(d.getDepartmentId())
                        .name(d.getName())
                        .build()));
        // attach children
        for (Department d : deps) {
            if (d.getParentDepartmentId() != null) {
                DepartmentTreeResponse parent = map.get(d.getParentDepartmentId());
                if (parent != null) {
                    parent.getChildren().add(map.get(d.getDepartmentId()));
                }
            }
        }
        // return roots (parent null)
        return map.values().stream()
                .filter(dto -> deps.stream()
                        .filter(d -> dto.getDepartmentId().equals(d.getDepartmentId()))
                        .findFirst()
                        .map(d -> d.getParentDepartmentId() == null)
                        .orElse(false))
                .collect(Collectors.toList());
    }
}
