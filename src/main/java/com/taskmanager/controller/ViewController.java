package com.taskmanager.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controller for view pages.
 */
@Controller
public class ViewController {

    /**
     * Shows the login page.
     *
     * @return the login view
     */
    @GetMapping("/login")
    public String login() {
        return "login";
    }

    /**
     * Shows the dashboard page.
     *
     * @return the dashboard view
     */
    @GetMapping({"/", "/dashboard"})
    public String dashboard() {
        return "dashboard";
    }
}