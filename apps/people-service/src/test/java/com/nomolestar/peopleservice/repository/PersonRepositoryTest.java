package com.nomolestar.peopleservice.repository;

import com.nomolestar.peopleservice.enums.PersonRole;
import com.nomolestar.peopleservice.model.PersonEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class PersonRepositoryTest {

    @Autowired
    private PersonRepository personRepository;

    private PersonEntity buildPerson(String fullName, PersonRole role, Integer caseId) {
        return PersonEntity.builder()
                .fullName(fullName)
                .role(role)
                .age(30)
                .description("Test person")
                .caseId(caseId)
                .build();
    }

    @Test
    void save_andFindById_works() {
        PersonEntity saved = personRepository.save(buildPerson("Alice", PersonRole.DETECTIVE, 1));
        Optional<PersonEntity> found = personRepository.findById(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getFullName()).isEqualTo("Alice");
    }

    @Test
    void findAll_returnsAllPeople() {
        personRepository.save(buildPerson("Alice", PersonRole.DETECTIVE, 1));
        personRepository.save(buildPerson("Bob", PersonRole.SUSPECT, 2));
        assertThat(personRepository.findAll()).hasSize(2);
    }

    @Test
    void findByCaseId_returnsMatchingPeople() {
        personRepository.save(buildPerson("Alice", PersonRole.DETECTIVE, 1));
        personRepository.save(buildPerson("Bob", PersonRole.WITNESS, 1));
        personRepository.save(buildPerson("Charlie", PersonRole.SUSPECT, 2));

        List<PersonEntity> result = personRepository.findByCaseId(1);
        assertThat(result).hasSize(2);
        assertThat(result).allMatch(p -> p.getCaseId().equals(1));
    }

    @Test
    void findByCaseId_noMatch_returnsEmpty() {
        personRepository.save(buildPerson("Alice", PersonRole.DETECTIVE, 1));
        List<PersonEntity> result = personRepository.findByCaseId(999);
        assertThat(result).isEmpty();
    }

    @Test
    void deleteById_removesPerson() {
        PersonEntity saved = personRepository.save(buildPerson("Alice", PersonRole.DETECTIVE, 1));
        personRepository.deleteById(saved.getId());
        assertThat(personRepository.findById(saved.getId())).isEmpty();
    }
}
