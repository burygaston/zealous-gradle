package com.taskmanager.common;

/**
 * Utility class for common functionality shared across modules.
 */
public class CommonUtil {

    private CommonUtil() {
        // Private constructor to prevent instantiation
    }

    /**
     * Example utility method.
     */
    public static String getAppName() {
        return "Hello from Task Manager";
    }

    public static void newMethod() {
        System.out.println("This is a new V3 method!");
    }

    public static void newMethodV5() {
        System.out.println("This is a new V5 method!");
    }
}