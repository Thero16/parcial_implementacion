package com.nomolestar.workflowservice.events;

public record TaskAssignedEvent(
        Integer taskId,
        String title,
        Integer caseId,
        String assignedTo
) {}
