package com.nomolestar.caseservice.unit;

import com.nomolestar.caseservice.dto.CaseCreateDTO;
import com.nomolestar.caseservice.dto.CaseResponseDTO;
import com.nomolestar.caseservice.dto.CaseUpdateDTO;
import com.nomolestar.caseservice.enums.CasePriority;
import com.nomolestar.caseservice.enums.InvestigationStatus;
import com.nomolestar.caseservice.exceptions.ResourceNotFoundException;
import com.nomolestar.caseservice.messaging.CaseEventPublisher;
import com.nomolestar.caseservice.model.CaseEntity;
import com.nomolestar.caseservice.repository.CaseRepository;
import com.nomolestar.caseservice.service.CaseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CaseServiceTest {

    @Mock private CaseRepository caseRepository;
    @Mock private CaseEventPublisher caseEventPublisher;

    private CaseService caseService;
    private CaseEntity caseEntity;

    @BeforeEach
    void setUp() {
        caseService = new CaseService(caseRepository, caseEventPublisher);

        caseEntity = CaseEntity.builder()
                .id(1)
                .title("Test Case")
                .description("A test case description")
                .status(InvestigationStatus.OPEN)
                .priority(CasePriority.HIGH)
                .assignedDetective("Detective Smith")
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    void findAll_returnsList() {
        when(caseRepository.findAll()).thenReturn(List.of(caseEntity));
        List<CaseResponseDTO> result = caseService.findAll();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).title()).isEqualTo("Test Case");
    }

    @Test
    void findAll_emptyList_returnsEmpty() {
        when(caseRepository.findAll()).thenReturn(List.of());
        List<CaseResponseDTO> result = caseService.findAll();
        assertThat(result).isEmpty();
    }

    @Test
    void findById_found_returnsDTO() {
        when(caseRepository.findById(1)).thenReturn(Optional.of(caseEntity));
        CaseResponseDTO result = caseService.findById(1);
        assertThat(result.id()).isEqualTo(1);
        assertThat(result.title()).isEqualTo("Test Case");
    }

    @Test
    void findById_notFound_throwsException() {
        when(caseRepository.findById(99)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> caseService.findById(99))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void create_savesAndPublishesEvent() {
        CaseCreateDTO dto = new CaseCreateDTO(
                "New Case", "Description", InvestigationStatus.OPEN,
                CasePriority.MEDIUM, "Detective Jones", LocalDateTime.now());
        when(caseRepository.save(any())).thenReturn(caseEntity);
        doNothing().when(caseEventPublisher).publishCaseCreated(any());

        CaseResponseDTO result = caseService.create(dto);

        assertThat(result).isNotNull();
        verify(caseEventPublisher, times(1)).publishCaseCreated(any());
    }

    @Test
    void updateById_found_updatesAndReturns() {
        CaseUpdateDTO dto = new CaseUpdateDTO(
                "Updated Title", "Updated desc",
                InvestigationStatus.IN_PROGRESS, CasePriority.HIGH, "Detective Smith");
        when(caseRepository.findById(1)).thenReturn(Optional.of(caseEntity));
        when(caseRepository.save(any())).thenReturn(caseEntity);

        CaseResponseDTO result = caseService.updateById(1, dto);
        assertThat(result).isNotNull();
    }

    @Test
    void updateById_notFound_throwsException() {
        CaseUpdateDTO dto = new CaseUpdateDTO(
                "Title", "desc", InvestigationStatus.OPEN, CasePriority.LOW, "Det");
        when(caseRepository.findById(99)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> caseService.updateById(99, dto))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void deleteById_found_deletes() {
        when(caseRepository.findById(1)).thenReturn(Optional.of(caseEntity));
        doNothing().when(caseRepository).delete(caseEntity);

        caseService.deleteById(1);

        verify(caseRepository, times(1)).delete(caseEntity);
    }

    @Test
    void deleteById_notFound_throwsException() {
        when(caseRepository.findById(99)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> caseService.deleteById(99))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
