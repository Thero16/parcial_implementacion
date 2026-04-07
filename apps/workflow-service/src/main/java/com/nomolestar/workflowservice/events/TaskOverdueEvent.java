package com.nomolestar.workflowservice.events;

import java.time.LocalDateTime;

public record TaskOverdueEvent(
        Integer taskId,
        String title,
        Integer caseId,
        String assignedTo,
        LocalDateTime dueDate
) {}
