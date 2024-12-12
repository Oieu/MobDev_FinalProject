package com.example.final_project;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegistrationPage extends AppCompatActivity {

    EditText etFirstName, etLastName, etEmail, etPassword, etConfirmPassword;
    Button btnRegister;
    TextView backToLogin;

    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_page);

        etFirstName = findViewById(R.id.etFirstName);
        etLastName = findViewById(R.id.etLastName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnRegister = findViewById(R.id.btnRegister);
        backToLogin = findViewById(R.id.tvLoginLink);

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance("https://finalproject-848e0-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("Users");

        btnRegister.setOnClickListener(v -> {
            if (isPasswordMatching()) {
                registerUser();
            } else {
                Toast.makeText(getApplicationContext(), "Passwords did not match",
                        Toast.LENGTH_SHORT).show();
            }
        });

        backToLogin.setOnClickListener(v -> {
            Intent intent = new Intent(RegistrationPage.this, LoginPage.class);
            startActivity(intent);
        });
    }

    public void registerUser() {
        String firstName = etFirstName.getText().toString().trim();
        String lastName = etLastName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        if (TextUtils.isEmpty(firstName) || TextUtils.isEmpty(lastName) ||
                TextUtils.isEmpty(email) || TextUtils.isEmpty(password) ||
                TextUtils.isEmpty(confirmPassword)) {
            Toast.makeText(RegistrationPage.this, "Please fill in all fields",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 8) {
            etPassword.setError("Password must be greater than 8 characters.");
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        String userId = mAuth.getCurrentUser().getUid();


                        User user = new User(userId, firstName, lastName, email);


                        databaseReference.child(userId).setValue(user)
                                .addOnCompleteListener(dbTask -> {
                                    if (dbTask.isSuccessful()) {
                                        Toast.makeText(RegistrationPage.this,
                                                "Registration successful",
                                                Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(RegistrationPage.this, MainActivity.class);
                                        startActivity(intent);
                                    } else {
                                        Toast.makeText(RegistrationPage.this,
                                                "Failed to save user data. Please try again.",
                                                Toast.LENGTH_LONG).show();
                                    }
                                });

                    } else {
                        Toast.makeText(RegistrationPage.this,
                                "Registration failed. Please try again.",
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    public boolean isPasswordMatching() {
        String password = etPassword.getText().toString().trim();
        String confirmPass = etConfirmPassword.getText().toString().trim();
        return password.equals(confirmPass);
    }
}
