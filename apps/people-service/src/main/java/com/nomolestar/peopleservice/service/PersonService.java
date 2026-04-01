package com.nomolestar.peopleservice.service;

import com.nomolestar.peopleservice.dto.PersonCreateDTO;
import com.nomolestar.peopleservice.dto.PersonResponseDTO;
import com.nomolestar.peopleservice.dto.PersonUpdateDTO;
import com.nomolestar.peopleservice.exceptions.ResourceNotFoundException;
import com.nomolestar.peopleservice.mapper.PersonMapper;
import com.nomolestar.peopleservice.model.PersonEntity;
import com.nomolestar.peopleservice.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PersonService {

    private final PersonRepository personRepository;
    private final WebClient webClient;

    @Value("${services.case-service.url}")
    private String caseServiceUrl;

    public PersonService(PersonRepository personRepository,
                         WebClient.Builder webClientBuilder) {
        this.personRepository = personRepository;
        this.webClient = webClientBuilder.build();
    }

    public List<PersonResponseDTO> findAll() {
        return personRepository.findAll()
                .stream()
                .map(PersonMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    public PersonResponseDTO findById(Integer id) {
        PersonEntity personEntity = personRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Person with id " + id + " not found"
                ));

        return PersonMapper.toResponseDTO(personEntity);
    }

    public List<PersonResponseDTO> findByCaseId(Integer caseId) {
        return personRepository.findByCaseId(caseId)
                .stream()
                .map(PersonMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    public PersonResponseDTO create(PersonCreateDTO dto, String token) {
        validateCaseExists(dto.caseId(), token);

        PersonEntity personEntity = PersonMapper.toEntity(dto);
        PersonEntity savedPerson = personRepository.save(personEntity);

        return PersonMapper.toResponseDTO(savedPerson);
    }

    public PersonResponseDTO updateById(Integer id, PersonUpdateDTO dto) {
        PersonEntity personEntity = personRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Person with id " + id + " not found"
                ));

        PersonMapper.updateEntity(personEntity, dto);

        PersonEntity updatedPerson = personRepository.save(personEntity);

        return PersonMapper.toResponseDTO(updatedPerson);
    }

    public void deleteById(Integer id) {
        PersonEntity personEntity = personRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Person with id " + id + " not found"
                ));

        personRepository.delete(personEntity);
    }

    private void validateCaseExists(Integer caseId, String token) {
        try {
            webClient.get()
                    .uri(caseServiceUrl + "/" + caseId)
                    .header("Authorization", token)
                    .retrieve()
                    .toBodilessEntity()
                    .block();

        } catch (WebClientResponseException.NotFound e) {
            throw new ResourceNotFoundException(
                    "Case with id " + caseId + " not found"
            );

        } catch (WebClientResponseException.Forbidden e) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You do not have permission to access the case service"
            );

        } catch (WebClientResponseException.Unauthorized e) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Authentication token is invalid or missing"
            );

        } catch (Exception e) {
            throw new RuntimeException(
                    "Error communicating with Case Service: " + e.getMessage(),
                    e
            );
        }
    }
}