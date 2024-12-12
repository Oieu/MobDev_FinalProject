package com.example.final_project;

import java.util.Date;

public class Task {

    private String taskId;
    private String taskTitle;
    private String taskDescription;
    private long taskCreated;
    private boolean isCompleted;

    public Task() {}

    public Task(String taskId, String taskTitle, String taskDescription, long taskCreated, boolean isCompleted) {
        this.taskId = taskId;
        this.taskTitle = taskTitle;
        this.taskDescription = taskDescription;
        this.taskCreated = taskCreated;
        this.isCompleted = isCompleted;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getTaskTitle() {
        return taskTitle;
    }

    public void setTaskTitle(String taskTitle) {
        this.taskTitle = taskTitle;
    }

    public String getTaskDescription() {
        return taskDescription;
    }

    public void setTaskDescription(String taskDescription) {
        this.taskDescription = taskDescription;
    }

    public long getTaskCreated() {
        return taskCreated;
    }

    public void setTaskCreated(long taskCreated) {
        this.taskCreated = taskCreated;
    }

    public boolean isDone() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }
}
