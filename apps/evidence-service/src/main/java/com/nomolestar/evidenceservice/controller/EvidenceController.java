package com.nomolestar.evidenceservice.controller;

import com.nomolestar.evidenceservice.dto.EvidenceCreateDTO;
import com.nomolestar.evidenceservice.dto.EvidenceCustodyHistoryResponseDTO;
import com.nomolestar.evidenceservice.dto.EvidenceResponseDTO;
import com.nomolestar.evidenceservice.dto.EvidenceUpdateDTO;
import com.nomolestar.evidenceservice.service.EvidenceService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/evidences")
public class EvidenceController {

    private final EvidenceService evidenceService;

    public EvidenceController(EvidenceService evidenceService) {
        this.evidenceService = evidenceService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DETECTIVE', 'VIEWER')")
    public List<EvidenceResponseDTO> findAll() {
        return evidenceService.findAll();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DETECTIVE', 'VIEWER')")
    public EvidenceResponseDTO findById(@PathVariable Integer id) {
        return evidenceService.findById(id);
    }

    @GetMapping("/case/{caseId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DETECTIVE', 'VIEWER')")
    public List<EvidenceResponseDTO> findByCaseId(@PathVariable Integer caseId) {
        return evidenceService.findByCaseId(caseId);
    }

    @GetMapping("/{id}/custody-history")
    @PreAuthorize("hasAnyRole('ADMIN', 'DETECTIVE', 'VIEWER')")
    public List<EvidenceCustodyHistoryResponseDTO> getCustodyHistory(
            @PathVariable Integer id
    ) {
        return evidenceService.getCustodyHistory(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ADMIN', 'DETECTIVE')")
    public EvidenceResponseDTO create(
            @Valid @RequestBody EvidenceCreateDTO dto,
            @RequestHeader("Authorization") String authorization
    ) {
        return evidenceService.create(dto, authorization);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DETECTIVE')")
    public EvidenceResponseDTO updateById(
            @PathVariable Integer id,
            @Valid @RequestBody EvidenceUpdateDTO dto
    ) {
        return evidenceService.updateById(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteById(@PathVariable Integer id) {
        evidenceService.deleteById(id);
    }
}
