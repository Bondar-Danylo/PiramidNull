package com.example.piramidnull;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import android.widget.ArrayAdapter;

public class CreateAccountDetails extends AppCompatActivity {
    Button createAccount;
    EditText usernameInput, emailInput;
    Spinner genderSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.createaccount_details);

        // Initialize views
        createAccount = findViewById(R.id.createaccount_button);
        usernameInput = findViewById(R.id.username_input);
        emailInput = findViewById(R.id.email_input);
        genderSpinner = findViewById(R.id.genderSpinner);

        // Set up spinner for gender/voice type (already in layout)
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.voice_types,
                R.layout.spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderSpinner.setAdapter(adapter);

        // Set up "Create Account" button click listener
        createAccount.setOnClickListener(v -> {
            String username = usernameInput.getText().toString().trim();
            String email = emailInput.getText().toString().trim();
            String voice = genderSpinner.getSelectedItem().toString();

            // Validate input
            if (username.isEmpty() || email.isEmpty()) {
                Toast.makeText(CreateAccountDetails.this, "Please enter username and email", Toast.LENGTH_SHORT).show();
                return;
            }

            // Pass the data to MainSliderActivity via Intent
            Intent intent = new Intent(this, MainSliderActivity.class);
            intent.putExtra("USERNAME", username);
            intent.putExtra("EMAIL", email);
            intent.putExtra("VOICE_TYPE", voice);
            startActivity(intent);

            finish();
        });
    }
}
