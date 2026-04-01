package com.nomolestar.caseservice.controller;

import com.nomolestar.caseservice.dto.CaseCreateDTO;
import com.nomolestar.caseservice.dto.CaseResponseDTO;
import com.nomolestar.caseservice.dto.CaseUpdateDTO;
import com.nomolestar.caseservice.service.CaseService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cases")
public class CaseController {

    private final CaseService caseService;

    public CaseController(CaseService caseService) {
        this.caseService = caseService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DETECTIVE', 'VIEWER')")
    public List<CaseResponseDTO> findAll() {
        return caseService.findAll();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DETECTIVE', 'VIEWER')")
    public CaseResponseDTO findById(@PathVariable Integer id) {
        return caseService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ADMIN', 'DETECTIVE')")
    public CaseResponseDTO create(@Valid @RequestBody CaseCreateDTO dto) {
        return caseService.create(dto);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DETECTIVE')")
    public CaseResponseDTO updateById(
            @PathVariable Integer id,
            @Valid @RequestBody CaseUpdateDTO dto
    ) {
        return caseService.updateById(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteById(@PathVariable Integer id) {
        caseService.deleteById(id);
    }
}
