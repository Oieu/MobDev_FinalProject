package com.example.final_project;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity {

    private TextView tvFullName, tvEmail;
    private Button btnEditProfile;

    private FirebaseAuth mAuth;
    private DatabaseReference dbRef;

    private final ActivityResultLauncher<Intent> editProfileLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == RESULT_OK) {
                            fetchUserProfile();
                        }
                    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();
        String userId = mAuth.getCurrentUser().getUid();
        dbRef = FirebaseDatabase.getInstance("https://finalproject-848e0-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("Users").child(userId);

        tvFullName = findViewById(R.id.tvFullName);
        tvEmail = findViewById(R.id.tvEmail);
        btnEditProfile = findViewById(R.id.btnEditProfile);

        fetchUserProfile();

        btnEditProfile.setOnClickListener(v -> {
            Intent editIntent = new Intent(ProfileActivity.this, EditProfileActivity.class);
            editIntent.putExtra("fullName", tvFullName.getText().toString());
            editIntent.putExtra("email", tvEmail.getText().toString());
            editProfileLauncher.launch(editIntent);
        });

        findViewById(R.id.btnChangePassword).setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, ChangePasswordActivity.class);
            startActivity(intent);
        });

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
                    }
                } else {
                    Toast.makeText(ProfileActivity.this, "User profile not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(ProfileActivity.this, "Error fetching profile", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

