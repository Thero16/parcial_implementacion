package com.nomolestar.notificationservice.events;

public record TaskAssignedEvent(
        Integer taskId,
        String title,
        Integer caseId,
        String assignedTo
) {}
