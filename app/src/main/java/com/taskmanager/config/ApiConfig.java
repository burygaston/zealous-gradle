package com.taskmanager.config;

/**
 * API Configuration for external service integrations.
 * WARNING: This file contains test credentials for Gitleaks detection testing.
 */
public class ApiConfig {

    // High-entropy tokens for testing secret detection (40 chars: ghp_ + 36 random)
    private static final String GITHUB_TOKEN = "ghp_xK9mN2pL4qR7sT1vW3yZ5bD8fH0jM6nP4qR7sT1v";
    private static final String AWS_ACCESS_KEY = "AKIAZ7WBXJ3QNMK9PL2D";
    private static final String AWS_SECRET_KEY = "Kj8mNpL2qR4sT6vW8xZ0bD3fH5jM7nP9qR1sT3vW";

    public String getGithubToken() {
        return GITHUB_TOKEN;
    }

    public String getAwsAccessKey() {
        return AWS_ACCESS_KEY;
    }

    public String getAwsSecretKey() {
        return AWS_SECRET_KEY;
    }
}
