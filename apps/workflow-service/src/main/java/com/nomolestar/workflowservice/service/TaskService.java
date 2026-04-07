package com.nomolestar.workflowservice.service;

import com.nomolestar.workflowservice.dto.TaskCreateDTO;
import com.nomolestar.workflowservice.dto.TaskResponseDTO;
import com.nomolestar.workflowservice.dto.TaskUpdateDTO;
import com.nomolestar.workflowservice.enums.TaskPriority;
import com.nomolestar.workflowservice.enums.TaskStatus;
import com.nomolestar.workflowservice.events.CaseCreatedEvent;
import com.nomolestar.workflowservice.events.TaskAssignedEvent;
import com.nomolestar.workflowservice.events.TaskOverdueEvent;
import com.nomolestar.workflowservice.exceptions.ResourceNotFoundException;
import com.nomolestar.workflowservice.mapper.TaskMapper;
import com.nomolestar.workflowservice.messaging.TaskEventPublisher;
import com.nomolestar.workflowservice.model.TaskEntity;
import com.nomolestar.workflowservice.repository.TaskRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final TaskEventPublisher taskEventPublisher;

    public TaskService(TaskRepository taskRepository, TaskEventPublisher taskEventPublisher) {
        this.taskRepository = taskRepository;
        this.taskEventPublisher = taskEventPublisher;
    }

    public List<TaskResponseDTO> findAll() {
        return taskRepository.findAll()
                .stream()
                .map(TaskMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    public TaskResponseDTO findById(Integer id) {
        TaskEntity entity = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task with id " + id + " not found"));
        return TaskMapper.toResponseDTO(entity);
    }

    public List<TaskResponseDTO> findByCaseId(Integer caseId) {
        return taskRepository.findByCaseId(caseId)
                .stream()
                .map(TaskMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    public TaskResponseDTO create(TaskCreateDTO dto) {
        TaskEntity entity = TaskMapper.toEntity(dto);
        TaskEntity saved = taskRepository.save(entity);

        if (saved.getAssignedTo() != null) {
            TaskAssignedEvent event = new TaskAssignedEvent(
                    saved.getId(),
                    saved.getTitle(),
                    saved.getCaseId(),
                    saved.getAssignedTo()
            );
            taskEventPublisher.publishTaskAssigned(event);
        }

        return TaskMapper.toResponseDTO(saved);
    }

    public TaskResponseDTO updateById(Integer id, TaskUpdateDTO dto) {
        TaskEntity entity = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task with id " + id + " not found"));

        String previousAssignee = entity.getAssignedTo();
        TaskMapper.updateEntity(entity, dto);
        TaskEntity updated = taskRepository.save(entity);

        if (dto.assignedTo() != null && !dto.assignedTo().equals(previousAssignee)) {
            TaskAssignedEvent event = new TaskAssignedEvent(
                    updated.getId(),
                    updated.getTitle(),
                    updated.getCaseId(),
                    updated.getAssignedTo()
            );
            taskEventPublisher.publishTaskAssigned(event);
        }

        return TaskMapper.toResponseDTO(updated);
    }

    public void deleteById(Integer id) {
        TaskEntity entity = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task with id " + id + " not found"));
        taskRepository.delete(entity);
    }

    @Scheduled(fixedRate = 60000)
    public void checkAndMarkOverdueTasks() {
        List<TaskStatus> excluded = List.of(TaskStatus.COMPLETED, TaskStatus.OVERDUE);
        List<TaskEntity> overdueTasks = taskRepository.findByDueDateBeforeAndStatusNotIn(LocalDateTime.now(), excluded);

        for (TaskEntity task : overdueTasks) {
            task.setStatus(TaskStatus.OVERDUE);
            taskRepository.save(task);

            TaskOverdueEvent event = new TaskOverdueEvent(
                    task.getId(),
                    task.getTitle(),
                    task.getCaseId(),
                    task.getAssignedTo(),
                    task.getDueDate()
            );
            taskEventPublisher.publishTaskOverdue(event);
        }
    }

    public void createDefaultTasksForCase(CaseCreatedEvent event) {
        String[] defaultTitles = {
                "Initial investigation review",
                "Collect evidence",
                "Interview witnesses"
        };

        for (String title : defaultTitles) {
            TaskEntity task = new TaskEntity();
            task.setTitle(title);
            task.setDescription("Default task created for case: " + event.title());
            task.setCaseId(event.caseId());
            task.setStatus(TaskStatus.PENDING);
            task.setPriority(TaskPriority.MEDIUM);
            task.setCreatedAt(LocalDateTime.now());
            taskRepository.save(task);
        }
    }
}
