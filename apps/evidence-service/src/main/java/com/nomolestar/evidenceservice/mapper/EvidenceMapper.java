package com.nomolestar.evidenceservice.mapper;

import com.nomolestar.evidenceservice.dto.EvidenceCreateDTO;
import com.nomolestar.evidenceservice.dto.EvidenceCustodyHistoryResponseDTO;
import com.nomolestar.evidenceservice.dto.EvidenceResponseDTO;
import com.nomolestar.evidenceservice.dto.EvidenceUpdateDTO;
import com.nomolestar.evidenceservice.model.EvidenceCustodyHistoryEntity;
import com.nomolestar.evidenceservice.model.EvidenceEntity;

public class EvidenceMapper {

    public static EvidenceEntity toEntity(EvidenceCreateDTO dto) {
        return EvidenceEntity.builder()
                .caseId(dto.caseId())
                .evidenceType(dto.evidenceType())
                .description(dto.description())
                .locationFound(dto.locationFound())
                .dateCollected(dto.dateCollected())
                .collectedBy(dto.collectedBy())
                .fileUrl(dto.fileUrl())
                .custodyStatus(dto.custodyStatus())
                .currentCustodian(dto.currentCustodian())
                .build();
    }

    public static void updateEntity(EvidenceEntity entity, EvidenceUpdateDTO dto) {
        entity.setEvidenceType(dto.evidenceType());
        entity.setDescription(dto.description());
        entity.setLocationFound(dto.locationFound());
        entity.setDateCollected(dto.dateCollected());
        entity.setCollectedBy(dto.collectedBy());
        entity.setFileUrl(dto.fileUrl());
        entity.setCustodyStatus(dto.custodyStatus());
        entity.setCurrentCustodian(dto.currentCustodian());
    }

    public static EvidenceResponseDTO toResponse(EvidenceEntity entity) {
        return new EvidenceResponseDTO(
                entity.getEvidenceId(),
                entity.getCaseId(),
                entity.getEvidenceType(),
                entity.getDescription(),
                entity.getLocationFound(),
                entity.getDateCollected(),
                entity.getCollectedBy(),
                entity.getFileUrl(),
                entity.getCustodyStatus(),
                entity.getCurrentCustodian()
        );
    }

    public static EvidenceCustodyHistoryResponseDTO toCustodyHistoryResponse(
            EvidenceCustodyHistoryEntity entity
    ) {
        return new EvidenceCustodyHistoryResponseDTO(
                entity.getHistoryId(),
                entity.getEvidenceId(),
                entity.getPreviousCustodian(),
                entity.getNewCustodian(),
                entity.getReason(),
                entity.getTransferredAt()
        );
    }
}
