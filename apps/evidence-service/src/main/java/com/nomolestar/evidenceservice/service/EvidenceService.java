package com.nomolestar.evidenceservice.service;

import com.nomolestar.evidenceservice.dto.EvidenceCreateDTO;
import com.nomolestar.evidenceservice.dto.EvidenceCustodyHistoryResponseDTO;
import com.nomolestar.evidenceservice.dto.EvidenceResponseDTO;
import com.nomolestar.evidenceservice.dto.EvidenceUpdateDTO;
import com.nomolestar.evidenceservice.events.EvidenceAddedEvent;
import com.nomolestar.evidenceservice.exceptions.ResourceNotFoundException;
import com.nomolestar.evidenceservice.mapper.EvidenceMapper;
import com.nomolestar.evidenceservice.messaging.EvidenceEventPublisher;
import com.nomolestar.evidenceservice.model.EvidenceCustodyHistoryEntity;
import com.nomolestar.evidenceservice.model.EvidenceEntity;
import com.nomolestar.evidenceservice.repository.EvidenceCustodyHistoryRepository;
import com.nomolestar.evidenceservice.repository.EvidenceRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class EvidenceService {

    private final EvidenceRepository evidenceRepository;
    private final EvidenceCustodyHistoryRepository custodyHistoryRepository;
    private final WebClient webClient;
    private final EvidenceEventPublisher evidenceEventPublisher;

    @Value("${services.case-service.url}")
    private String caseServiceUrl;

    public EvidenceService(EvidenceRepository evidenceRepository,
                           EvidenceCustodyHistoryRepository custodyHistoryRepository,
                           WebClient.Builder webClientBuilder,
                           EvidenceEventPublisher evidenceEventPublisher) {
        this.evidenceRepository = evidenceRepository;
        this.custodyHistoryRepository = custodyHistoryRepository;
        this.webClient = webClientBuilder.build();
        this.evidenceEventPublisher = evidenceEventPublisher;
    }

    public List<EvidenceResponseDTO> findAll() {
        return evidenceRepository.findAll()
                .stream()
                .map(EvidenceMapper::toResponse)
                .toList();
    }

    public EvidenceResponseDTO findById(Integer id) {
        EvidenceEntity evidence = evidenceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Evidence with id " + id + " not found"
                ));

        return EvidenceMapper.toResponse(evidence);
    }

    public List<EvidenceResponseDTO> findByCaseId(Integer caseId) {
        return evidenceRepository.findByCaseId(caseId)
                .stream()
                .map(EvidenceMapper::toResponse)
                .toList();
    }

    public List<EvidenceCustodyHistoryResponseDTO> getCustodyHistory(Integer evidenceId) {

        if (!evidenceRepository.existsById(evidenceId)) {
            throw new ResourceNotFoundException(
                    "Evidence with id " + evidenceId + " not found"
            );
        }

        return custodyHistoryRepository.findByEvidenceIdOrderByTransferredAtDesc(evidenceId)
                .stream()
                .map(EvidenceMapper::toCustodyHistoryResponse)
                .toList();
    }

    public EvidenceResponseDTO create(EvidenceCreateDTO dto, String token) {
        validateCaseExists(dto.caseId(), token);

        EvidenceEntity evidence = EvidenceMapper.toEntity(dto);

        EvidenceEntity savedEvidence = evidenceRepository.save(evidence);

        EvidenceCustodyHistoryEntity initialHistory = EvidenceCustodyHistoryEntity.builder()
                .evidenceId(savedEvidence.getEvidenceId())
                .previousCustodian("NONE")
                .newCustodian(savedEvidence.getCurrentCustodian())
                .reason("Initial custody assignment")
                .transferredAt(LocalDateTime.now())
                .build();

        custodyHistoryRepository.save(initialHistory);

        EvidenceAddedEvent event = new EvidenceAddedEvent(
                savedEvidence.getEvidenceId(),
                savedEvidence.getCaseId(),
                savedEvidence.getEvidenceType().name(),
                savedEvidence.getCollectedBy()
        );
        evidenceEventPublisher.publishEvidenceAdded(event);

        return EvidenceMapper.toResponse(savedEvidence);
    }

    public EvidenceResponseDTO updateById(Integer id, EvidenceUpdateDTO dto) {
        EvidenceEntity evidence = evidenceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Evidence with id " + id + " not found"
                ));

        String previousCustodian = evidence.getCurrentCustodian();

        boolean custodianChanged = !previousCustodian.equals(dto.currentCustodian());

        EvidenceMapper.updateEntity(evidence, dto);

        EvidenceEntity updatedEvidence = evidenceRepository.save(evidence);

        if (custodianChanged) {
            EvidenceCustodyHistoryEntity history = EvidenceCustodyHistoryEntity.builder()
                    .evidenceId(updatedEvidence.getEvidenceId())
                    .previousCustodian(previousCustodian)
                    .newCustodian(updatedEvidence.getCurrentCustodian())
                    .reason(
                            dto.transferReason() != null && !dto.transferReason().isBlank()
                                    ? dto.transferReason()
                                    : "Custodian changed"
                    )
                    .transferredAt(LocalDateTime.now())
                    .build();

            custodyHistoryRepository.save(history);
        }

        return EvidenceMapper.toResponse(updatedEvidence);
    }

    public void deleteById(Integer id) {
        EvidenceEntity evidence = evidenceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Evidence with id " + id + " not found"
                ));

        evidenceRepository.delete(evidence);
    }

    private void validateCaseExists(Integer caseId, String token) {
        try {
            webClient.get()
                    .uri(caseServiceUrl + "/" + caseId)
                    .header("Authorization", token)
                    .retrieve()
                    .toBodilessEntity()
                    .block();

        } catch (WebClientResponseException.NotFound e) {
            throw new ResourceNotFoundException(
                    "Case with id " + caseId + " not found"
            );

        } catch (WebClientResponseException.Forbidden e) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You do not have permission to access the case service"
            );

        } catch (WebClientResponseException.Unauthorized e) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Authentication token is invalid or missing"
            );

        } catch (Exception e) {
            throw new RuntimeException(
                    "Error communicating with Case Service: " + e.getMessage(),
                    e
            );
        }
    }
}
