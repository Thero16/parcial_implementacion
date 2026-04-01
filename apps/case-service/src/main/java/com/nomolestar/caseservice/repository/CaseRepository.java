package com.nomolestar.caseservice.repository;

import com.nomolestar.caseservice.model.CaseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CaseRepository extends JpaRepository<CaseEntity, Integer> {
}
