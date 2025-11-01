package com.taskmanager;

import com.taskmanager.common.CommonUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main application class for Task Manager.
 */
@SpringBootApplication
public class TaskManagerApplication {

    /**
     * Main entry point for the application.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        CommonUtil.newMethod();
        SpringApplication.run(TaskManagerApplication.class, args);
    }
}