package com.example.final_project;

import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CalendarRecAdapter extends RecyclerView.Adapter<CalendarRecAdapter.ViewHolder> {

    private List<Task> originalTaskList;
    private List<Task> filteredTaskList;

    public CalendarRecAdapter(List<Task> taskList) {
        this.originalTaskList = taskList;
        filterTaskList();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_today, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Task task = filteredTaskList.get(position);


        String formattedDate = formatDate(task.getTaskCreated());


        holder.dateTextView.setText(formattedDate);
        holder.titleTextView.setText(task.getTaskTitle());
        holder.descriptionTextView.setText(task.getTaskDescription());


        if ("completed".equalsIgnoreCase(task.getTaskStatus())) {
            holder.titleTextView.setPaintFlags(holder.titleTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.descriptionTextView.setPaintFlags(holder.descriptionTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {

            holder.titleTextView.setPaintFlags(holder.titleTextView.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            holder.descriptionTextView.setPaintFlags(holder.descriptionTextView.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
        }
    }

    @Override
    public int getItemCount() {
        return filteredTaskList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView dateTextView;
        TextView titleTextView;
        TextView descriptionTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            dateTextView = itemView.findViewById(R.id.taskdatecreate);
            titleTextView = itemView.findViewById(R.id.tasktitle);
            descriptionTextView = itemView.findViewById(R.id.taskdesc);
        }
    }

    private String formatDate(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        return sdf.format(new Date(timestamp));
    }


    private void filterTaskList() {
        filteredTaskList = new ArrayList<>();
        for (Task task : originalTaskList) {
            if (!task.isDeleted()) {
                filteredTaskList.add(task);
            }
        }
    }


    public void updateTaskList(List<Task> newTaskList) {
        this.originalTaskList = newTaskList;
        filterTaskList();
        notifyDataSetChanged();
    }
}
