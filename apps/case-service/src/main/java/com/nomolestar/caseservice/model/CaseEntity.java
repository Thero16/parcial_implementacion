package com.nomolestar.caseservice.model;

import com.nomolestar.caseservice.enums.CasePriority;
import com.nomolestar.caseservice.enums.InvestigationStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "cases")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InvestigationStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CasePriority priority;

    @Column(nullable = false)
    private String assignedDetective;

    @Column(nullable = false)
    private LocalDateTime createdAt;
}
