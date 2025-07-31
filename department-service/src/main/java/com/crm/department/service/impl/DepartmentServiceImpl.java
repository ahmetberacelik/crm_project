package com.crm.department.service.impl;

import com.crm.department.api.dto.response.DepartmentTreeResponse;
import com.crm.department.data.entity.Department;
import com.crm.department.data.repository.DepartmentRepository;
import com.crm.department.service.DepartmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

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
        return departmentRepository.findAll()
                .collectList()
                .flatMapMany(list -> Flux.fromIterable(buildTree(list)));
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
