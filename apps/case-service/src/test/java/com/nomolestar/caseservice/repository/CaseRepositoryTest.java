package com.nomolestar.caseservice.repository;

import com.nomolestar.caseservice.enums.CasePriority;
import com.nomolestar.caseservice.enums.InvestigationStatus;
import com.nomolestar.caseservice.model.CaseEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class CaseRepositoryTest {

    @Autowired
    private CaseRepository caseRepository;

    private CaseEntity buildCase(String title, InvestigationStatus status) {
        return CaseEntity.builder()
                .title(title)
                .description("Test case description")
                .status(status)
                .priority(CasePriority.MEDIUM)
                .assignedDetective("detective1")
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    void save_andFindById_works() {
        CaseEntity saved = caseRepository.save(buildCase("Murder at the mansion", InvestigationStatus.OPEN));
        Optional<CaseEntity> found = caseRepository.findById(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getTitle()).isEqualTo("Murder at the mansion");
    }

    @Test
    void findAll_returnsAllCases() {
        caseRepository.save(buildCase("Case A", InvestigationStatus.OPEN));
        caseRepository.save(buildCase("Case B", InvestigationStatus.IN_PROGRESS));
        assertThat(caseRepository.findAll()).hasSize(2);
    }

    @Test
    void save_persistsAllFields() {
        CaseEntity saved = caseRepository.save(buildCase("Robbery", InvestigationStatus.IN_PROGRESS));
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getStatus()).isEqualTo(InvestigationStatus.IN_PROGRESS);
        assertThat(saved.getPriority()).isEqualTo(CasePriority.MEDIUM);
        assertThat(saved.getAssignedDetective()).isEqualTo("detective1");
    }

    @Test
    void deleteById_removesCase() {
        CaseEntity saved = caseRepository.save(buildCase("Cold case", InvestigationStatus.CLOSED));
        caseRepository.deleteById(saved.getId());
        assertThat(caseRepository.findById(saved.getId())).isEmpty();
    }

    @Test
    void update_changesFields() {
        CaseEntity saved = caseRepository.save(buildCase("Open case", InvestigationStatus.OPEN));
        saved.setStatus(InvestigationStatus.CLOSED);
        CaseEntity updated = caseRepository.save(saved);
        assertThat(updated.getStatus()).isEqualTo(InvestigationStatus.CLOSED);
    }
}
