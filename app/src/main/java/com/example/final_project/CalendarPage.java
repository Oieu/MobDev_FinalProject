package com.example.final_project;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.CalendarView;
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
import java.util.Calendar;
import java.util.List;

public class CalendarPage extends AppCompatActivity {

    private CalendarView calendarView;
    private RecyclerView recyclerView;
    private TaskAdapter taskAdapter;
    private List<Task> taskList;
    private DatabaseReference dbRef;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar_page);

        calendarView = findViewById(R.id.calendarView);
        recyclerView = findViewById(R.id.CalendarrecyclerView);
        BottomNavigationView bottomNav = findViewById(R.id.bottomnav);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        taskList = new ArrayList<>();
        taskAdapter = new TaskAdapter(taskList, false);
        recyclerView.setAdapter(taskAdapter);

        dbRef = FirebaseDatabase.getInstance("https://finalproject-848e0-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("tasks");
        mAuth = FirebaseAuth.getInstance();

        loadTasksForDate(calendarView.getDate());

        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            long selectedDateInMillis = getDateInMillis(year, month, dayOfMonth);
            loadTasksForDate(selectedDateInMillis);
        });

        bottomNav.setSelectedItemId(R.id.navigation_calendar);
        bottomNav.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.navigation_calendar:
                    return true;
                case R.id.navigation_home:
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    finish();
                    return true;
//                case R.id.navigation_tasks:
//                    startActivity(new Intent(getApplicationContext(), TasksActivity.class));
//                    finish();
//                    return true;
                case R.id.navigation_account:
                    startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                    finish();
                    return true;
                default:
                    return false;
            }
        });
    }

    private void loadTasksForDate(long dateInMillis) {
        String userId = mAuth.getCurrentUser().getUid();
        long startOfDay = getStartOfDayInMillis(dateInMillis);
        long endOfDay = getEndOfDayInMillis(dateInMillis);

        Log.d("CalendarPage", "Start of day: " + startOfDay);
        Log.d("CalendarPage", "End of day: " + endOfDay);

        dbRef.orderByChild("taskCreated").startAt(startOfDay).endAt(endOfDay)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        taskList.clear();
                        if (!dataSnapshot.exists()) {
                            Toast.makeText(CalendarPage.this, "No tasks found for this date", Toast.LENGTH_SHORT).show();
                            Log.d("CalendarPage", "No tasks found for the selected date.");
                        } else {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                Task task = snapshot.getValue(Task.class);
                                if (task != null && task.getUserId().equals(userId)) {
                                    taskList.add(task);
                                }
                            }
                        }
                        Log.d("CalendarPage", "Tasks found: " + taskList.size());
                        taskAdapter.notifyDataSetChanged(); // Notify the adapter to refresh the RecyclerView
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Handle error
                        Log.e("CalendarPage", "Database error: " + databaseError.getMessage());
                    }
                });
    }

    private long getDateInMillis(int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day, 0, 0, 0);
        return calendar.getTimeInMillis();
    }

    private long getStartOfDayInMillis(long dateInMillis) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(dateInMillis);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }

    private long getEndOfDayInMillis(long dateInMillis) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(dateInMillis);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTimeInMillis();
    }
}
