package com.example.final_project;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements TaskAdapter.OnTaskStatusChangedListener {

    private RecyclerView inProgressRecyclerView;
    private RecyclerView completedRecyclerView;
    private BottomNavigationView bottomNav;
    private TextView tvWelcome, inProgress, completed;
    private Button btnAddTask;

    private ArrayList<Task> inProgressTasks = new ArrayList<>();
    private ArrayList<Task> completedTasks = new ArrayList<>();
    private TaskAdapter inProgressAdapter;
    private TaskAdapter completedAdapter;

    private DatabaseReference dbRef;
    private FirebaseAuth mAuth;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        String userId = mAuth.getCurrentUser().getUid(); // Get the user ID

        dbRef = FirebaseDatabase.getInstance("https://finalproject-848e0-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("tasks");

        inProgressRecyclerView = findViewById(R.id.in_progress_recyclerview);
        completedRecyclerView = findViewById(R.id.completed_recyclerview);
        btnAddTask = findViewById(R.id.btnaddtask);
        tvWelcome = findViewById(R.id.tvwelcome);
        inProgress = findViewById(R.id.inprogresstext);
        completed = findViewById(R.id.completedtext);
        bottomNav = findViewById(R.id.bottomnav);

        inProgressAdapter = new TaskAdapter(inProgressTasks, this);
        completedAdapter = new TaskAdapter(completedTasks, this);

        inProgressRecyclerView.setAdapter(inProgressAdapter);
        completedRecyclerView.setAdapter(completedAdapter);

        inProgressRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        completedRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        fetchTasksFromFirebase();

        btnAddTask.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddTask.class);
            startActivity(intent);
        });

        bottomNav.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    startActivity(new Intent(this, MainActivity.class));
                    return true;
                case R.id.navigation_calendar:
                    startActivity(new Intent(this, CalendarPage.class));
                    return true;
                // Add more navigation items here
                default:
                    return false;
            }
        });
    }

    private void fetchTasksFromFirebase() {
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                inProgressTasks.clear();
                completedTasks.clear();

                for (DataSnapshot taskSnapshot : snapshot.getChildren()) {
                    Task task = taskSnapshot.getValue(Task.class);
                    if (task.getUserId().equals(mAuth.getCurrentUser().getUid()) && !task.isDeleted()) {
                        if (task.getTaskStatus().equals("in progress")) {
                            inProgressTasks.add(task);
                        } else if (task.getTaskStatus().equals("completed")) {
                            completedTasks.add(task);
                        }
                    }
                }

                inProgressAdapter.notifyDataSetChanged();
                completedAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, "Error fetching tasks", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onTaskStatusChanged(Task task) {
        if (task.isDeleted()) {
            // Instead of directly setting the value in Firebase, we set the deleted state in the Task object
            task.setDeleted(true);
            dbRef.child(task.getTaskId()).setValue(task); // Update the task in Firebase
        } else {
            dbRef.child(task.getTaskId()).setValue(task); // Update the task in Firebase
            if (task.getTaskStatus().equals("completed")) {
                inProgressTasks.remove(task);
                completedTasks.add(task);
            } else {
                inProgressTasks.add(task);
                completedTasks.remove(task);
            }
        }
        inProgressAdapter.notifyDataSetChanged();
        completedAdapter.notifyDataSetChanged();
    }


}
