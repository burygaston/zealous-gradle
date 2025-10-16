package com.taskmanager.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Configuration for enabling scheduled tasks.
 */
@Configuration
@EnableScheduling
public class SchedulerConfig {
    // Scheduling is enabled via @EnableScheduling annotation
    // Individual scheduled methods are defined in services with @Scheduled annotation
}