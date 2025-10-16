package com.taskmanager.service;

import com.taskmanager.dto.ReportDTO;
import com.taskmanager.dto.WorkItemDTO;
import com.taskmanager.model.User;
import com.taskmanager.model.WorkItemStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service for generating work item reports.
 */
@Service
@RequiredArgsConstructor
public class ReportService {

    private final WorkItemService workItemService;

    /**
     * Generates a comprehensive report for a user.
     *
     * @param user the user
     * @return the report DTO
     */
    @Transactional(readOnly = true)
    public ReportDTO generateReport(User user) {
        ReportDTO report = new ReportDTO();
        report.setUserId(user.getId());
        report.setUsername(user.getUsername());
        report.setGeneratedAt(LocalDateTime.now());

        // Get counts
        report.setTotalItems(workItemService.countTotal(user));
        report.setTodoCount(workItemService.countByStatus(user, WorkItemStatus.TODO));
        report.setInProgressCount(workItemService.countByStatus(user, WorkItemStatus.IN_PROGRESS));
        report.setCompletedCount(workItemService.countByStatus(user, WorkItemStatus.COMPLETE));

        // Get overdue items
        List<WorkItemDTO> overdueItems = workItemService.getOverdueWorkItems(user);
        report.setOverdueCount(overdueItems.size());
        report.setOverdueItems(overdueItems);

        return report;
    }

    /**
     * Generates a text-formatted report for email.
     *
     * @param user the user
     * @return formatted text report
     */
    @Transactional(readOnly = true)
    public String generateTextReport(User user) {
        ReportDTO report = generateReport(user);
        return report.toTextReport();
    }
}