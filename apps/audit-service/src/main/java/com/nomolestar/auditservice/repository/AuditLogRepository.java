package com.nomolestar.auditservice.repository;

import com.nomolestar.auditservice.model.AuditLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLogEntity, Integer> {
    List<AuditLogEntity> findByEventType(String eventType);
}
