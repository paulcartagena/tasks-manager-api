package com.tasksmanager.api.exception;

public class ProjectHasTasksException extends RuntimeException {
    public ProjectHasTasksException(String message) {
        super(message);
    }
}
