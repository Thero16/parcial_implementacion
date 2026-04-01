package com.nomolestar.evidenceservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "evidence_custody_history")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EvidenceCustodyHistoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "history_id", nullable = false)
    private Integer historyId;

    @Column(name = "evidence_id", nullable = false)
    private Integer evidenceId;

    @Column(name = "previous_custodian", nullable = false)
    private String previousCustodian;

    @Column(name = "new_custodian", nullable = false)
    private String newCustodian;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String reason;

    @Column(name = "transferred_at", nullable = false)
    private LocalDateTime transferredAt;

}