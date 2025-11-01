package com.taskmanager.controller;

import com.taskmanager.dto.WorkItemDTO;
import com.taskmanager.model.User;
import com.taskmanager.model.WorkItemStatus;
import com.taskmanager.service.UserService;
import com.taskmanager.service.WorkItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for work item operations.
 */
@RestController
@RequestMapping("/api/workitems")
@RequiredArgsConstructor
public class WorkItemController {

    private final WorkItemService workItemService;
    private final UserService userService;

    /**
     * Gets all work items for the authenticated user.
     *
     * @param authentication the authentication object
     * @return list of work item DTOs
     */
    @GetMapping
    public ResponseEntity<List<WorkItemDTO>> getAllWorkItems(Authentication authentication) {
        User user = getAuthenticatedUser(authentication);
        List<WorkItemDTO> workItems = workItemService.getAllWorkItems(user);
        return ResponseEntity.ok(workItems);
    }

    /**
     * Gets work items by status.
     *
     * @param status         the status filter
     * @param authentication the authentication object
     * @return list of work item DTOs
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<WorkItemDTO>> getWorkItemsByStatus(
            @PathVariable WorkItemStatus status,
            Authentication authentication) {
        User user = getAuthenticatedUser(authentication);
        List<WorkItemDTO> workItems = workItemService.getWorkItemsByStatus(user, status);
        return ResponseEntity.ok(workItems);
    }

    /**
     * Gets overdue work items.
     *
     * @param authentication the authentication object
     * @return list of overdue work item DTOs
     */
    @GetMapping("/overdue")
    public ResponseEntity<List<WorkItemDTO>> getOverdueWorkItems(Authentication authentication) {
        User user = getAuthenticatedUser(authentication);
        List<WorkItemDTO> workItems = workItemService.getOverdueWorkItems(user);
        return ResponseEntity.ok(workItems);
    }

    /**
     * Gets a specific work item by ID.
     *
     * @param id the work item ID
     * @return the work item DTO
     */
    @GetMapping("/{id}")
    public ResponseEntity<WorkItemDTO> getWorkItem(@PathVariable Long id) {
        WorkItemDTO workItem = workItemService.getWorkItemById(id);
        return ResponseEntity.ok(workItem);
    }

    /**
     * Creates a new work item.
     *
     * @param workItemDTO    the work item DTO
     * @param authentication the authentication object
     * @return the created work item DTO
     */
    @PostMapping
    public ResponseEntity<WorkItemDTO> createWorkItem(
            @RequestBody WorkItemDTO workItemDTO,
            Authentication authentication) {
        User user = getAuthenticatedUser(authentication);
        WorkItemDTO created = workItemService.createWorkItem(workItemDTO, user);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * Updates an existing work item.
     *
     * @param id             the work item ID
     * @param workItemDTO    the updated work item DTO
     * @param authentication the authentication object
     * @return the updated work item DTO
     */
    @PutMapping("/{id}")
    public ResponseEntity<WorkItemDTO> updateWorkItem(
            @PathVariable Long id,
            @RequestBody WorkItemDTO workItemDTO,
            Authentication authentication) {
        User user = getAuthenticatedUser(authentication);
        WorkItemDTO updated = workItemService.updateWorkItem(id, workItemDTO, user);
        return ResponseEntity.ok(updated);
    }

    /**
     * Deletes a work item.
     *
     * @param id             the work item ID
     * @param authentication the authentication object
     * @return no content response
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWorkItem(
            @PathVariable Long id,
            Authentication authentication) {
        User user = getAuthenticatedUser(authentication);
        workItemService.deleteWorkItem(id, user);
        return ResponseEntity.noContent().build();
    }

    /**
     * Helper method to get the authenticated user.
     *
     * @param authentication the authentication object
     * @return the user entity
     */
    private User getAuthenticatedUser(Authentication authentication) {
        return userService.findByUsername(authentication.getName())
                .orElseThrow(() -> new IllegalStateException("User not found"));
    }
}