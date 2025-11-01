package com.taskmanager.controller;

import com.taskmanager.dto.LabelDTO;
import com.taskmanager.model.User;
import com.taskmanager.service.LabelService;
import com.taskmanager.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for label operations.
 */
@RestController
@RequestMapping("/api/labels")
@RequiredArgsConstructor
public class LabelController {

    private final LabelService labelService;
    private final UserService userService;

    /**
     * Gets all labels for the authenticated user.
     *
     * @param authentication the authentication object
     * @return list of label DTOs
     */
    @GetMapping
    public ResponseEntity<List<LabelDTO>> getAllLabels(Authentication authentication) {
        User user = getAuthenticatedUser(authentication);
        List<LabelDTO> labels = labelService.getAllLabels(user);
        return ResponseEntity.ok(labels);
    }

    /**
     * Gets a specific label by ID.
     *
     * @param id the label ID
     * @return the label DTO
     */
    @GetMapping("/{id}")
    public ResponseEntity<LabelDTO> getLabel(@PathVariable Long id) {
        LabelDTO label = labelService.getLabelById(id);
        return ResponseEntity.ok(label);
    }

    /**
     * Creates a new label.
     *
     * @param labelDTO       the label DTO
     * @param authentication the authentication object
     * @return the created label DTO
     */
    @PostMapping
    public ResponseEntity<LabelDTO> createLabel(
            @RequestBody LabelDTO labelDTO,
            Authentication authentication) {
        User user = getAuthenticatedUser(authentication);
        LabelDTO created = labelService.createLabel(labelDTO, user);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * Updates an existing label.
     *
     * @param id             the label ID
     * @param labelDTO       the updated label DTO
     * @param authentication the authentication object
     * @return the updated label DTO
     */
    @PutMapping("/{id}")
    public ResponseEntity<LabelDTO> updateLabel(
            @PathVariable Long id,
            @RequestBody LabelDTO labelDTO,
            Authentication authentication) {
        User user = getAuthenticatedUser(authentication);
        LabelDTO updated = labelService.updateLabel(id, labelDTO, user);
        return ResponseEntity.ok(updated);
    }

    /**
     * Deletes a label.
     *
     * @param id             the label ID
     * @param authentication the authentication object
     * @return no content response
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLabel(
            @PathVariable Long id,
            Authentication authentication) {
        User user = getAuthenticatedUser(authentication);
        labelService.deleteLabel(id, user);
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