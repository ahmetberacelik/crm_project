package com.crm.department.api.controller;

import com.crm.department.api.dto.request.PersonCreateRequest;
import com.crm.department.api.dto.request.PersonUpdateRequest;
import com.crm.department.api.dto.response.PersonResponse;
import com.crm.department.service.PersonService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/persons")
@RequiredArgsConstructor
public class PersonController {

    private final PersonService personService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<PersonResponse> create(@Valid @RequestBody PersonCreateRequest request) {
        return personService.createPerson(request);
    }

    @GetMapping
    public Flux<PersonResponse> getAll() {
        return personService.findAll();
    }

    @GetMapping("/{id}")
    public Mono<PersonResponse> getById(@PathVariable Long id) {
        return personService.findById(id);
    }

    @PutMapping("/{id}")
    public Mono<PersonResponse> update(@PathVariable Long id, @Valid @RequestBody PersonUpdateRequest request) {
        return personService.updatePerson(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> delete(@PathVariable Long id) {
        return personService.deletePerson(id);
    }

    @PostMapping("/internal/register")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<PersonResponse> createForRegistration(@Valid @RequestBody PersonCreateRequest request) {
        return personService.createPerson(request);
    }

    // --- ADMIN ENDPOINTS ---

    @GetMapping("/all")
    public Flux<PersonResponse> getAllWithInactive() {
        return personService.findAllWithInactive();
    }

    @GetMapping("/inactive")
    public Flux<PersonResponse> getAllInactive() {
        return personService.findAllInactive();
    }

    @PutMapping("/{id}/reactivate")
    public Mono<PersonResponse> reactivate(@PathVariable Long id) {
        return personService.reactivatePerson(id);
    }
}
