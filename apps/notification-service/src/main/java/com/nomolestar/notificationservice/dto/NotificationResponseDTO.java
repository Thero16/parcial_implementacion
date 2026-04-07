package com.nomolestar.notificationservice.dto;

import com.nomolestar.notificationservice.enums.NotificationType;

import java.time.LocalDateTime;

public record NotificationResponseDTO(
        Integer id,
        String message,
        NotificationType type,
        Integer caseId,
        boolean read,
        LocalDateTime createdAt
) {}
