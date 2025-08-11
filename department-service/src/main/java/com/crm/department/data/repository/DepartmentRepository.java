package com.crm.department.data.repository;

import com.crm.department.data.entity.Department;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface DepartmentRepository extends ReactiveCrudRepository<Department, Long> {

    @Query("SELECT department_id, name, parent_department_id, isActive, isDeleted, deleted_user_id, isUpdated, updated_user_id FROM departments WHERE parent_department_id = :parentDepartmentId")
    Flux<Department> findByParentDepartmentId(@Param("parentDepartmentId") Long parentDepartmentId);

    @Query("SELECT department_id, name, parent_department_id, isActive, isDeleted, deleted_user_id, isUpdated, updated_user_id FROM departments WHERE isActive = 1")
    Flux<Department> findAllByIsActiveTrue();

    // Avoid default findById parameter name mismatch by using property-based finder
    @Query("SELECT department_id, name, parent_department_id, isActive, isDeleted, deleted_user_id, isUpdated, updated_user_id FROM departments WHERE department_id = :departmentId")
    Mono<Department> findByDepartmentId(@Param("departmentId") Long departmentId);
}


