package com.nomolestar.evidenceservice.repository;

import com.nomolestar.evidenceservice.model.EvidenceCustodyHistoryEntity;
import com.nomolestar.evidenceservice.model.EvidenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EvidenceCustodyHistoryRepository extends JpaRepository<EvidenceCustodyHistoryEntity, Integer> {

    List<EvidenceCustodyHistoryEntity> findByEvidenceIdOrderByTransferredAtDesc(
            Integer evidenceId
    );
}
