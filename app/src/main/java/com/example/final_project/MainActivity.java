package com.example.final_project;

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
    private RecyclerView recyclerView;
    private TaskAdapter taskAdapter;
    private List<Task> taskList;
    private FirebaseAuth mAuth;
    private BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        String userId = mAuth.getCurrentUser().getUid(); // Get the user ID

        dbRef = FirebaseDatabase.getInstance("https://finalproject-848e0-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("tasks");

        recyclerView = findViewById(R.id.taskrecyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        taskList = new ArrayList<>();
        taskAdapter = new TaskAdapter(taskList);
        recyclerView.setAdapter(taskAdapter);

        fetchTasksForUser(userId);

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

    private void fetchTasksForUser(String userId) {
        dbRef.orderByChild("userId").equalTo(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                taskList.clear();
                for (DataSnapshot taskSnapshot : dataSnapshot.getChildren()) {
                    Task task = taskSnapshot.getValue(Task.class);
                    if (task != null) {
                        task.setTaskId(taskSnapshot.getKey());
                        taskList.add(task);
                    }
                }
                taskAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
            }
        });
    }
}
