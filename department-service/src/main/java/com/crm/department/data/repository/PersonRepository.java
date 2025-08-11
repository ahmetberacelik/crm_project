package com.crm.department.data.repository;

import com.crm.department.data.entity.Person;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface PersonRepository extends ReactiveCrudRepository<Person, Long> {

    @Query("SELECT person_id, name, surname, email, phone, department_id, isActive FROM persons WHERE email = :email")
    Mono<Person> findByEmail(@Param("email") String email);

    @Query("SELECT person_id, name, surname, email, phone, department_id, isActive FROM persons WHERE department_id = :departmentId")
    Flux<Person> findByDepartmentId(@Param("departmentId") Long departmentId);

    // Listing helpers for API use-cases
    Flux<Person> findAllByIsActiveTrue();

    Flux<Person> findAllByIsActiveFalse();

    // The driver has issues with BIT bindings on some derived methods; use explicit SQL.
    @Query("SELECT person_id, name, surname, email, phone, department_id, isActive FROM persons WHERE person_id = :personId AND isActive = 1")
    Mono<Person> findActiveById(@Param("personId") Long personId);

    @Query("SELECT person_id, name, surname, email, phone, department_id, isActive FROM persons WHERE isActive = 1")
    Flux<Person> findAllActive();

    @Query("SELECT person_id, name, surname, email, phone, department_id, isActive FROM persons WHERE isActive = 0")
    Flux<Person> findAllInactive();
}


