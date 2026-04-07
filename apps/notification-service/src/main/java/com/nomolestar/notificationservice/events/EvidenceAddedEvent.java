package com.nomolestar.notificationservice.events;

public record EvidenceAddedEvent(
        Integer evidenceId,
        String evidenceType,
        Integer caseId,
        String description
) {}
