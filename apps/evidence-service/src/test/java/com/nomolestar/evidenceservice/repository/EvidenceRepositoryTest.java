package com.nomolestar.evidenceservice.repository;

import com.nomolestar.evidenceservice.enums.CustodyStatus;
import com.nomolestar.evidenceservice.enums.EvidenceType;
import com.nomolestar.evidenceservice.model.EvidenceEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class EvidenceRepositoryTest {

    @Autowired
    private EvidenceRepository evidenceRepository;

    private EvidenceEntity buildEvidence(Integer caseId, EvidenceType type) {
        return EvidenceEntity.builder()
                .caseId(caseId)
                .evidenceType(type)
                .description("Test evidence")
                .locationFound("Scene of crime")
                .dateCollected(LocalDateTime.now())
                .collectedBy("detective1")
                .custodyStatus(CustodyStatus.COLLECTED)
                .currentCustodian("detective1")
                .build();
    }

    @Test
    void save_andFindById_works() {
        EvidenceEntity saved = evidenceRepository.save(buildEvidence(1, EvidenceType.PHOTO));
        Optional<EvidenceEntity> found = evidenceRepository.findById(saved.getEvidenceId());
        assertThat(found).isPresent();
        assertThat(found.get().getEvidenceType()).isEqualTo(EvidenceType.PHOTO);
    }

    @Test
    void findAll_returnsAllEvidence() {
        evidenceRepository.save(buildEvidence(1, EvidenceType.PHOTO));
        evidenceRepository.save(buildEvidence(2, EvidenceType.DNA));
        assertThat(evidenceRepository.findAll()).hasSize(2);
    }

    @Test
    void findByCaseId_returnsMatchingEvidence() {
        evidenceRepository.save(buildEvidence(1, EvidenceType.PHOTO));
        evidenceRepository.save(buildEvidence(1, EvidenceType.DNA));
        evidenceRepository.save(buildEvidence(2, EvidenceType.WEAPON));

        List<EvidenceEntity> result = evidenceRepository.findByCaseId(1);
        assertThat(result).hasSize(2);
        assertThat(result).allMatch(e -> e.getCaseId().equals(1));
    }

    @Test
    void findByCaseId_noMatch_returnsEmpty() {
        evidenceRepository.save(buildEvidence(1, EvidenceType.PHOTO));
        List<EvidenceEntity> result = evidenceRepository.findByCaseId(999);
        assertThat(result).isEmpty();
    }

    @Test
    void deleteById_removesEvidence() {
        EvidenceEntity saved = evidenceRepository.save(buildEvidence(1, EvidenceType.DOCUMENT));
        evidenceRepository.deleteById(saved.getEvidenceId());
        assertThat(evidenceRepository.findById(saved.getEvidenceId())).isEmpty();
    }
}
