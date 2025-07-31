package com.crm.department.service.impl;

import com.crm.department.api.dto.request.PersonCreateRequest;
import com.crm.department.api.dto.response.PersonResponse;
import com.crm.department.data.entity.Person;
import com.crm.department.data.repository.PersonRepository;
import com.crm.department.service.PersonService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class PersonServiceImpl implements PersonService {

    private final PersonRepository personRepository;

    @Override
    public Mono<PersonResponse> createPerson(PersonCreateRequest request) {
        return personRepository.findByEmail(request.getEmail())
                .hasElement()
                .flatMap(exists -> {
                    if (exists) {
                        return Mono.error(new RuntimeException("Email already exists: " + request.getEmail()));
                    }
                    Person entity = Person.builder()
                            .name(request.getName())
                            .surname(request.getSurname())
                            .email(request.getEmail())
                            .phone(request.getPhone())
                            .departmentId(request.getDepartmentId())
                            .isActive(true)
                            .build();
                    return personRepository.save(entity)
                            .map(saved -> PersonResponse.builder()
                                    .personId(saved.getPersonId())
                                    .name(saved.getName())
                                    .surname(saved.getSurname())
                                    .email(saved.getEmail())
                                    .phone(saved.getPhone())
                                    .departmentId(saved.getDepartmentId())
                                    .build());
                });
    }
}
