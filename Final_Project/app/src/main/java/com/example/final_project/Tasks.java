package com.example.final_project;

import java.util.Date;

public class Tasks {

    String TaskName;
    String TaskDescription;
    Date TaskCreated;

    public Tasks(String taskName, String taskDescription, Date taskCreated) {
        TaskName = taskName;
        TaskDescription = taskDescription;
        TaskCreated = taskCreated;
    }

    public String getTaskName() {
        return TaskName;
    }

    public void setTaskName(String taskName) {
        TaskName = taskName;
    }

    public String getTaskDescription() {
        return TaskDescription;
    }

    public void setTaskDescription(String taskDescription) {
        TaskDescription = taskDescription;
    }

    public Date getTaskCreated() {
        return TaskCreated;
    }

    public void setTaskCreated(Date taskCreated) {
        TaskCreated = taskCreated;
    }
}
