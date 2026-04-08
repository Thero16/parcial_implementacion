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
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PersonServiceTest {

    @Mock private PersonRepository personRepository;
    @Mock private WebClient.Builder webClientBuilder;
    @Mock private WebClient webClient;
    @Mock private WebClient.RequestHeadersUriSpec<?> requestHeadersUriSpec;
    @Mock private WebClient.RequestHeadersSpec<?> requestHeadersSpec;
    @Mock private WebClient.ResponseSpec responseSpec;

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

    private void mockWebClientSuccess() {
        doReturn(requestHeadersUriSpec).when(webClient).get();
        doReturn(requestHeadersSpec).when(requestHeadersUriSpec).uri(anyString());
        doReturn(requestHeadersSpec).when(requestHeadersSpec).header(anyString(), anyString());
        doReturn(responseSpec).when(requestHeadersSpec).retrieve();
        doReturn(Mono.just(ResponseEntity.ok().<Void>build())).when(responseSpec).toBodilessEntity();
    }

    @Test
    void create_savesAndReturnsDTO() {
        PersonCreateDTO dto = new PersonCreateDTO(10, "New Person", PersonRole.WITNESS, 25, "New witness");
        PersonEntity savedEntity = PersonEntity.builder()
                .id(2).caseId(10).fullName("New Person")
                .role(PersonRole.WITNESS).age(25).description("New witness")
                .build();

        mockWebClientSuccess();
        when(personRepository.save(any())).thenReturn(savedEntity);

        PersonResponseDTO result = personService.create(dto, "Bearer token");

        assertThat(result.fullName()).isEqualTo("New Person");
        assertThat(result.caseId()).isEqualTo(10);
        assertThat(result.role()).isEqualTo(PersonRole.WITNESS);
        verify(personRepository).save(any());
    }

    @Test
    void create_caseNotFound_throwsResourceNotFoundException() {
        PersonCreateDTO dto = new PersonCreateDTO(999, "New Person", PersonRole.WITNESS, 25, "New witness");

        doReturn(requestHeadersUriSpec).when(webClient).get();
        doReturn(requestHeadersSpec).when(requestHeadersUriSpec).uri(anyString());
        doReturn(requestHeadersSpec).when(requestHeadersSpec).header(anyString(), anyString());
        doReturn(responseSpec).when(requestHeadersSpec).retrieve();
        doReturn(Mono.error(WebClientResponseException.create(
                404, "Not Found", HttpHeaders.EMPTY, null, null)))
                .when(responseSpec).toBodilessEntity();

        assertThatThrownBy(() -> personService.create(dto, "Bearer token"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Case with id 999 not found");
    }
}
