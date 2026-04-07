package com.nomolestar.evidenceservice.events;

public record EvidenceAddedEvent(
        Integer evidenceId,
        Integer caseId,
        String evidenceType,
        String collectedBy
) {
}
