package com.nomolestar.workflowservice.mapper;

import com.nomolestar.workflowservice.dto.TaskCreateDTO;
import com.nomolestar.workflowservice.dto.TaskResponseDTO;
import com.nomolestar.workflowservice.dto.TaskUpdateDTO;
import com.nomolestar.workflowservice.enums.TaskStatus;
import com.nomolestar.workflowservice.model.TaskEntity;

import java.time.LocalDateTime;

public class TaskMapper {

    private TaskMapper() {}

    public static TaskResponseDTO toResponseDTO(TaskEntity entity) {
        return new TaskResponseDTO(
                entity.getId(),
                entity.getTitle(),
                entity.getDescription(),
                entity.getCaseId(),
                entity.getStatus(),
                entity.getPriority(),
                entity.getAssignedTo(),
                entity.getDueDate(),
                entity.getCreatedAt()
        );
    }

    public static TaskEntity toEntity(TaskCreateDTO dto) {
        TaskEntity entity = new TaskEntity();
        entity.setTitle(dto.title());
        entity.setDescription(dto.description());
        entity.setCaseId(dto.caseId());
        entity.setStatus(TaskStatus.PENDING);
        entity.setPriority(dto.priority());
        entity.setAssignedTo(dto.assignedTo());
        entity.setDueDate(dto.dueDate());
        entity.setCreatedAt(LocalDateTime.now());
        return entity;
    }

    public static void updateEntity(TaskEntity entity, TaskUpdateDTO dto) {
        if (dto.title() != null) entity.setTitle(dto.title());
        if (dto.description() != null) entity.setDescription(dto.description());
        if (dto.status() != null) entity.setStatus(dto.status());
        if (dto.priority() != null) entity.setPriority(dto.priority());
        if (dto.assignedTo() != null) entity.setAssignedTo(dto.assignedTo());
        if (dto.dueDate() != null) entity.setDueDate(dto.dueDate());
    }
}
