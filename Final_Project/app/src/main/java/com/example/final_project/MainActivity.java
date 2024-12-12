package com.example.final_project;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);


        List<Tasks> tasksList = new ArrayList<Tasks>();

        tasksList.add(new Tasks("Zoom Meeting", "Meeting of zoom with members", new Date()));
        tasksList.add(new Tasks("Zoom Meeting", "Meeting of zoom with members", new Date()));
        tasksList.add(new Tasks("Zoom Meeting", "Meeting of zoom with members", new Date()));
        tasksList.add(new Tasks("Zoom Meeting", "Meeting of zoom with members", new Date()));
        tasksList.add(new Tasks("Zoom Meeting", "Meeting of zoom with members", new Date()));
        tasksList.add(new Tasks("Zoom Meeting", "Meeting of zoom with members", new Date()));

        RecyclerView taskRecyclerView = findViewById(R.id.taskRecyclerView);
        taskRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        taskRecyclerView.setAdapter(new TasksAdapter(getApplicationContext(),tasksList));


    }
}