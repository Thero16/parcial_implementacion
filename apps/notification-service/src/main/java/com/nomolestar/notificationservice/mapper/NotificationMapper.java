package com.nomolestar.notificationservice.mapper;

import com.nomolestar.notificationservice.dto.NotificationResponseDTO;
import com.nomolestar.notificationservice.model.NotificationEntity;

public class NotificationMapper {

    private NotificationMapper() {}

    public static NotificationResponseDTO toResponseDTO(NotificationEntity entity) {
        return new NotificationResponseDTO(
                entity.getId(),
                entity.getMessage(),
                entity.getType(),
                entity.getCaseId(),
                entity.isRead(),
                entity.getCreatedAt()
        );
    }
}
