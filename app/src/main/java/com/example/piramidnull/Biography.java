package com.example.piramidnull;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Biography extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_biography);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ImageView backArrow = findViewById(R.id.backArrow);
        ImageView navNotes = findViewById(R.id.navigation_notes);
        ImageView navProfile = findViewById(R.id.navigation_profile);
        ImageView navMain = findViewById(R.id.navigation_main);
        ImageView navBio = findViewById(R.id.navigation_bio); // active page

        backArrow.setOnClickListener(v -> {
            finish(); // go back to previous activity
        });

        navNotes.setOnClickListener(v -> {
            Toast.makeText(this, "Go to Notes", Toast.LENGTH_SHORT).show();
            // startActivity(new Intent(this, NotesActivity.class));
        });

        navProfile.setOnClickListener(v -> {
            Toast.makeText(this, "Go to Profile", Toast.LENGTH_SHORT).show();
            // startActivity(new Intent(this, ProfileActivity.class));
        });

        navMain.setOnClickListener(v -> {
            Toast.makeText(this, "Go to Main", Toast.LENGTH_SHORT).show();
            // startActivity(new Intent(this, MainActivity.class));
        });

        navBio.setOnClickListener(v -> {
            Toast.makeText(this, "Already in Biography", Toast.LENGTH_SHORT).show();
        });
    }
}
