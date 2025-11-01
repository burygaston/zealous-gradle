package com.taskmanager.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Entity representing a work item (task) in the system.
 */
@Entity
@Table(name = "work_items", indexes = {
    @Index(name = "idx_user_status", columnList = "user_id, status"),
    @Index(name = "idx_deadline", columnList = "deadline"),
    @Index(name = "idx_status", columnList = "status")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WorkItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private WorkItemStatus status = WorkItemStatus.TODO;

    @Column(nullable = false)
    private LocalDateTime deadline;

    @Column(nullable = false)
    private Integer priority = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
        name = "work_item_labels",
        joinColumns = @JoinColumn(name = "work_item_id"),
        inverseJoinColumns = @JoinColumn(name = "label_id")
    )
    private Set<Label> labels = new HashSet<>();

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    /**
     * Checks if the work item is overdue.
     *
     * @return true if the deadline has passed and status is not COMPLETE
     */
    public boolean isOverdue() {
        return status != WorkItemStatus.COMPLETE && deadline.isBefore(LocalDateTime.now());
    }

    /**
     * Marks the work item as completed.
     */
    public void markAsComplete() {
        this.status = WorkItemStatus.COMPLETE;
        this.completedAt = LocalDateTime.now();
    }

    /**
     * Updates the status and sets completedAt timestamp if changing to COMPLETE.
     *
     * @param newStatus the new status
     */
    public void updateStatus(WorkItemStatus newStatus) {
        if (newStatus == WorkItemStatus.COMPLETE && this.status != WorkItemStatus.COMPLETE) {
            this.completedAt = LocalDateTime.now();
        } else if (newStatus != WorkItemStatus.COMPLETE) {
            this.completedAt = null;
        }
        this.status = newStatus;
    }
}