package com.nomolestar.workflowservice.unit;

import com.nomolestar.workflowservice.dto.TaskCreateDTO;
import com.nomolestar.workflowservice.dto.TaskResponseDTO;
import com.nomolestar.workflowservice.dto.TaskUpdateDTO;
import com.nomolestar.workflowservice.enums.TaskPriority;
import com.nomolestar.workflowservice.enums.TaskStatus;
import com.nomolestar.workflowservice.exceptions.ResourceNotFoundException;
import com.nomolestar.workflowservice.messaging.TaskEventPublisher;
import com.nomolestar.workflowservice.model.TaskEntity;
import com.nomolestar.workflowservice.repository.TaskRepository;
import com.nomolestar.workflowservice.service.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private TaskEventPublisher taskEventPublisher;

    private TaskService taskService;

    private TaskEntity sampleTask;

    @BeforeEach
    void setUp() {
        taskService = new TaskService(taskRepository, taskEventPublisher);

        sampleTask = new TaskEntity();
        sampleTask.setId(1);
        sampleTask.setTitle("Test Task");
        sampleTask.setDescription("Test Description");
        sampleTask.setCaseId(1);
        sampleTask.setStatus(TaskStatus.PENDING);
        sampleTask.setPriority(TaskPriority.HIGH);
        sampleTask.setAssignedTo("detective_smith");
        sampleTask.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void findAll_returnsAllTasks() {
        when(taskRepository.findAll()).thenReturn(List.of(sampleTask));

        List<TaskResponseDTO> result = taskService.findAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).title()).isEqualTo("Test Task");
    }

    @Test
    void findById_found_returnsTask() {
        when(taskRepository.findById(1)).thenReturn(Optional.of(sampleTask));

        TaskResponseDTO result = taskService.findById(1);

        assertThat(result.id()).isEqualTo(1);
        assertThat(result.title()).isEqualTo("Test Task");
    }

    @Test
    void findById_notFound_throwsException() {
        when(taskRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.findById(99))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void create_withAssignedTo_publishesEvent() {
        TaskCreateDTO dto = new TaskCreateDTO("New Task", "Desc", 1, TaskPriority.HIGH, "detective_jones", null);

        TaskEntity saved = new TaskEntity();
        saved.setId(2);
        saved.setTitle("New Task");
        saved.setDescription("Desc");
        saved.setCaseId(1);
        saved.setStatus(TaskStatus.PENDING);
        saved.setPriority(TaskPriority.HIGH);
        saved.setAssignedTo("detective_jones");
        saved.setCreatedAt(LocalDateTime.now());

        when(taskRepository.save(any())).thenReturn(saved);

        TaskResponseDTO result = taskService.create(dto);

        assertThat(result.title()).isEqualTo("New Task");
        verify(taskEventPublisher, times(1)).publishTaskAssigned(any());
    }

    @Test
    void create_withoutAssignedTo_doesNotPublishEvent() {
        TaskCreateDTO dto = new TaskCreateDTO("New Task", "Desc", 1, TaskPriority.MEDIUM, null, null);

        TaskEntity saved = new TaskEntity();
        saved.setId(3);
        saved.setTitle("New Task");
        saved.setCaseId(1);
        saved.setStatus(TaskStatus.PENDING);
        saved.setPriority(TaskPriority.MEDIUM);
        saved.setCreatedAt(LocalDateTime.now());

        when(taskRepository.save(any())).thenReturn(saved);

        taskService.create(dto);

        verify(taskEventPublisher, never()).publishTaskAssigned(any());
    }

    @Test
    void updateById_found_updatesTask() {
        TaskUpdateDTO dto = new TaskUpdateDTO("Updated Title", null, TaskStatus.IN_PROGRESS, null, null, null);

        when(taskRepository.findById(1)).thenReturn(Optional.of(sampleTask));
        when(taskRepository.save(any())).thenReturn(sampleTask);

        TaskResponseDTO result = taskService.updateById(1, dto);

        assertThat(result).isNotNull();
        verify(taskRepository).save(any());
    }

    @Test
    void updateById_notFound_throwsException() {
        TaskUpdateDTO dto = new TaskUpdateDTO("Title", null, null, null, null, null);
        when(taskRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.updateById(99, dto))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void deleteById_found_deletes() {
        when(taskRepository.findById(1)).thenReturn(Optional.of(sampleTask));

        taskService.deleteById(1);

        verify(taskRepository).delete(sampleTask);
    }

    @Test
    void deleteById_notFound_throwsException() {
        when(taskRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.deleteById(99))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
