package com.taskmanager.dto;

import com.taskmanager.model.WorkItemStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * Data Transfer Object for WorkItem entity.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WorkItemDTO {
    private Long id;
    private String title;
    private String description;
    private WorkItemStatus status;
    private LocalDateTime deadline;
    private Integer priority;
    private Set<LabelDTO> labels;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime completedAt;
    private boolean overdue;
}