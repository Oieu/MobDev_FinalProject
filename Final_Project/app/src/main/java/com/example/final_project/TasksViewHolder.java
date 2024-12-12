package com.example.final_project;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Date;

//This Class Is the ViewHolder for the Recycler view in Main

public class TasksViewHolder  extends RecyclerView.ViewHolder {

    TextView taskName;
    TextView taskDesc;
    TextView taskDate;


    public TasksViewHolder(@NonNull View itemView) {
        super(itemView);

    //ID's are from recycler_item.xml

        taskName = itemView.findViewById(R.id.tasktitle);
        taskDesc = itemView.findViewById(R.id.taskdesc);
        taskDate =itemView.findViewById(R.id.taskdatecreate);
    }
}
