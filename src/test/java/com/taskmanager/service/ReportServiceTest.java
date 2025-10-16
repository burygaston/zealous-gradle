package com.taskmanager.service;

import com.taskmanager.dto.ReportDTO;
import com.taskmanager.dto.WorkItemDTO;
import com.taskmanager.model.User;
import com.taskmanager.model.WorkItemStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * Unit tests for ReportService.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Report Service Tests")
class ReportServiceTest {

    @Mock
    private WorkItemService workItemService;

    @InjectMocks
    private ReportService reportService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
    }

    @Test
    @DisplayName("Should generate comprehensive report")
    void shouldGenerateReport() {
        WorkItemDTO overdueItem = new WorkItemDTO();
        overdueItem.setTitle("Overdue Task");
        overdueItem.setDeadline(LocalDateTime.now().minusDays(1));
        overdueItem.setOverdue(true);

        List<WorkItemDTO> overdueItems = Arrays.asList(overdueItem);

        when(workItemService.countTotal(testUser)).thenReturn(10L);
        when(workItemService.countByStatus(testUser, WorkItemStatus.TODO)).thenReturn(3L);
        when(workItemService.countByStatus(testUser, WorkItemStatus.IN_PROGRESS)).thenReturn(2L);
        when(workItemService.countByStatus(testUser, WorkItemStatus.COMPLETE)).thenReturn(5L);
        when(workItemService.getOverdueWorkItems(testUser)).thenReturn(overdueItems);

        ReportDTO report = reportService.generateReport(testUser);

        assertThat(report).isNotNull();
        assertThat(report.getUserId()).isEqualTo(1L);
        assertThat(report.getUsername()).isEqualTo("testuser");
        assertThat(report.getTotalItems()).isEqualTo(10L);
        assertThat(report.getTodoCount()).isEqualTo(3L);
        assertThat(report.getInProgressCount()).isEqualTo(2L);
        assertThat(report.getCompletedCount()).isEqualTo(5L);
        assertThat(report.getOverdueCount()).isEqualTo(1L);
        assertThat(report.getOverdueItems()).hasSize(1);

        verify(workItemService).countTotal(testUser);
        verify(workItemService).countByStatus(testUser, WorkItemStatus.TODO);
        verify(workItemService).countByStatus(testUser, WorkItemStatus.IN_PROGRESS);
        verify(workItemService).countByStatus(testUser, WorkItemStatus.COMPLETE);
        verify(workItemService).getOverdueWorkItems(testUser);
    }

    @Test
    @DisplayName("Should generate report with no items")
    void shouldGenerateReportWithNoItems() {
        when(workItemService.countTotal(testUser)).thenReturn(0L);
        when(workItemService.countByStatus(testUser, WorkItemStatus.TODO)).thenReturn(0L);
        when(workItemService.countByStatus(testUser, WorkItemStatus.IN_PROGRESS)).thenReturn(0L);
        when(workItemService.countByStatus(testUser, WorkItemStatus.COMPLETE)).thenReturn(0L);
        when(workItemService.getOverdueWorkItems(testUser)).thenReturn(Collections.emptyList());

        ReportDTO report = reportService.generateReport(testUser);

        assertThat(report.getTotalItems()).isEqualTo(0L);
        assertThat(report.getOverdueCount()).isEqualTo(0L);
    }

    @Test
    @DisplayName("Should generate text report")
    void shouldGenerateTextReport() {
        when(workItemService.countTotal(testUser)).thenReturn(5L);
        when(workItemService.countByStatus(testUser, WorkItemStatus.TODO)).thenReturn(2L);
        when(workItemService.countByStatus(testUser, WorkItemStatus.IN_PROGRESS)).thenReturn(1L);
        when(workItemService.countByStatus(testUser, WorkItemStatus.COMPLETE)).thenReturn(2L);
        when(workItemService.getOverdueWorkItems(testUser)).thenReturn(Collections.emptyList());

        String textReport = reportService.generateTextReport(testUser);

        assertThat(textReport).isNotNull();
        assertThat(textReport).contains("Task Management Report");
        assertThat(textReport).contains("testuser");
        assertThat(textReport).contains("Total Items: 5");
        assertThat(textReport).contains("To Do: 2");
        assertThat(textReport).contains("In Progress: 1");
        assertThat(textReport).contains("Completed: 2");
    }

    @Test
    @DisplayName("Should include overdue items in text report")
    void shouldIncludeOverdueItemsInTextReport() {
        WorkItemDTO overdueItem = new WorkItemDTO();
        overdueItem.setTitle("Overdue Task");
        overdueItem.setDeadline(LocalDateTime.now().minusDays(1));

        when(workItemService.countTotal(testUser)).thenReturn(1L);
        when(workItemService.countByStatus(testUser, WorkItemStatus.TODO)).thenReturn(1L);
        when(workItemService.countByStatus(testUser, WorkItemStatus.IN_PROGRESS)).thenReturn(0L);
        when(workItemService.countByStatus(testUser, WorkItemStatus.COMPLETE)).thenReturn(0L);
        when(workItemService.getOverdueWorkItems(testUser)).thenReturn(Arrays.asList(overdueItem));

        String textReport = reportService.generateTextReport(testUser);

        assertThat(textReport).contains("Overdue Items:");
        assertThat(textReport).contains("Overdue Task");
    }

    @Test
    @DisplayName("Should verify report generation timestamp")
    void shouldVerifyReportTimestamp() {
        when(workItemService.countTotal(testUser)).thenReturn(0L);
        when(workItemService.countByStatus(any(), any())).thenReturn(0L);
        when(workItemService.getOverdueWorkItems(testUser)).thenReturn(Collections.emptyList());

        LocalDateTime before = LocalDateTime.now();
        ReportDTO report = reportService.generateReport(testUser);
        LocalDateTime after = LocalDateTime.now();

        assertThat(report.getGeneratedAt()).isNotNull();
        assertThat(report.getGeneratedAt()).isAfterOrEqualTo(before);
        assertThat(report.getGeneratedAt()).isBeforeOrEqualTo(after);
    }
}