package com.example.final_project;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DashBoard extends AppCompatActivity {

    private BottomNavigationView bottomNav;
    private TextView tvGreeting, tvDateTime, tvCompletedTasks, tvPendingTasks;
    private RecyclerView todayTaskRecyclerView;
    private DashboardTaskAdapter taskAdapter;
    private List<Task> taskList;
    private DatabaseReference dbRef;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash_board);

        bottomNav = findViewById(R.id.bottomnav);
        tvGreeting = findViewById(R.id.tvGreeting);
        tvDateTime = findViewById(R.id.tvDateTime);
        tvCompletedTasks = findViewById(R.id.tvCompletedTasks);
        tvPendingTasks = findViewById(R.id.tvPendingTasks);
        todayTaskRecyclerView = findViewById(R.id.todaystaskrecycler);

        mAuth = FirebaseAuth.getInstance();
        dbRef = FirebaseDatabase.getInstance("https://finalproject-848e0-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("tasks");

        String userId = mAuth.getCurrentUser().getUid();
        fetchUserName(userId);
        fetchTaskCounts(userId);
        fetchTasksForToday(userId);
        displayCurrentDateTime();

        bottomNav.setSelectedItemId(R.id.navigation_home);
        bottomNav.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    return true;
                case R.id.navigation_calendar:
                    startActivity(new Intent(getApplicationContext(), CalendarPage.class));
                    finish();
                    return true;
                case R.id.navigation_tasks:
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    finish();
                    return true;
                case R.id.navigation_account:
                    startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                    finish();
                    return true;
                default:
                    return false;
            }
        });

        todayTaskRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        taskList = new ArrayList<>();
        taskAdapter = new DashboardTaskAdapter(taskList);
        todayTaskRecyclerView.setAdapter(taskAdapter);
    }

    private void fetchUserName(String userId) {
        DatabaseReference userRef = FirebaseDatabase.getInstance("https://finalproject-848e0-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("Users");
        userRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    User user = dataSnapshot.getValue(User.class);
                    if (user != null) {
                        String greeting = "Hello " + user.getFirstName() + " " + user.getLastName();
                        tvGreeting.setText(greeting);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle possible errors.
            }
        });
    }

    private void fetchTaskCounts(String userId) {
        dbRef.orderByChild("userId").equalTo(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int pendingTasksCount = 0;
                int completedTasksCount = 0;

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Task task = snapshot.getValue(Task.class);
                    if (task != null) {
                        if ("in progress".equals(task.getTaskStatus())) {
                            pendingTasksCount++;
                        } else if ("completed".equals(task.getTaskStatus())) {
                            completedTasksCount++;
                        }
                    }
                }

                tvPendingTasks.setText(String.valueOf(pendingTasksCount));
                tvCompletedTasks.setText(String.valueOf(completedTasksCount));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
            }
        });
    }

    private void fetchTasksForToday(String userId) {
        long todayStartMillis = getStartOfDayInMillis(new Date());
        long todayEndMillis = getEndOfDayInMillis(new Date());

        dbRef.orderByChild("taskCreated")
                .startAt(todayStartMillis)
                .endAt(todayEndMillis)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        taskList.clear();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Task task = snapshot.getValue(Task.class);
                            if (task != null && userId != null && userId.equals(task.getUserId())) {
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


    private long getStartOfDayInMillis(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }

    private long getEndOfDayInMillis(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTimeInMillis();
    }

    private void displayCurrentDateTime() {
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.getDefault());
        timeFormat.setTimeZone(java.util.TimeZone.getTimeZone("Asia/Manila")); // Set to the local timezone
        dateFormat.setTimeZone(java.util.TimeZone.getTimeZone("Asia/Manila")); // Set to the local timezone

        String currentTime = timeFormat.format(new Date());
        String currentDate = dateFormat.format(new Date());

        String dateTimeText = "Time is: " + currentTime + "\n" + currentDate;
        tvDateTime.setText(dateTimeText);
    }
}
