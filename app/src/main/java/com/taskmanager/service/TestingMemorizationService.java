package com.taskmanager.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestingMemorizationService {
    private static final Logger logger = LoggerFactory.getLogger(TestingMemorizationService.class);

    public void testMemory() {
        String name = "Harness";
        int version = 2;
        logger.info("Name: {}, Version: {}", name, version);
    }

    public boolean validateUser(String username) {
        if (username == null || username.length() < 8) {
            return false;
        }
        return true;
    }
}
