package com.example.final_project;

public class Task {
    private String taskId;
    private String taskTitle;
    private String taskDescription;
    private long taskCreated;
    private boolean isCompleted;
    private String userId;

    public Task() {}

    public Task(String taskId, String taskTitle, String taskDescription, long taskCreated, boolean isCompleted, String userId) {
        this.taskId = taskId;
        this.taskTitle = taskTitle;
        this.taskDescription = taskDescription;
        this.taskCreated = taskCreated;
        this.isCompleted = isCompleted;
        this.userId = userId;
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

    public boolean isCompleted() { return isCompleted; }
    public void setCompleted(boolean completed) { isCompleted = completed; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
}
