package com.taskmanager.service;

import java.io.*;
import java.util.List;

public class ZealousService {

    // 1. Logic Bug: Potential NPE
    // 2. Convention Break: Too many params without Javadoc
    public void processData(String input, List<String> items, boolean flag, int priority) {
        if (input.equals("ADMIN")) { // Potentially throws NPE if input is null
            System.out.println("Processing admin data..."); // Convention break: Use logger
        }
        
        for (String item : items) {
            System.out.println("Item: " + item);
        }
    }

    // 3. Resource Leak: Opening a file but never closing it
    public void readFile(String path) throws FileNotFoundException {
        FileInputStream fis = new FileInputStream(path);
        // fis is never closed; CodeRabbit should catch the missing try-with-resources
    }
}