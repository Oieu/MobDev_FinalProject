package com.example.final_project;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.ValueEventListener;

public class EditTask extends AppCompatActivity {

    private TextInputEditText etTaskTitle, etTaskDescription;
    private Button btnUpdateTask, btnDeleteTask;
    private DatabaseReference dbRef;
    private FirebaseAuth mAuth;
    private String taskId;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_task);

        etTaskTitle = findViewById(R.id.ettasktitle);
        etTaskDescription = findViewById(R.id.ettaskdescription);
        btnUpdateTask = findViewById(R.id.btnupdatetask);
        btnDeleteTask = findViewById(R.id.btndeletetask);

        mAuth = FirebaseAuth.getInstance();
        dbRef = FirebaseDatabase.getInstance("https://finalproject-848e0-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("tasks");

        // Get the task ID and other data from the intent
        taskId = getIntent().getStringExtra("TASK_ID");
        String taskTitle = getIntent().getStringExtra("TASK_TITLE");
        String taskDescription = getIntent().getStringExtra("TASK_DESCRIPTION");

        // Set the task data to the input fields
        etTaskTitle.setText(taskTitle);
        etTaskDescription.setText(taskDescription);

        btnUpdateTask.setOnClickListener(v -> updateTask());
        btnDeleteTask.setOnClickListener(v -> deleteTask());
    }

    private void updateTask() {
        // Fetch the existing task to get its current status
        dbRef.child(taskId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Task existingTask = dataSnapshot.getValue(Task.class);
                    if (existingTask != null) {
                        String taskTitle = etTaskTitle.getText().toString().trim();
                        String taskDescription = etTaskDescription.getText().toString().trim();

                        if (taskTitle.isEmpty() || taskDescription.isEmpty()) {
                            Toast.makeText(EditTask.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        // Create a new Task object with the existing status
                        Task task = new Task(taskId, taskTitle, taskDescription, existingTask.getTaskCreated(), existingTask.getUserId(), existingTask.getTaskStatus());
                        dbRef.child(taskId).setValue(task)
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(EditTask.this, "Task updated", Toast.LENGTH_SHORT).show();
                                    finish();
                                })
                                .addOnFailureListener(e -> Toast.makeText(EditTask.this, "Error updating task", Toast.LENGTH_SHORT).show());
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(EditTask.this, "Error fetching task", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteTask() {
        // Fetch the existing task to get its current status
        dbRef.child(taskId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Task existingTask = dataSnapshot.getValue(Task.class);
                    if (existingTask != null) {
                        // Create a new Task object with the existing status and set it as deleted
                        Task task = new Task(taskId, existingTask.getTaskTitle(), existingTask.getTaskDescription(), existingTask.getTaskCreated(), existingTask.getUserId(), existingTask.getTaskStatus());
                        task.setDeleted(true); // Set the task as deleted

                        dbRef.child(taskId).setValue(task) // Update the task in the database
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(EditTask.this, "Task deleted successfully", Toast.LENGTH_SHORT).show();
                                    finish();
                                })
                                .addOnFailureListener(e -> Toast.makeText(EditTask.this, "Error in deleting task", Toast.LENGTH_SHORT).show());
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(EditTask.this, "Error fetching task", Toast.LENGTH_SHORT).show();
            }
        });
    }
}