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
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private final List<Task> taskList;
    private final DatabaseReference dbRef;
    private boolean isCompleted;

    private OnTaskStatusChangedListener onTaskStatusChangedListener;

    public interface OnTaskStatusChangedListener {
        void onTaskStatusChanged(Task task);
    }

    public void setOnTaskStatusChangedListener(OnTaskStatusChangedListener onTaskStatusChangedListener) {
        this.onTaskStatusChangedListener = onTaskStatusChangedListener;
    }

    public TaskAdapter(List<Task> taskList, boolean isCompleted) {
        this.taskList = taskList;
        this.dbRef = FirebaseDatabase.getInstance(FirebaseConfig.dbURL)
                .getReference("tasks");
        this.isCompleted = isCompleted;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = taskList.get(position);
        holder.taskTitle.setText(task.getTaskTitle());
        holder.taskDescription.setText(task.getTaskDescription());

        if (task.getTaskCreated() != 0) {
            Date date = new Date(task.getTaskCreated());
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            String dateString = dateFormat.format(date);
            holder.taskDate.setText(dateString);
        } else {
            holder.taskDate.setText("No Date");
        }

        String taskStatus = task.getTaskStatus() != null ? task.getTaskStatus() : "in progress";

        if (taskStatus.equals("completed")) {
            holder.taskTitle.setPaintFlags(holder.taskTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.cbTaskCompleted.setChecked(true);
        } else {
            holder.taskTitle.setPaintFlags(holder.taskTitle.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
            holder.cbTaskCompleted.setChecked(false);
        }


        holder.cbTaskCompleted.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (task.getTaskId() != null) {
                // Update task status based on checkbox state
                String newStatus = isChecked ? "completed" : "in progress";
                dbRef.child(task.getTaskId()).child("taskStatus").setValue(newStatus)
                        .addOnSuccessListener(aVoid -> {
                            // Optionally notify the listener if set
                            if (onTaskStatusChangedListener != null) {
                                task.setTaskStatus(newStatus);
                                onTaskStatusChangedListener.onTaskStatusChanged(task);
                            }
                        });
            }
        });

        holder.btnDeleteTask.setOnClickListener(v -> {
            if (task.getTaskId() != null) {
                dbRef.child(task.getTaskId()).removeValue().addOnSuccessListener(aVoid -> {
                    taskList.remove(position);
                    notifyItemRemoved(position);
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    static class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView taskTitle, taskDescription, taskDate;
        CheckBox cbTaskCompleted;
        Button btnDeleteTask;

        TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            taskTitle = itemView.findViewById(R.id.tasktitle);
            cbTaskCompleted = itemView.findViewById(R.id.cbtaskcompleted);
            btnDeleteTask = itemView.findViewById(R.id.btndeletetask);
            taskDescription = itemView.findViewById(R.id.taskdesc);
            taskDate = itemView.findViewById(R.id.taskdatecreate);
        }
    }
}