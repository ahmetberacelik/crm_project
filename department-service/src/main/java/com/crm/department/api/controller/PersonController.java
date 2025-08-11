package com.crm.department.api.controller;

import com.crm.department.api.dto.request.PersonCreateRequest;
import com.crm.department.api.dto.request.PersonUpdateRequest;
import com.crm.department.api.dto.response.PersonResponse;
import com.crm.department.service.PersonService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequestMapping("/api/persons")
@RequiredArgsConstructor
public class PersonController {

    private final PersonService personService;

    @GetMapping
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public Flux<PersonResponse> listActive() {
        return personService.getActivePersons();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public Mono<PersonResponse> getActiveById(@PathVariable Long id) {
        return personService.getActiveById(id);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<PersonResponse> create(@Valid @RequestBody PersonCreateRequest request) {
        return personService.create(request);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public Mono<PersonResponse> update(@PathVariable Long id, @Valid @RequestBody PersonUpdateRequest request) {
        return personService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deactivate(@PathVariable Long id) {
        return personService.deactivate(id);
    }

    // Admin özel listelemeler
    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public Flux<PersonResponse> listAll() {
        return personService.getAllPersons();
    }

    @GetMapping("/inactive")
    @PreAuthorize("hasRole('ADMIN')")
    public Flux<PersonResponse> listInactive() {
        return personService.getInactivePersons();
    }

    // Internal endpoint: user-service register sırasında kullanır
    @PostMapping("/internal/register")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Map<String, Long>> internalRegister(@Valid @RequestBody PersonCreateRequest request) {
        return personService.internalRegister(request).map(p -> Map.of("personId", p.getPersonId()));
    }
}


