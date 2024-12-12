package com.example.final_project;

import android.content.Intent;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private ArrayList<Task> tasks;
    private OnTaskStatusChangedListener listener;

    public interface OnTaskStatusChangedListener {
        void onTaskStatusChanged(Task task);
    }

    public TaskAdapter(ArrayList<Task> tasks, OnTaskStatusChangedListener listener) {
        this.tasks = tasks;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = tasks.get(position);
        holder.taskTitle.setText(task.getTaskTitle());
        holder.taskDescription.setText(task.getTaskDescription());

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String dateCreated = simpleDateFormat.format(new Date(task.getTaskCreated()));
        holder.taskDateCreated.setText(dateCreated);

        // Set the checkbox state based on task status
        holder.checkBox.setChecked(task.getTaskStatus().equals("completed"));

        // Apply strikethrough if the task is completed
        if (task.getTaskStatus().equals("completed")) {
            holder.taskTitle.setPaintFlags(holder.taskTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.taskDescription.setPaintFlags(holder.taskDescription.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            holder.taskTitle.setPaintFlags(holder.taskTitle.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            holder.taskDescription.setPaintFlags(holder.taskDescription.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
        }

        // Set the edit button click listener
        holder.btnEditTask.setOnClickListener(v -> {
            Intent intent = new Intent(holder.itemView.getContext(), EditTask.class);
            intent.putExtra("TASK_ID", task.getTaskId());
            intent.putExtra("TASK_TITLE", task.getTaskTitle());
            intent.putExtra("TASK_DESCRIPTION", task.getTaskDescription());
            holder.itemView.getContext().startActivity(intent);
        });

        holder.checkBox.setOnClickListener(v -> {
            if (holder.checkBox.isChecked()) {
                task.setTaskStatus("completed");
            } else {
                task.setTaskStatus("in progress");
            }
            listener.onTaskStatusChanged(task);
        });
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    public class TaskViewHolder extends RecyclerView.ViewHolder {
        public TextView taskTitle, taskDescription, taskDateCreated;
        public CheckBox checkBox;
        public Button btnEditTask; // Add the edit button

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            taskTitle = itemView.findViewById(R.id.tasktitle);
            taskDescription = itemView.findViewById(R.id.taskdesc);
            taskDateCreated = itemView.findViewById(R.id.taskdatecreate);
            checkBox = itemView.findViewById(R.id.cbtaskcompleted);
            btnEditTask = itemView.findViewById(R.id.btnedittask); // Initialize the edit button
        }
    }
}