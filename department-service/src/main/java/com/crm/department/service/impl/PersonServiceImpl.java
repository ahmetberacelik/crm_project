package com.crm.department.service.impl;

import com.crm.department.api.dto.request.PersonCreateRequest;
import com.crm.department.api.dto.request.PersonUpdateRequest;
import com.crm.department.api.dto.response.PersonResponse;
import com.crm.department.data.entity.Person;
import com.crm.department.data.repository.PersonRepository;
import com.crm.department.service.PersonService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class PersonServiceImpl implements PersonService {

    private final PersonRepository personRepository;

    @Override
    public Flux<PersonResponse> getActivePersons() {
        return personRepository.findAllActive().map(this::toResponse);
    }

    @Override
    public Flux<PersonResponse> getAllPersons() {
        return personRepository.findAll().map(this::toResponse);
    }

    @Override
    public Flux<PersonResponse> getInactivePersons() {
        return personRepository.findAllInactive().map(this::toResponse);
    }

    @Override
    public Mono<PersonResponse> getActiveById(Long id) {
        return personRepository.findActiveById(id).map(this::toResponse);
    }

    @Override
    public Mono<PersonResponse> create(PersonCreateRequest request) {
        Person p = Person.builder()
                .name(request.getName())
                .surname(request.getSurname())
                .email(request.getEmail())
                .phone(request.getPhone())
                .departmentId(request.getDepartmentId())
                .isActive(true)
                .build();
        return personRepository.save(p).map(this::toResponse);
    }

    @Override
    public Mono<PersonResponse> update(Long id, PersonUpdateRequest request) {
        return personRepository.findById(id)
                .flatMap(p -> {
                    p.setName(request.getName());
                    p.setSurname(request.getSurname());
                    p.setEmail(request.getEmail());
                    p.setPhone(request.getPhone());
                    if (request.getDepartmentId() != null) {
                        p.setDepartmentId(request.getDepartmentId());
                    }
                    return personRepository.save(p);
                })
                .map(this::toResponse);
    }

    @Override
    public Mono<Void> deactivate(Long id) {
        return personRepository.findById(id)
                .flatMap(p -> {
                    p.setIsActive(false);
                    return personRepository.save(p);
                })
                .then();
    }

    @Override
    public Mono<PersonResponse> internalRegister(PersonCreateRequest request) {
        // For internal register, behave same as create; uniqueness by email handled by DB unique constraint
        return create(request);
    }

    private PersonResponse toResponse(Person person) {
        return PersonResponse.builder()
                .personId(person.getPersonId())
                .name(person.getName())
                .surname(person.getSurname())
                .email(person.getEmail())
                .phone(person.getPhone())
                .departmentId(person.getDepartmentId())
                .build();
    }
}


