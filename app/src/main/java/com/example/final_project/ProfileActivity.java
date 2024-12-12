package com.example.final_project;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity {

    private static final String TAG = "ProfileActivity";

    private TextView tvFullName, tvEmail, tvCompletedTasks, tvPendingTasks;
    private Button btnEditProfile;

    private FirebaseAuth mAuth;
    private DatabaseReference dbRef;
    private DatabaseReference taskRef;

    private final ActivityResultLauncher<Intent> editProfileLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == RESULT_OK) {
                            fetchUserProfile();
                        }
                    });

    private BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        String userId = mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getUid() : null;

        if (userId == null) {
            Toast.makeText(this, "Error: User not logged in", Toast.LENGTH_LONG).show();
            Log.e(TAG, "User not logged in.");
            finish();
            return;
        }

        dbRef = FirebaseDatabase.getInstance("https://finalproject-848e0-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("Users").child(userId);
        taskRef = FirebaseDatabase.getInstance("https://finalproject-848e0-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("tasks");

        // Initialize UI elements
        tvFullName = findViewById(R.id.tvFullName);
        tvEmail = findViewById(R.id.tvEmail);
        tvCompletedTasks = findViewById(R.id.tvCompletedTasks);
        tvPendingTasks = findViewById(R.id.tvPendingTasks);
        btnEditProfile = findViewById(R.id.btnEditProfile);
        bottomNav = findViewById(R.id.bottomnav);

        // Fetch user details and tasks
        fetchUserProfile();
        fetchCompletedTasksCount(userId);
        fetchPendingTasksCount(userId);

        // Debugging: Setting test text
        Log.d(TAG, "Initial Pending Tasks: " + tvPendingTasks.getText());
        Log.d(TAG, "Initial Completed Tasks: " + tvCompletedTasks.getText());

        // Bottom Navigation
        bottomNav.setSelectedItemId(R.id.navigation_tasks);
        bottomNav.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.navigation_tasks:
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    finish();
                    return true;
                case R.id.navigation_calendar:
                    startActivity(new Intent(getApplicationContext(), CalendarPage.class));
                    finish();
                    return true;
                case R.id.navigation_home:
                    startActivity(new Intent(getApplicationContext(), DashBoard.class));
                    finish();
                    return true;
                case R.id.navigation_account:
                    return true; // Stay in the current activity
                default:
                    return false;
            }
        });

        // Edit Profile Button
        btnEditProfile.setOnClickListener(v -> {
            Intent editIntent = new Intent(ProfileActivity.this, EditProfileActivity.class);
            editIntent.putExtra("fullName", tvFullName.getText().toString());
            editIntent.putExtra("email", tvEmail.getText().toString());
            editProfileLauncher.launch(editIntent);
        });

        // Change Password Button
        findViewById(R.id.btnChangePassword).setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, ChangePasswordActivity.class);
            startActivity(intent);
        });

        // Logout Button
        findViewById(R.id.btnLogout).setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(ProfileActivity.this, LoginPage.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }

    private void fetchUserProfile() {
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    User user = snapshot.getValue(User.class);
                    if (user != null) {
                        tvFullName.setText(user.getFirstName() + " " + user.getLastName());
                        tvEmail.setText(user.getEmail());
                        Log.d(TAG, "User profile loaded successfully.");
                    }
                } else {
                    Log.e(TAG, "User profile not found.");
                    Toast.makeText(ProfileActivity.this, "User profile not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e(TAG, "Error fetching user profile: " + error.getMessage());
                Toast.makeText(ProfileActivity.this, "Error fetching profile", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchCompletedTasksCount(String userId) {
        taskRef.orderByChild("userId").equalTo(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int completedTasksCount = 0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Task task = snapshot.getValue(Task.class);
                    if (task != null && !task.isDeleted() && "completed".equalsIgnoreCase(task.getTaskStatus())) {
                        completedTasksCount++;
                    }
                }
                tvCompletedTasks.setText(String.valueOf(completedTasksCount));
                Log.d(TAG, "Completed tasks count: " + completedTasksCount);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Error fetching completed tasks: " + databaseError.getMessage());
            }
        });
    }

    private void fetchPendingTasksCount(String userId) {
        taskRef.orderByChild("userId").equalTo(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int pendingTasksCount = 0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Task task = snapshot.getValue(Task.class);
                    if (task != null && !task.isDeleted() && "in progress".equalsIgnoreCase(task.getTaskStatus())) {
                        pendingTasksCount++;
                    }
                }
                tvPendingTasks.setText(String.valueOf(pendingTasksCount));
                Log.d(TAG, "Pending tasks count: " + pendingTasksCount);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Error fetching pending tasks: " + databaseError.getMessage());
            }
        });
    }
}
