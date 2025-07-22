package com.example.piramidnull;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

public class profile extends AppCompatActivity {

    EditText username, email, password;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Initialize Views
        ImageView backArrow = findViewById(R.id.backArrow);
        ImageView settingsIcon = findViewById(R.id.settingsIcon);
        ImageView avatarImage = findViewById(R.id.avatarImage);
        username = findViewById(R.id.username);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        Button editProfileBtn = findViewById(R.id.editProfileBtn);
        TextView logOut = findViewById(R.id.logOut);

        ImageView navNotes = findViewById(R.id.navigation_notes);
        ImageView navProfile = findViewById(R.id.navigation_profile);
        ImageView navMain = findViewById(R.id.navigation_main);
        ImageView navBio = findViewById(R.id.navigation_bio);


        // Navigation & UI Listeners
        settingsIcon.setOnClickListener(v -> {
            Intent intent = new Intent(profile.this, Settings.class);
            startActivity(intent);
        });

        backArrow.setOnClickListener(v -> finish());


        logOut.setOnClickListener(v -> {
            Toast.makeText(this, "Logging out...", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(profile.this, LoadingPage.class);
            startActivity(intent);
        });;

        // Save data on button click
        editProfileBtn.setOnClickListener(v -> {
            String userNameInput = username.getText().toString().trim();
            String emailInput = email.getText().toString().trim();
            String passwordInput = password.getText().toString().trim();

            if (userNameInput.isEmpty() || emailInput.isEmpty() || passwordInput.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Profile Updated", Toast.LENGTH_SHORT).show();
            }
        });
    }
}


