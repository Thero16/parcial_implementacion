package com.nomolestar.caseservice.service;

import com.nomolestar.caseservice.dto.CaseCreateDTO;
import com.nomolestar.caseservice.dto.CaseResponseDTO;
import com.nomolestar.caseservice.dto.CaseUpdateDTO;
import com.nomolestar.caseservice.exceptions.ResourceNotFoundException;
import com.nomolestar.caseservice.mapper.CaseMapper;
import com.nomolestar.caseservice.model.CaseEntity;
import com.nomolestar.caseservice.repository.CaseRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CaseService {

    private final CaseRepository caseRepository;

    public CaseService(CaseRepository caseRepository) {
        this.caseRepository = caseRepository;
    }

    public List<CaseResponseDTO> findAll() {
        return caseRepository.findAll()
                .stream()
                .map(CaseMapper::toCaseResponseDTO)
                .collect(Collectors.toList());
    }

    public CaseResponseDTO findById(Integer id) {
        CaseEntity caseEntity = caseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Case with id " + id + " not found"
                ));

        return CaseMapper.toCaseResponseDTO(caseEntity);
    }

    public CaseResponseDTO create(CaseCreateDTO dto) {
        CaseEntity caseEntity = CaseMapper.toEntity(dto);
        CaseEntity savedCase = caseRepository.save(caseEntity);

        return CaseMapper.toCaseResponseDTO(savedCase);
    }

    public CaseResponseDTO updateById(Integer id, CaseUpdateDTO dto) {
        CaseEntity caseEntity = caseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Case with id " + id + " not found"
                ));

        CaseMapper.updateEntity(caseEntity, dto);

        CaseEntity updatedCase = caseRepository.save(caseEntity);

        return CaseMapper.toCaseResponseDTO(updatedCase);
    }

    public void deleteById(Integer id) {
        CaseEntity caseEntity = caseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Case with id " + id + " not found"
                ));

        caseRepository.delete(caseEntity);
    }
}
