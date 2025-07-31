package com.crm.department.data.repository;

import com.crm.department.data.entity.Person;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface PersonRepository extends ReactiveCrudRepository<Person, Long> {
    Mono<Person> findByEmail(String email);
}
