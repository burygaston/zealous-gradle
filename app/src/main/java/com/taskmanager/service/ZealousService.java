package com.taskmanager.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

public class ZealousService {

    private static final Logger log = LoggerFactory.getLogger(ZealousService.class);

    public void processData(String input, List<String> items, boolean flag, int priority) {
        if ("ADMIN".equals(input)) {
            log.info("Processing admin data...");
        }

        List<String> safeItems = items != null ? items : Collections.emptyList();
        for (String item : safeItems) {
            log.info("Item: {}", item);
        }
    }

    public void readFile(String path) throws IOException {
        try (InputStream fis = Files.newInputStream(Paths.get(path))) {
            // Process file content here
        }
    }
}
