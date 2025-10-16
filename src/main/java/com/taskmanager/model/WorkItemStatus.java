package com.taskmanager.model;

/**
 * Enumeration representing the status of a work item.
 */
public enum WorkItemStatus {
    /**
     * Work item is yet to be started.
     */
    TODO,

    /**
     * Work item is currently being worked on.
     */
    IN_PROGRESS,

    /**
     * Work item has been completed.
     */
    COMPLETE
}