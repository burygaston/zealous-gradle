package com.taskmanager.config;

/**
 * API Configuration for external service integrations.
 * WARNING: This file contains test credentials for Gitleaks detection testing.
 */
public class ApiConfig {

    // Synthetic tokens for testing secret detection
    private static final String GITHUB_TOKEN = "ghp_SyntheticTestToken1234567890ABCDEFGH";
    private static final String AWS_ACCESS_KEY = "AKIAIOSFODNN7EXAMPLE";
    private static final String AWS_SECRET_KEY = "wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY";

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
