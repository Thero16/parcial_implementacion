package com.nomolestar.caseservice.events;

public record CaseCreatedEvent(
        Integer caseId,
        String title,
        String assignedDetective,
        String status
) {
}
