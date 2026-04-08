package com.nomolestar.workflowservice.repository;

import com.nomolestar.workflowservice.enums.TaskPriority;
import com.nomolestar.workflowservice.enums.TaskStatus;
import com.nomolestar.workflowservice.model.TaskEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class TaskRepositoryTest {

    @Autowired
    private TaskRepository taskRepository;

    private TaskEntity buildTask(String title, Integer caseId, TaskStatus status, LocalDateTime dueDate) {
        return TaskEntity.builder()
                .title(title)
                .description("Test task")
                .caseId(caseId)
                .status(status)
                .priority(TaskPriority.MEDIUM)
                .assignedTo("detective1")
                .dueDate(dueDate)
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    void save_andFindById_works() {
        TaskEntity saved = taskRepository.save(
                buildTask("Interview witness", 1, TaskStatus.PENDING, LocalDateTime.now().plusDays(1)));
        Optional<TaskEntity> found = taskRepository.findById(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getTitle()).isEqualTo("Interview witness");
    }

    @Test
    void findAll_returnsAllTasks() {
        taskRepository.save(buildTask("Task A", 1, TaskStatus.PENDING, LocalDateTime.now().plusDays(1)));
        taskRepository.save(buildTask("Task B", 2, TaskStatus.IN_PROGRESS, LocalDateTime.now().plusDays(2)));
        assertThat(taskRepository.findAll()).hasSize(2);
    }

    @Test
    void findByCaseId_returnsMatchingTasks() {
        taskRepository.save(buildTask("Task A", 1, TaskStatus.PENDING, LocalDateTime.now().plusDays(1)));
        taskRepository.save(buildTask("Task B", 1, TaskStatus.IN_PROGRESS, LocalDateTime.now().plusDays(2)));
        taskRepository.save(buildTask("Task C", 2, TaskStatus.PENDING, LocalDateTime.now().plusDays(3)));

        List<TaskEntity> result = taskRepository.findByCaseId(1);
        assertThat(result).hasSize(2);
        assertThat(result).allMatch(t -> t.getCaseId().equals(1));
    }

    @Test
    void findByCaseId_noMatch_returnsEmpty() {
        taskRepository.save(buildTask("Task A", 1, TaskStatus.PENDING, LocalDateTime.now().plusDays(1)));
        List<TaskEntity> result = taskRepository.findByCaseId(999);
        assertThat(result).isEmpty();
    }

    @Test
    void findByDueDateBeforeAndStatusNotIn_returnsOverdueTasks() {
        LocalDateTime pastDate = LocalDateTime.now().minusDays(1);
        LocalDateTime futureDate = LocalDateTime.now().plusDays(1);

        taskRepository.save(buildTask("Overdue task", 1, TaskStatus.PENDING, pastDate));
        taskRepository.save(buildTask("Future task", 1, TaskStatus.PENDING, futureDate));
        taskRepository.save(buildTask("Completed past task", 1, TaskStatus.COMPLETED, pastDate));

        List<TaskEntity> result = taskRepository.findByDueDateBeforeAndStatusNotIn(
                LocalDateTime.now(), List.of(TaskStatus.COMPLETED, TaskStatus.OVERDUE));

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("Overdue task");
    }

    @Test
    void deleteById_removesTask() {
        TaskEntity saved = taskRepository.save(
                buildTask("Task to delete", 1, TaskStatus.PENDING, LocalDateTime.now().plusDays(1)));
        taskRepository.deleteById(saved.getId());
        assertThat(taskRepository.findById(saved.getId())).isEmpty();
    }
}
