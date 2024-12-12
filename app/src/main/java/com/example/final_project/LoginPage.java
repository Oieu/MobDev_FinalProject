package com.example.final_project;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

public class LoginPage extends AppCompatActivity {

    EditText emailField, passwordField;
    Button loginButton;
    TextView registerLink;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);

        auth = FirebaseAuth.getInstance();

        emailField = findViewById(R.id.etLoginEmail);
        passwordField = findViewById(R.id.etLoginPassword);
        loginButton = findViewById(R.id.btnLogin);
        registerLink = findViewById(R.id.tvRegisterLink);

        loginButton.setOnClickListener(v -> {
            loginUserAccount();
        });

        findViewById(R.id.tvRegisterLink).setOnClickListener(v -> {
            Intent intent = new Intent(LoginPage.this, RegistrationPage.class);
            startActivity(intent);
        });
    }

    public void loginUserAccount() {
        String email = emailField.getText().toString().trim();
        String password = passwordField.getText().toString().trim();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(LoginPage.this, "Please enter email and password", Toast.LENGTH_SHORT).show();
        } else {
            auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser user = auth.getCurrentUser();
                            if (user != null) {
                                Intent intent = new Intent(LoginPage.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        } else {
                            Exception exception = task.getException();
                            if (exception instanceof FirebaseAuthInvalidUserException) {
                                Toast.makeText(LoginPage.this, "No account found with this email", Toast.LENGTH_SHORT).show();
                            } else if (exception instanceof FirebaseAuthInvalidCredentialsException) {
                                Toast.makeText(LoginPage.this, "Invalid password. Please try again", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(LoginPage.this, "Login failed. Please try again", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

}
