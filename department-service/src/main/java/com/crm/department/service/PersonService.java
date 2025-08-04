package com.crm.department.service;

import com.crm.department.api.dto.request.PersonCreateRequest;
import com.crm.department.api.dto.request.PersonUpdateRequest;
import com.crm.department.api.dto.response.PersonResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface PersonService {
    // Standard User methods
    Mono<PersonResponse> createPerson(PersonCreateRequest request);
    Flux<PersonResponse> findAll();
    Mono<PersonResponse> findById(Long id);
    Mono<PersonResponse> updatePerson(Long id, PersonUpdateRequest request);
    Mono<Void> deletePerson(Long id);

    // Admin-specific methods
    Flux<PersonResponse> findAllWithInactive();
    Flux<PersonResponse> findAllInactive();
    Mono<PersonResponse> reactivatePerson(Long id);
}
