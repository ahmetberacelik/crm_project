package com.crm.department.data.repository;

import com.crm.department.data.entity.Department;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface DepartmentRepository extends ReactiveCrudRepository<Department, Long> {
    Flux<Department> findAllByIsDeletedFalse();
    Mono<Department> findByDepartmentIdAndIsDeletedFalse(Long id);
}
