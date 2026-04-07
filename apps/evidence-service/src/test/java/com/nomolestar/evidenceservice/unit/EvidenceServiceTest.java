package com.nomolestar.evidenceservice.unit;

import com.nomolestar.evidenceservice.dto.EvidenceResponseDTO;
import com.nomolestar.evidenceservice.dto.EvidenceUpdateDTO;
import com.nomolestar.evidenceservice.enums.CustodyStatus;
import com.nomolestar.evidenceservice.enums.EvidenceType;
import com.nomolestar.evidenceservice.exceptions.ResourceNotFoundException;
import com.nomolestar.evidenceservice.messaging.EvidenceEventPublisher;
import com.nomolestar.evidenceservice.model.EvidenceCustodyHistoryEntity;
import com.nomolestar.evidenceservice.model.EvidenceEntity;
import com.nomolestar.evidenceservice.repository.EvidenceCustodyHistoryRepository;
import com.nomolestar.evidenceservice.repository.EvidenceRepository;
import com.nomolestar.evidenceservice.service.EvidenceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EvidenceServiceTest {

    @Mock private EvidenceRepository evidenceRepository;
    @Mock private EvidenceCustodyHistoryRepository custodyHistoryRepository;
    @Mock private EvidenceEventPublisher evidenceEventPublisher;
    @Mock private WebClient.Builder webClientBuilder;
    @Mock private WebClient webClient;

    private EvidenceService evidenceService;
    private EvidenceEntity evidenceEntity;

    @BeforeEach
    void setUp() {
        when(webClientBuilder.build()).thenReturn(webClient);
        evidenceService = new EvidenceService(
                evidenceRepository, custodyHistoryRepository,
                webClientBuilder, evidenceEventPublisher);

        evidenceEntity = EvidenceEntity.builder()
                .evidenceId(1)
                .caseId(10)
                .evidenceType(EvidenceType.PHOTO)
                .description("A test photo")
                .locationFound("Crime scene")
                .dateCollected(LocalDateTime.now())
                .collectedBy("Officer Jones")
                .custodyStatus(CustodyStatus.STORED)
                .currentCustodian("Lab A")
                .build();
    }

    @Test
    void findAll_returnsList() {
        when(evidenceRepository.findAll()).thenReturn(List.of(evidenceEntity));
        List<EvidenceResponseDTO> result = evidenceService.findAll();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).description()).isEqualTo("A test photo");
    }

    @Test
    void findAll_emptyList_returnsEmpty() {
        when(evidenceRepository.findAll()).thenReturn(List.of());
        assertThat(evidenceService.findAll()).isEmpty();
    }

    @Test
    void findById_found_returnsDTO() {
        when(evidenceRepository.findById(1)).thenReturn(Optional.of(evidenceEntity));
        EvidenceResponseDTO result = evidenceService.findById(1);
        assertThat(result.evidenceId()).isEqualTo(1);
    }

    @Test
    void findById_notFound_throwsException() {
        when(evidenceRepository.findById(99)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> evidenceService.findById(99))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void findByCaseId_returnsList() {
        when(evidenceRepository.findByCaseId(10)).thenReturn(List.of(evidenceEntity));
        List<EvidenceResponseDTO> result = evidenceService.findByCaseId(10);
        assertThat(result).hasSize(1);
        assertThat(result.get(0).caseId()).isEqualTo(10);
    }

    @Test
    void getCustodyHistory_evidenceNotFound_throwsException() {
        when(evidenceRepository.existsById(99)).thenReturn(false);
        assertThatThrownBy(() -> evidenceService.getCustodyHistory(99))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void getCustodyHistory_found_returnsList() {
        EvidenceCustodyHistoryEntity history = EvidenceCustodyHistoryEntity.builder()
                .historyId(1)
                .evidenceId(1)
                .previousCustodian("NONE")
                .newCustodian("Lab A")
                .reason("Initial custody")
                .transferredAt(LocalDateTime.now())
                .build();
        when(evidenceRepository.existsById(1)).thenReturn(true);
        when(custodyHistoryRepository.findByEvidenceIdOrderByTransferredAtDesc(1))
                .thenReturn(List.of(history));

        var result = evidenceService.getCustodyHistory(1);
        assertThat(result).hasSize(1);
    }

    @Test
    void updateById_found_updatesAndReturns() {
        EvidenceUpdateDTO dto = new EvidenceUpdateDTO(
                EvidenceType.DOCUMENT, "Updated desc", "New Location",
                LocalDateTime.now(), "Officer New", null,
                CustodyStatus.IN_ANALYSIS, "Lab B", null);
        when(evidenceRepository.findById(1)).thenReturn(Optional.of(evidenceEntity));
        when(evidenceRepository.save(any())).thenReturn(evidenceEntity);

        EvidenceResponseDTO result = evidenceService.updateById(1, dto);
        assertThat(result).isNotNull();
    }

    @Test
    void updateById_notFound_throwsException() {
        EvidenceUpdateDTO dto = new EvidenceUpdateDTO(
                EvidenceType.PHOTO, "Desc", "Location",
                LocalDateTime.now(), "Officer", null,
                CustodyStatus.STORED, "Lab A", null);
        when(evidenceRepository.findById(99)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> evidenceService.updateById(99, dto))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void deleteById_found_deletes() {
        when(evidenceRepository.findById(1)).thenReturn(Optional.of(evidenceEntity));
        doNothing().when(evidenceRepository).delete(evidenceEntity);

        evidenceService.deleteById(1);

        verify(evidenceRepository, times(1)).delete(evidenceEntity);
    }

    @Test
    void deleteById_notFound_throwsException() {
        when(evidenceRepository.findById(99)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> evidenceService.deleteById(99))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
