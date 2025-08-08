// PersonRepository.java - Düzeltilmiş versi
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

    @Query("SELECT * FROM persons WHERE email = :email")
    Mono<Person> findByEmail(String email);

    // Explicit SQL queries to avoid parameter mapping issues
    @Query("SELECT * FROM persons WHERE isActive = 1")
    Flux<Person> findAllByIsActiveTrue();

    @Query("SELECT * FROM persons WHERE isActive = 0")
    Flux<Person> findAllByIsActiveFalse();

    @Query("SELECT * FROM persons WHERE person_id = :person_id AND isActive = 1")
    Mono<Person> findByPersonIdAndIsActiveTrue(@Param("person_id") Long personId);
}