package com.nomolestar.evidenceservice.model;

import com.nomolestar.evidenceservice.enums.CustodyStatus;
import com.nomolestar.evidenceservice.enums.EvidenceType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "evidences")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EvidenceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "evidence_id", nullable = false)
    private Integer evidenceId;

    @Column(name = "case_id", nullable = false)
    private Integer caseId;

    @Enumerated(EnumType.STRING)
    @Column(name = "evidence_type", nullable = false)
    private EvidenceType evidenceType;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(name = "location_found", nullable = false)
    private String locationFound;

    @Column(name = "date_collected", nullable = false)
    private LocalDateTime dateCollected;

    @Column(name = "collected_by", nullable = false)
    private String collectedBy;

    @Column(name = "file_url")
    private String fileUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "custody_status", nullable = false)
    private CustodyStatus custodyStatus;

    @Column(name = "current_custodian", nullable = false)
    private String currentCustodian;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "evidence_id")
    private List<EvidenceCustodyHistoryEntity> custodyHistory;
}
