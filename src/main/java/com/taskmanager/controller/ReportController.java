package com.taskmanager.controller;

import com.taskmanager.dto.ReportDTO;
import com.taskmanager.model.User;
import com.taskmanager.service.ReportService;
import com.taskmanager.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for report operations.
 */
@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;
    private final UserService userService;

    /**
     * Generates a comprehensive report for the authenticated user.
     *
     * @param authentication the authentication object
     * @return the report DTO
     */
    @GetMapping
    public ResponseEntity<ReportDTO> generateReport(Authentication authentication) {
        User user = getAuthenticatedUser(authentication);
        ReportDTO report = reportService.generateReport(user);
        return ResponseEntity.ok(report);
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