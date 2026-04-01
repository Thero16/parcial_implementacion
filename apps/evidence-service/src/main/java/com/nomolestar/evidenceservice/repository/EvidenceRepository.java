package com.nomolestar.evidenceservice.repository;

import com.nomolestar.evidenceservice.model.EvidenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EvidenceRepository extends JpaRepository<EvidenceEntity, Integer> {
    List<EvidenceEntity> findByCaseId(Integer caseId);

}
