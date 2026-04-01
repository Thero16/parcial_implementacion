package com.nomolestar.peopleservice.controller;

import com.nomolestar.peopleservice.dto.PersonCreateDTO;
import com.nomolestar.peopleservice.dto.PersonResponseDTO;
import com.nomolestar.peopleservice.dto.PersonUpdateDTO;
import com.nomolestar.peopleservice.service.PersonService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/people")
public class PersonController {

    private final PersonService personService;

    public PersonController(PersonService personService) {
        this.personService = personService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DETECTIVE', 'VIEWER')")
    public List<PersonResponseDTO> findAll() {
        return personService.findAll();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DETECTIVE', 'VIEWER')")
    public PersonResponseDTO findById(@PathVariable Integer id) {
        return personService.findById(id);
    }

    @GetMapping("/case/{caseId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DETECTIVE', 'VIEWER')")
    public List<PersonResponseDTO> findByCaseId(@PathVariable Integer caseId) {
        return personService.findByCaseId(caseId);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DETECTIVE')")
    public ResponseEntity<PersonResponseDTO> create(
            @RequestHeader("Authorization") String authorization,
            @Valid @RequestBody PersonCreateDTO dto
    ) {
        PersonResponseDTO response = personService.create(dto, authorization);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DETECTIVE')")
    public PersonResponseDTO updateById(
            @PathVariable Integer id,
            @Valid @RequestBody PersonUpdateDTO dto
    ) {
        return personService.updateById(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteById(@PathVariable Integer id) {
        personService.deleteById(id);
    }
}
