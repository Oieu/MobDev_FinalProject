package com.example.final_project;

public class Task {
    private String taskId;
    private String taskTitle;
    private String taskDescription;
    private long taskCreated;
    private boolean isCompleted;
    private String userId;
    private String taskStatus;

    public Task() {}

    public Task(String taskId, String taskTitle, String taskDescription, long taskCreated, boolean isCompleted, String userId, String taskStatus) {
        this.taskId = taskId;
        this.taskTitle = taskTitle;
        this.taskDescription = taskDescription;
        this.taskCreated = taskCreated;
        this.isCompleted = isCompleted;
        this.userId = userId;
        this.taskStatus = taskStatus;
    }

    // Getters and setters
    public String getTaskId() { return taskId; }
    public void setTaskId(String taskId) { this.taskId = taskId; }

    public String getTaskTitle() { return taskTitle; }
    public void setTaskTitle(String taskTitle) { this.taskTitle = taskTitle; }

    public String getTaskDescription() { return taskDescription; }
    public void setTaskDescription(String taskDescription) { this.taskDescription = taskDescription; }

    public long getTaskCreated() { return taskCreated; }
    public void setTaskCreated(long taskCreated) { this.taskCreated = taskCreated; }


    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(String taskStatus) {
        this.taskStatus = taskStatus;
    }
}