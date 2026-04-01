package com.nomolestar.caseservice.mapper;

import com.nomolestar.caseservice.dto.CaseCreateDTO;
import com.nomolestar.caseservice.dto.CaseResponseDTO;
import com.nomolestar.caseservice.dto.CaseUpdateDTO;
import com.nomolestar.caseservice.model.CaseEntity;

public class CaseMapper {

    private CaseMapper() {
    }

    public static CaseResponseDTO toCaseResponseDTO(CaseEntity caseEntity) {
        return new CaseResponseDTO(
                caseEntity.getId(),
                caseEntity.getTitle(),
                caseEntity.getDescription(),
                caseEntity.getStatus(),
                caseEntity.getPriority(),
                caseEntity.getAssignedDetective(),
                caseEntity.getCreatedAt()
        );
    }

    public static CaseEntity toEntity(CaseCreateDTO dto) {
        CaseEntity caseEntity = new CaseEntity();
        caseEntity.setTitle(dto.title());
        caseEntity.setDescription(dto.description());
        caseEntity.setStatus(dto.status());
        caseEntity.setPriority(dto.priority());
        caseEntity.setAssignedDetective(dto.assignedDetective());
        caseEntity.setCreatedAt(dto.createdAt());
        return caseEntity;
    }

    public static void updateEntity(CaseEntity caseEntity, CaseUpdateDTO dto) {
        caseEntity.setTitle(dto.title());
        caseEntity.setDescription(dto.description());
        caseEntity.setStatus(dto.status());
        caseEntity.setPriority(dto.priority());
        caseEntity.setAssignedDetective(dto.assignedDetective());
    }
}