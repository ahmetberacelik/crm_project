package com.crm.department.service;

import com.crm.department.api.dto.request.PersonCreateRequest;
import com.crm.department.api.dto.response.PersonResponse;
import reactor.core.publisher.Mono;

public interface PersonService {
    Mono<PersonResponse> createPerson(PersonCreateRequest request);

    // tree util may be placed here later
}
