package com.example.final_project;

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

    public TaskAdapter(List<Task> taskList) {
        this.taskList = taskList;
        this.dbRef = FirebaseDatabase.getInstance(FirebaseConfig.dbURL)
                .getReference("tasks");
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

        holder.cbTaskCompleted.setChecked(task.isDone());

        holder.cbTaskCompleted.setOnCheckedChangeListener((buttonView, isChecked) -> {
            task.setCompleted(isChecked);
            if (task.getTaskId() != null) {
                dbRef.child(task.getTaskId()).child("isCompleted").setValue(isChecked);
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
