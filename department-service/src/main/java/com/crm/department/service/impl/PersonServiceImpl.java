package com.crm.department.service.impl;

import com.crm.department.api.dto.request.PersonCreateRequest;
import com.crm.department.api.dto.request.PersonUpdateRequest;
import com.crm.department.api.dto.response.PersonResponse;
import com.crm.department.data.entity.Person;
import com.crm.department.data.repository.DepartmentRepository;
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
    private final DepartmentRepository departmentRepository;

    @Override
    public Mono<PersonResponse> createPerson(PersonCreateRequest request) {
        return departmentRepository.findByDepartmentIdAndIsDeletedFalse(request.getDepartmentId())
                .switchIfEmpty(Mono.error(new RuntimeException("Department not found with ID: " + request.getDepartmentId())))
                .flatMap(department -> personRepository.findByEmail(request.getEmail())
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
                                    .map(this::toPersonResponse);
                        }));
    }

    @Override
    public Flux<PersonResponse> findAll() {
        return personRepository.findAllByIsActiveTrue().map(this::toPersonResponse);
    }

    @Override
    public Mono<PersonResponse> findById(Long id) {
        return personRepository.findByPersonIdAndIsActiveTrue(id)
                .map(this::toPersonResponse)
                .switchIfEmpty(Mono.error(new RuntimeException("Active person not found with id: " + id)));
    }

    @Override
    public Mono<PersonResponse> updatePerson(Long id, PersonUpdateRequest request) {
        return personRepository.findByPersonIdAndIsActiveTrue(id)
                .switchIfEmpty(Mono.error(new RuntimeException("Active person not found with id: " + id)))
                .flatMap(person -> {
                    // Check if the new department exists
                    return departmentRepository.findByDepartmentIdAndIsDeletedFalse(request.getDepartmentId())
                            .switchIfEmpty(Mono.error(new RuntimeException("New department not found with id: " + request.getDepartmentId())))
                            .flatMap(department -> {
                                person.setName(request.getName());
                                person.setSurname(request.getSurname());
                                person.setEmail(request.getEmail());
                                person.setPhone(request.getPhone());
                                person.setDepartmentId(request.getDepartmentId());
                                return personRepository.save(person);
                            });
                })
                .map(this::toPersonResponse);
    }

    @Override
    public Mono<Void> deletePerson(Long id) {
        return personRepository.findByPersonIdAndIsActiveTrue(id)
                .switchIfEmpty(Mono.error(new RuntimeException("Active person not found with id: " + id)))
                .flatMap(person -> {
                    person.setIsActive(false);
                    return personRepository.save(person);
                }).then();
    }

    private PersonResponse toPersonResponse(Person person) {
        return PersonResponse.builder()
                .personId(person.getPersonId())
                .name(person.getName())
                .surname(person.getSurname())
                .email(person.getEmail())
                .phone(person.getPhone())
                .departmentId(person.getDepartmentId())
                .build();
    }

    // --- ADMIN-SPECIFIC METHODS ---

    @Override
    public Flux<PersonResponse> findAllWithInactive() {
        return personRepository.findAll().map(this::toPersonResponse);
    }

    @Override
    public Flux<PersonResponse> findAllInactive() {
        return personRepository.findAllByIsActiveFalse().map(this::toPersonResponse);
    }

    @Override
    public Mono<PersonResponse> reactivatePerson(Long id) {
        return personRepository.findById(id) // Find any person, active or not
                .switchIfEmpty(Mono.error(new RuntimeException("Person not found with id: " + id)))
                .flatMap(person -> {
                    if (person.getIsActive()) {
                        return Mono.error(new RuntimeException("Person with id: " + id + " is already active."));
                    }
                    person.setIsActive(true);
                    return personRepository.save(person);
                })
                .map(this::toPersonResponse);
    }
}
