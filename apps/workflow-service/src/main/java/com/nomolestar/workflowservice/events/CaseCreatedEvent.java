package com.nomolestar.workflowservice.events;

public record CaseCreatedEvent(
        Integer caseId,
        String title,
        String assignedDetective,
        String status
) {}
