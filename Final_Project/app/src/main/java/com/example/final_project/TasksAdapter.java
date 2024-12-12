package com.example.final_project;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class TasksAdapter extends RecyclerView.Adapter<TasksViewHolder> {

    Context context;
    //Tasks if from Tasks class
    List<Tasks> tasksList;

    public TasksAdapter(Context context, List<Tasks> tasksList) {
        this.context = context;
        this.tasksList = tasksList;
    }

    @NonNull
    @Override
    public TasksViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        Inflates the recyler_item layout
        return new TasksViewHolder((LayoutInflater.from(context).inflate(R.layout.recycler_item,parent, false)));
    }

    @Override
    public void onBindViewHolder(@NonNull TasksViewHolder holder, int position) {
//        Binds the details of recycler view
        holder.taskName.setText(tasksList.get(position).getTaskName());
        holder.taskDesc.setText(tasksList.get(position).getTaskDescription());
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String dateString = dateFormat.format(tasksList.get(position).getTaskCreated()); // Set the formatted date text
        holder.taskDate.setText(dateString);
    }

    @Override
    public int getItemCount() {
        return tasksList.size();
    }
}
