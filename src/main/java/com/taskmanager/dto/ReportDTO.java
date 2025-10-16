package com.taskmanager.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Data Transfer Object for work item reports.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReportDTO {
    private Long userId;
    private String username;
    private LocalDateTime generatedAt;
    private long totalItems;
    private long todoCount;
    private long inProgressCount;
    private long completedCount;
    private long overdueCount;
    private List<WorkItemDTO> overdueItems;

    /**
     * Converts the report to a text format suitable for email.
     *
     * @return formatted text report
     */
    public String toTextReport() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== Task Management Report ===\n\n");
        sb.append("User: ").append(username).append("\n");
        sb.append("Generated: ").append(generatedAt).append("\n\n");
        sb.append("Summary:\n");
        sb.append("  Total Items: ").append(totalItems).append("\n");
        sb.append("  To Do: ").append(todoCount).append("\n");
        sb.append("  In Progress: ").append(inProgressCount).append("\n");
        sb.append("  Completed: ").append(completedCount).append("\n");
        sb.append("  Overdue: ").append(overdueCount).append("\n\n");

        if (overdueCount > 0) {
            sb.append("Overdue Items:\n");
            overdueItems.forEach(item -> {
                sb.append("  - ").append(item.getTitle())
                  .append(" (Deadline: ").append(item.getDeadline()).append(")\n");
            });
        }

        return sb.toString();
    }
}