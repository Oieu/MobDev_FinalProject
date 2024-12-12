package com.example.final_project;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
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

public class MainActivity extends AppCompatActivity {

    private DatabaseReference dbRef;
    private RecyclerView inProgressRecyclerView;
    private RecyclerView completedRecyclerView;
    private TaskAdapter inProgressTaskAdapter;
    private TaskAdapter completedTaskAdapter;
    private List<Task> inProgressTaskList;
    private List<Task> completedTaskList;
    private FirebaseAuth mAuth;
    private BottomNavigationView bottomNav;
    private ValueEventListener listener;

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

        inProgressTaskList = new ArrayList<>();
        completedTaskList = new ArrayList<>();

        inProgressTaskAdapter = new TaskAdapter(inProgressTaskList, false);
        completedTaskAdapter = new TaskAdapter(completedTaskList, true);

        inProgressRecyclerView.setAdapter(inProgressTaskAdapter);
        completedRecyclerView.setAdapter(completedTaskAdapter);

        inProgressRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        completedRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        fetchTasksForUser  (userId);

        findViewById(R.id.btnaddtask).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddTask.class);
            startActivity(intent);
        });

        bottomNav = findViewById(R.id.bottomnav);
        bottomNav.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    startActivity(new Intent(this, MainActivity.class));
                    return true;
                case R.id.navigation_calendar:
                    startActivity(new Intent(this, CalendarPage.class));
                    return true;
//                case R.id.navigation_tasks:
//                    startActivity(new Intent(this, TasksActivity.class));
//                    return true;
                case R.id.navigation_account:
                    startActivity(new Intent(this, ProfileActivity.class));
                    return true;
            }
            return false;
        });
    }

    private void fetchTasksForUser (String userId) {
        listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                inProgressTaskList.clear();
                completedTaskList.clear();

                for (DataSnapshot taskSnapshot : dataSnapshot.getChildren()) {
                    Task task = taskSnapshot.getValue(Task.class);
                    if (task != null) {
                        task.setTaskId(taskSnapshot.getKey());

                        if (task.getTaskStatus().equals("in progress")) {
                            inProgressTaskList.add(task);
                        } else if (task.getTaskStatus().equals("completed")) {
                            completedTaskList.add(task);
                        }
                    }
                }

                inProgressTaskAdapter.setOnTaskStatusChangedListener(new TaskAdapter.OnTaskStatusChangedListener() {
                    @Override
                    public void onTaskStatusChanged(Task task) {
                        if (task.getTaskStatus().equals("completed")) {
                            inProgressTaskList.remove(task);
                            completedTaskList.add(task);
                        } else {
                            inProgressTaskList.add(task);
                            completedTaskList.remove(task);
                        }
                        inProgressTaskAdapter.notifyDataSetChanged();
                        completedTaskAdapter.notifyDataSetChanged();
                    }
                });

                completedTaskAdapter.setOnTaskStatusChangedListener(new TaskAdapter.OnTaskStatusChangedListener() {
                    @Override
                    public void onTaskStatusChanged(Task task) {
                        if (task.getTaskStatus().equals("in progress")) {
                            completedTaskList.remove(task);
                            inProgressTaskList.add(task);
                        } else {
                            completedTaskList.add(task);
                            inProgressTaskList.remove(task);
                        }
                        inProgressTaskAdapter.notifyDataSetChanged();
                        completedTaskAdapter.notifyDataSetChanged();
                    }
                });

                inProgressTaskAdapter.notifyDataSetChanged();
                completedTaskAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
            }
        };
        dbRef.orderByChild("userId").equalTo(userId).addValueEventListener(listener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (listener != null) {
            dbRef.removeEventListener(listener);
        }
    }
}
