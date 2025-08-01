package com.crm.department.api.controller;

import com.crm.department.api.dto.request.PersonCreateRequest;
import com.crm.department.api.dto.response.PersonResponse;
import com.crm.department.service.PersonService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/internal/persons")
@RequiredArgsConstructor
public class PersonController {

    private final PersonService personService;

    @PostMapping
    public Mono<PersonResponse> create(@Valid @RequestBody PersonCreateRequest request) {
        return personService.createPerson(request)
                .doOnSuccess(p -> System.out.println("Person created: " + p.getPersonId()))
                .doOnError(e -> System.err.println("Person create error: " + e.getMessage()))
                .onErrorMap(e -> new RuntimeException("Could not create person: " + e.getMessage()));
    }
}
