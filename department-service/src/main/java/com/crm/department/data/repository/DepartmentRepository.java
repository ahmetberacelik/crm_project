// DepartmentRepository.java - Acil Düzeltme
package com.crm.department.data.repository;

import com.crm.department.data.entity.Department;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface DepartmentRepository extends ReactiveCrudRepository<Department, Long> {

    @Query("SELECT * FROM departments WHERE isDeleted = 0")
    Flux<Department> findAllByIsDeletedFalse();

    @Query("SELECT * FROM departments WHERE department_id = :department_id AND isDeleted = 0")
    Mono<Department> findByDepartmentIdAndIsDeletedFalse(Long departmentId);
}