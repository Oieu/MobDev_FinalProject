package com.example.final_project;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class EditProfileActivity extends AppCompatActivity {

    private EditText etFullName, etEmail;
    private Button btnSaveProfile;

    private FirebaseAuth mAuth;
    private DatabaseReference dbRef;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        mAuth = FirebaseAuth.getInstance();
        String userId = mAuth.getCurrentUser().getUid();
        dbRef = FirebaseDatabase.getInstance("https://finalproject-848e0-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("Users").child(userId);

        etFullName = findViewById(R.id.etFullName);
        etEmail = findViewById(R.id.etEmail);
        btnSaveProfile = findViewById(R.id.btnSaveProfile);

        String currentFullName = getIntent().getStringExtra("fullName");
        String currentEmail = getIntent().getStringExtra("email");

        etFullName.setText(currentFullName);
        etEmail.setText(currentEmail);

        btnSaveProfile.setOnClickListener(view -> {
            String fullName = etFullName.getText().toString();
            String email = etEmail.getText().toString();

            String[] nameParts = fullName.split(" ");
            String firstName = nameParts[0];
            String lastName = nameParts.length > 1 ? nameParts[1] : "";

            User updatedUser = new User(userId, firstName, lastName, email, 0);


            dbRef.setValue(updatedUser).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(EditProfileActivity.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                } else {
                    Toast.makeText(EditProfileActivity.this, "Error updating profile", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}