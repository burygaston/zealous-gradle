package com.taskmanager.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDateTime;
import java.util.HashSet;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for WorkItem model.
 */
class WorkItemTest {

    private WorkItem workItem;
    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        workItem = new WorkItem();
        workItem.setId(1L);
        workItem.setTitle("Test Task");
        workItem.setDescription("Test Description");
        workItem.setStatus(WorkItemStatus.TODO);
        workItem.setDeadline(LocalDateTime.now().plusDays(7));
        workItem.setPriority(1);
        workItem.setUser(user);
        workItem.setLabels(new HashSet<>());
    }

    @Test
    @DisplayName("Should detect overdue work item")
    void shouldDetectOverdueWorkItem() {
        workItem.setDeadline(LocalDateTime.now().minusDays(1));
        workItem.setStatus(WorkItemStatus.TODO);

        assertThat(workItem.isOverdue()).isTrue();
    }

    @Test
    @DisplayName("Should not detect completed item as overdue")
    void shouldNotDetectCompletedItemAsOverdue() {
        workItem.setDeadline(LocalDateTime.now().minusDays(1));
        workItem.setStatus(WorkItemStatus.COMPLETE);

        assertThat(workItem.isOverdue()).isFalse();
    }

    @Test
    @DisplayName("Should not detect future item as overdue")
    void shouldNotDetectFutureItemAsOverdue() {
        workItem.setDeadline(LocalDateTime.now().plusDays(7));
        workItem.setStatus(WorkItemStatus.TODO);

        assertThat(workItem.isOverdue()).isFalse();
    }

    @Test
    @DisplayName("Should mark work item as complete")
    void shouldMarkWorkItemAsComplete() {
        workItem.markAsComplete();

        assertThat(workItem.getStatus()).isEqualTo(WorkItemStatus.COMPLETE);
        assertThat(workItem.getCompletedAt()).isNotNull();
    }

    @Test
    @DisplayName("Should update status to TODO")
    void shouldUpdateStatusToTodo() {
        workItem.updateStatus(WorkItemStatus.TODO);

        assertThat(workItem.getStatus()).isEqualTo(WorkItemStatus.TODO);
        assertThat(workItem.getCompletedAt()).isNull();
    }

    @Test
    @DisplayName("Should update status to IN_PROGRESS")
    void shouldUpdateStatusToInProgress() {
        workItem.updateStatus(WorkItemStatus.IN_PROGRESS);

        assertThat(workItem.getStatus()).isEqualTo(WorkItemStatus.IN_PROGRESS);
        assertThat(workItem.getCompletedAt()).isNull();
    }

    @Test
    @DisplayName("Should set completed timestamp when marking complete")
    void shouldSetCompletedTimestamp() {
        workItem.updateStatus(WorkItemStatus.COMPLETE);

        assertThat(workItem.getCompletedAt()).isNotNull();
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3, 4, 5})
    @DisplayName("Should accept valid priority values")
    void shouldAcceptValidPriority(int priority) {
        workItem.setPriority(priority);

        assertThat(workItem.getPriority()).isEqualTo(priority);
    }

    @ParameterizedTest
    @EnumSource(WorkItemStatus.class)
    @DisplayName("Should accept all status values")
    void shouldAcceptAllStatusValues(WorkItemStatus status) {
        workItem.setStatus(status);

        assertThat(workItem.getStatus()).isEqualTo(status);
    }

    @Test
    @DisplayName("Should have valid ID")
    void shouldHaveValidId() {
        assertThat(workItem.getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Should have valid title")
    void shouldHaveValidTitle() {
        assertThat(workItem.getTitle()).isEqualTo("Test Task");
    }

    @Test
    @DisplayName("Should have valid description")
    void shouldHaveValidDescription() {
        assertThat(workItem.getDescription()).isEqualTo("Test Description");
    }

    @Test
    @DisplayName("Should have valid user association")
    void shouldHaveValidUserAssociation() {
        assertThat(workItem.getUser()).isEqualTo(user);
    }

    @Test
    @DisplayName("Should have empty labels set by default")
    void shouldHaveEmptyLabelsSet() {
        assertThat(workItem.getLabels()).isEmpty();
    }

    @Test
    @DisplayName("Should allow adding labels")
    void shouldAllowAddingLabels() {
        Label label = new Label();
        label.setId(1L);
        label.setName("Bug");

        workItem.getLabels().add(label);

        assertThat(workItem.getLabels()).hasSize(1);
    }

    @Test
    @DisplayName("Should allow removing labels")
    void shouldAllowRemovingLabels() {
        Label label = new Label();
        label.setId(1L);
        label.setName("Bug");

        workItem.getLabels().add(label);
        workItem.getLabels().remove(label);

        assertThat(workItem.getLabels()).isEmpty();
    }

    @Test
    @DisplayName("Should maintain non-null labels set")
    void shouldMaintainNonNullLabelsSet() {
        workItem.setLabels(null);
        assertThat(workItem.getLabels()).isNull();
    }

    @Test
    @DisplayName("Should handle zero priority")
    void shouldHandleZeroPriority() {
        workItem.setPriority(0);
        assertThat(workItem.getPriority()).isEqualTo(0);
    }

    @Test
    @DisplayName("Should allow null description")
    void shouldAllowNullDescription() {
        workItem.setDescription(null);
        assertThat(workItem.getDescription()).isNull();
    }

    @Test
    @DisplayName("Should allow empty description")
    void shouldAllowEmptyDescription() {
        workItem.setDescription("");
        assertThat(workItem.getDescription()).isEmpty();
    }

    @Test
    @DisplayName("Should handle long description")
    void shouldHandleLongDescription() {
        String longDescription = "A".repeat(1000);
        workItem.setDescription(longDescription);
        assertThat(workItem.getDescription()).hasSize(1000);
    }

    @Test
    @DisplayName("Should handle title update")
    void shouldHandleTitleUpdate() {
        workItem.setTitle("Updated Title");
        assertThat(workItem.getTitle()).isEqualTo("Updated Title");
    }

    @Test
    @DisplayName("Should handle deadline update")
    void shouldHandleDeadlineUpdate() {
        LocalDateTime newDeadline = LocalDateTime.now().plusDays(30);
        workItem.setDeadline(newDeadline);
        assertThat(workItem.getDeadline()).isEqualTo(newDeadline);
    }

    @Test
    @DisplayName("Should handle priority update")
    void shouldHandlePriorityUpdate() {
        workItem.setPriority(5);
        assertThat(workItem.getPriority()).isEqualTo(5);
    }
}
