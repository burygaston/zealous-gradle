package com.taskmanager.repository;

import com.taskmanager.model.User;
import com.taskmanager.model.WorkItem;
import com.taskmanager.model.WorkItemStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository interface for WorkItem entity operations.
 */
@Repository
public interface WorkItemRepository extends JpaRepository<WorkItem, Long> {

    /**
     * Finds all work items for a specific user, ordered by deadline.
     *
     * @param user the user whose work items to retrieve
     * @return list of work items ordered by deadline ascending
     */
    List<WorkItem> findByUserOrderByDeadlineAsc(User user);

    /**
     * Finds work items by user and status.
     *
     * @param user   the user
     * @param status the status to filter by
     * @return list of matching work items
     */
    List<WorkItem> findByUserAndStatus(User user, WorkItemStatus status);

    /**
     * Finds overdue work items for a user.
     *
     * @param user     the user
     * @param now      the current time
     * @param statuses the statuses to exclude (typically COMPLETE)
     * @return list of overdue work items
     */
    @Query("SELECT w FROM WorkItem w WHERE w.user = :user AND w.deadline < :now AND w.status NOT IN :statuses")
    List<WorkItem> findOverdueWorkItems(User user, LocalDateTime now, List<WorkItemStatus> statuses);

    /**
     * Counts work items by user and status.
     *
     * @param user   the user
     * @param status the status
     * @return count of matching work items
     */
    long countByUserAndStatus(User user, WorkItemStatus status);

    /**
     * Counts total work items for a user.
     *
     * @param user the user
     * @return total count
     */
    long countByUser(User user);

    /**
     * Finds work items by user with deadline between dates.
     *
     * @param user  the user
     * @param start start date
     * @param end   end date
     * @return list of work items within the date range
     */
    List<WorkItem> findByUserAndDeadlineBetween(User user, LocalDateTime start, LocalDateTime end);
}