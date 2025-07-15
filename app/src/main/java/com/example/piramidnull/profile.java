package com.example.piramidnull;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

public class profile extends AppCompatActivity {

     EditText username, email, birthday;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        ImageView backArrow = findViewById(R.id.backArrow);
        ImageView settingsIcon = findViewById(R.id.settingsIcon);
        ImageView avatarImage = findViewById(R.id.avatarImage);
        username = findViewById(R.id.username);
        email = findViewById(R.id.email);
        birthday = findViewById(R.id.birthday);
        Button editProfileBtn = findViewById(R.id.editProfileBtn);
        TextView logOut = findViewById(R.id.logOut);

        ImageView navNotes = findViewById(R.id.navigation_notes);
        ImageView navProfile = findViewById(R.id.navigation_profile);
        ImageView navMain = findViewById(R.id.navigation_main);
        ImageView navBio = findViewById(R.id.navigation_bio);

        settingsIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(profile.this, Settings.class);
                startActivity(intent);
            }
        });



        backArrow.setOnClickListener(v -> finish());


        avatarImage.setOnClickListener(v ->
                Toast.makeText(this, "Edit avatar clicked", Toast.LENGTH_SHORT).show()
        );

        editProfileBtn.setOnClickListener(v -> {
            String userNameInput = username.getText().toString().trim();
            String emailInput = email.getText().toString().trim();
            String birthdayInput = birthday.getText().toString().trim();

            if (userNameInput.isEmpty() || emailInput.isEmpty() || birthdayInput.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Profile Updated", Toast.LENGTH_SHORT).show();
            }
        });

        logOut.setOnClickListener(v -> {
            Toast.makeText(this, "Logging out...", Toast.LENGTH_SHORT).show();
            finish();
        });

        navNotes.setOnClickListener(v -> Toast.makeText(this, "Notes", Toast.LENGTH_SHORT).show());
        navProfile.setOnClickListener(v -> Toast.makeText(this, "Profile", Toast.LENGTH_SHORT).show());
        navMain.setOnClickListener(v -> Toast.makeText(this, "Main", Toast.LENGTH_SHORT).show());
        navBio.setOnClickListener(v -> Toast.makeText(this, "Bio", Toast.LENGTH_SHORT).show());
    }
}

