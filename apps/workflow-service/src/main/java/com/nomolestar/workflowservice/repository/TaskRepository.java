package com.nomolestar.workflowservice.repository;

import com.nomolestar.workflowservice.enums.TaskStatus;
import com.nomolestar.workflowservice.model.TaskEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<TaskEntity, Integer> {
    List<TaskEntity> findByCaseId(Integer caseId);
    List<TaskEntity> findByDueDateBeforeAndStatusNotIn(LocalDateTime now, List<TaskStatus> excludedStatuses);
}
