package com.nomolestar.peopleservice.unit;

import com.nomolestar.peopleservice.dto.PersonCreateDTO;
import com.nomolestar.peopleservice.dto.PersonResponseDTO;
import com.nomolestar.peopleservice.dto.PersonUpdateDTO;
import com.nomolestar.peopleservice.enums.PersonRole;
import com.nomolestar.peopleservice.exceptions.ResourceNotFoundException;
import com.nomolestar.peopleservice.model.PersonEntity;
import com.nomolestar.peopleservice.repository.PersonRepository;
import com.nomolestar.peopleservice.service.PersonService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PersonServiceTest {

    @Mock private PersonRepository personRepository;
    @Mock private WebClient.Builder webClientBuilder;
    @Mock private WebClient webClient;

    private PersonService personService;
    private PersonEntity personEntity;

    @BeforeEach
    void setUp() {
        when(webClientBuilder.build()).thenReturn(webClient);
        personService = new PersonService(personRepository, webClientBuilder);

        personEntity = PersonEntity.builder()
                .id(1)
                .caseId(10)
                .fullName("John Doe")
                .role(PersonRole.DETECTIVE)
                .age(35)
                .description("Test person")
                .build();
    }

    @Test
    void findAll_returnsList() {
        when(personRepository.findAll()).thenReturn(List.of(personEntity));
        List<PersonResponseDTO> result = personService.findAll();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).fullName()).isEqualTo("John Doe");
    }

    @Test
    void findById_found_returnsDTO() {
        when(personRepository.findById(1)).thenReturn(Optional.of(personEntity));
        PersonResponseDTO result = personService.findById(1);
        assertThat(result.id()).isEqualTo(1);
    }

    @Test
    void findById_notFound_throwsException() {
        when(personRepository.findById(99)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> personService.findById(99))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void findByCaseId_returnsList() {
        when(personRepository.findByCaseId(10)).thenReturn(List.of(personEntity));
        List<PersonResponseDTO> result = personService.findByCaseId(10);
        assertThat(result).hasSize(1);
        assertThat(result.get(0).caseId()).isEqualTo(10);
    }

    @Test
    void updateById_found_updatesAndReturns() {
        PersonUpdateDTO dto = new PersonUpdateDTO("Jane Doe", PersonRole.SUSPECT, 28, "Updated");
        when(personRepository.findById(1)).thenReturn(Optional.of(personEntity));
        when(personRepository.save(any())).thenReturn(personEntity);
        PersonResponseDTO result = personService.updateById(1, dto);
        assertThat(result).isNotNull();
    }

    @Test
    void updateById_notFound_throwsException() {
        PersonUpdateDTO dto = new PersonUpdateDTO("Jane Doe", PersonRole.SUSPECT, 28, "Updated");
        when(personRepository.findById(99)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> personService.updateById(99, dto))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void deleteById_found_deletes() {
        when(personRepository.findById(1)).thenReturn(Optional.of(personEntity));
        doNothing().when(personRepository).delete(personEntity);
        personService.deleteById(1);
        verify(personRepository, times(1)).delete(personEntity);
    }

    @Test
    void deleteById_notFound_throwsException() {
        when(personRepository.findById(99)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> personService.deleteById(99))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
