package com.crm.department.service;

import com.crm.department.api.dto.request.PersonCreateRequest;
import com.crm.department.api.dto.request.PersonUpdateRequest;
import com.crm.department.api.dto.response.PersonResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface PersonService {
    Flux<PersonResponse> getActivePersons();
    Flux<PersonResponse> getAllPersons();
    Flux<PersonResponse> getInactivePersons();
    Mono<PersonResponse> getActiveById(Long id);
    Mono<PersonResponse> create(PersonCreateRequest request);
    Mono<PersonResponse> update(Long id, PersonUpdateRequest request);
    Mono<Void> deactivate(Long id);
    Mono<PersonResponse> internalRegister(PersonCreateRequest request);
}


