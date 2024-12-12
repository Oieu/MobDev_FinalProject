package com.example.final_project;

import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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
        public Button btnDeleteTask;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            taskTitle = itemView.findViewById(R.id.tasktitle);
            taskDescription = itemView.findViewById(R.id.taskdesc);
            taskDateCreated = itemView.findViewById(R.id.taskdatecreate);
            checkBox = itemView.findViewById(R.id.cbtaskcompleted);
            btnDeleteTask = itemView.findViewById(R.id.btndeletetask);

            btnDeleteTask.setOnClickListener(v -> {
                Task task = tasks.get(getAdapterPosition());
                task.setDeleted(true);
                listener.onTaskStatusChanged(task);
            });
        }
    }
}