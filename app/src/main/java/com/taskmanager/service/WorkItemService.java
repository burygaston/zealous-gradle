package com.taskmanager.service;

import com.taskmanager.dto.WorkItemDTO;
import com.taskmanager.mapper.WorkItemMapper;
import com.taskmanager.model.Label;
import com.taskmanager.model.User;
import com.taskmanager.model.WorkItem;
import com.taskmanager.model.WorkItemStatus;
import com.taskmanager.repository.LabelRepository;
import com.taskmanager.repository.WorkItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Service for managing work items.
 */
@Service
@RequiredArgsConstructor
public class WorkItemService {

    private final WorkItemRepository workItemRepository;
    private final LabelRepository labelRepository;
    private final WorkItemMapper workItemMapper;

    /**
     * Retrieves all work items for a user, sorted by deadline.
     *
     * @param user the user
     * @return list of work item DTOs
     */
    @Transactional(readOnly = true)
    public List<WorkItemDTO> getAllWorkItems(User user) {
        List<WorkItem> workItems = workItemRepository.findByUserOrderByDeadlineAsc(user);
        return workItemMapper.toDTOList(workItems);
    }

    /**
     * Retrieves work items by status.
     *
     * @param user   the user
     * @param status the status
     * @return list of work item DTOs
     */
    @Transactional(readOnly = true)
    public List<WorkItemDTO> getWorkItemsByStatus(User user, WorkItemStatus status) {
        List<WorkItem> workItems = workItemRepository.findByUserAndStatus(user, status);
        return workItemMapper.toDTOList(workItems);
    }

    /**
     * Retrieves overdue work items for a user.
     *
     * @param user the user
     * @return list of overdue work item DTOs
     */
    @Transactional(readOnly = true)
    public List<WorkItemDTO> getOverdueWorkItems(User user) {
        List<WorkItem> overdueItems = workItemRepository.findOverdueWorkItems(
                user,
                LocalDateTime.now(),
                List.of(WorkItemStatus.COMPLETE)
        );
        return workItemMapper.toDTOList(overdueItems);
    }

    /**
     * Finds a work item by ID.
     *
     * @param id the work item ID
     * @return the work item DTO
     * @throws IllegalArgumentException if not found
     */
    @Transactional(readOnly = true)
    public WorkItemDTO getWorkItemById(Long id) {
        WorkItem workItem = workItemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Work item not found: " + id));
        return workItemMapper.toDTO(workItem);
    }

    /**
     * Creates a new work item.
     *
     * @param workItemDTO the work item DTO
     * @param user        the user
     * @return the created work item DTO
     */
    @Transactional
    public WorkItemDTO createWorkItem(WorkItemDTO workItemDTO, User user) {
        WorkItem workItem = workItemMapper.toEntity(workItemDTO);
        workItem.setUser(user);

        // Handle labels
        if (workItemDTO.getLabels() != null && !workItemDTO.getLabels().isEmpty()) {
            Set<Label> labels = workItemDTO.getLabels().stream()
                    .filter(labelDTO -> labelDTO.getId() != null)
                    .map(labelDTO -> labelRepository.findById(labelDTO.getId())
                            .orElseThrow(() -> new IllegalArgumentException("Label not found: " + labelDTO.getId())))
                    .collect(Collectors.toSet());
            workItem.setLabels(labels);
        }

        WorkItem saved = workItemRepository.save(workItem);
        return workItemMapper.toDTO(saved);
    }

    /**
     * Updates an existing work item.
     *
     * @param id          the work item ID
     * @param workItemDTO the updated work item DTO
     * @param user        the user
     * @return the updated work item DTO
     * @throws IllegalArgumentException if not found or user mismatch
     */
    @Transactional
    public WorkItemDTO updateWorkItem(Long id, WorkItemDTO workItemDTO, User user) {
        WorkItem existing = workItemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Work item not found: " + id));

        if (!existing.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Work item does not belong to user");
        }

        existing.setTitle(workItemDTO.getTitle());
        existing.setDescription(workItemDTO.getDescription());
        existing.updateStatus(workItemDTO.getStatus());
        existing.setDeadline(workItemDTO.getDeadline());
        existing.setPriority(workItemDTO.getPriority());

        // Handle labels
        if (workItemDTO.getLabels() != null) {
            Set<Label> labels = workItemDTO.getLabels().stream()
                    .filter(labelDTO -> labelDTO.getId() != null)
                    .map(labelDTO -> labelRepository.findById(labelDTO.getId())
                            .orElseThrow(() -> new IllegalArgumentException("Label not found: " + labelDTO.getId())))
                    .collect(Collectors.toSet());
            existing.setLabels(labels);
        } else {
            existing.setLabels(new HashSet<>());
        }

        WorkItem updated = workItemRepository.save(existing);
        return workItemMapper.toDTO(updated);
    }

    /**
     * Deletes a work item.
     *
     * @param id   the work item ID
     * @param user the user
     * @throws IllegalArgumentException if not found or user mismatch
     */
    @Transactional
    public void deleteWorkItem(Long id, User user) {
        WorkItem workItem = workItemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Work item not found: " + id));

        if (!workItem.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Work item does not belong to user");
        }

        workItemRepository.delete(workItem);
    }

    /**
     * Counts work items by status.
     *
     * @param user   the user
     * @param status the status
     * @return the count
     */
    @Transactional(readOnly = true)
    public long countByStatus(User user, WorkItemStatus status) {
        return workItemRepository.countByUserAndStatus(user, status);
    }

    /**
     * Counts total work items for a user.
     *
     * @param user the user
     * @return the total count
     */
    @Transactional(readOnly = true)
    public long countTotal(User user) {
        return workItemRepository.countByUser(user);
    }
}