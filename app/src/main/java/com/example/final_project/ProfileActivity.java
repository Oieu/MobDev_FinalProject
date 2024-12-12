package com.example.final_project;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ProfileActivity extends AppCompatActivity {

    private BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        bottomNav = findViewById(R.id.bottomnav);

        bottomNav.setSelectedItemId(R.id.navigation_account);
        bottomNav.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.navigation_account:
                    return true;
                case R.id.navigation_tasks:
                    startActivity(new Intent(getApplicationContext(),  MainActivity.class));
                    finish();
                    return true;

                case R.id.navigation_calendar:
                    startActivity(new Intent(getApplicationContext(), CalendarPage.class));
                    finish();
                    return  true;

                case R.id.navigation_home:
                    startActivity(new Intent(getApplicationContext(), DashBoard.class));
                    finish();
                    return  true;

                default:
                    return false;
            }
        });
    }



}