package com.example.final_project;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AddTask extends AppCompatActivity {

    private TextInputEditText etTaskTitle, etTaskDescription;
    private MaterialButton btnSaveTask;
    private DatabaseReference dbRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        etTaskTitle = findViewById(R.id.ettasktitle);
        etTaskDescription = findViewById(R.id.ettaskdescription);
        btnSaveTask = findViewById(R.id.btnsavetask);
        dbRef = FirebaseDatabase.getInstance(FirebaseConfig.dbURL)
                .getReference("tasks");

        btnSaveTask.setOnClickListener(v -> saveTaskToFirebase());
    }

    private void saveTaskToFirebase() {
        String taskTitle = etTaskTitle.getText().toString().trim();
        String taskDescription = etTaskDescription.getText().toString().trim();
        long currentDate = new Date().getTime();

        if (taskTitle.isEmpty() || taskDescription.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        String taskId = dbRef.push().getKey();
        if (taskId == null) {
            Toast.makeText(this, "Error generating task ID", Toast.LENGTH_SHORT).show();
            return;
        }

        Task task = new Task(taskId, taskTitle, taskDescription, currentDate, false);
        dbRef.child(taskId).setValue(task)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(AddTask.this, "Task added", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(AddTask.this, "Error adding task", Toast.LENGTH_SHORT).show());
    }

}

