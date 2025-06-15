package com.example.piramidnull;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.ViewOutlineProvider;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class CreateAccountDetails extends AppCompatActivity {
    private Button createAccount;
    private EditText usernameInput, passwordInput, birthdayInput;
    private Spinner genderSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.createaccount_details);

        initializeViews();
        setupAvatarDisplay();
        setupBackButton();
        setupGenderSpinner();
        setupBirthdayPicker();
        setupCreateAccountButton();
    }

    private void initializeViews() {
        usernameInput = findViewById(R.id.username_input);
        passwordInput = findViewById(R.id.password_input);
        birthdayInput = findViewById(R.id.birthday_input);
        genderSpinner = findViewById(R.id.genderSpinner);
        createAccount = findViewById(R.id.createaccount_button);

        // Debug logging to check if views are found
        if (createAccount == null) {
            System.out.println("ERROR: createAccount button not found!");
        }
        if (genderSpinner == null) {
            System.out.println("ERROR: genderSpinner not found!");
        }
    }

    private void setupAvatarDisplay() {
        ImageView avatar = findViewById(R.id.detailAvatarImage);
        ImageView background = findViewById(R.id.detailAvatarBackground);
        Intent intent = getIntent();

        if (avatar != null && background != null) {
            avatar.setImageResource(intent.getIntExtra("SELECTED_AVATAR", R.drawable.avatar_4));
            background.setImageResource(intent.getIntExtra("SELECTED_BACKGROUND", R.drawable.background_1));
        }
    }

    private void setupBackButton() {
        ImageButton back = findViewById(R.id.back_icon);
        if (back != null) {
            back.setOnClickListener(v -> {
                setResult(RESULT_OK, new Intent().putExtra("GO_BACK_TO_BACKGROUND_STEP", true));
                finish();
            });
        }
    }

    private void setupGenderSpinner() {
        if (genderSpinner != null) {
            // Create a simple string array instead of using resources for testing
            String[] voiceTypes = {"Select Voice Type", "Male", "Female", "Neutral"};

            // Try a simpler approach first
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_spinner_item, voiceTypes) {

                @Override
                public boolean isEnabled(int position) {
                    // Disable the first item (placeholder)
                    return position != 0;
                }

                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    View view = super.getView(position, convertView, parent);
                    TextView textView = (TextView) view;

                    // Set text color based on your theme
                    if (position == 0) {
                        textView.setTextColor(Color.GRAY); // Placeholder color
                    } else {
                        textView.setTextColor(getResources().getColor(R.color.sand));
                    }
                    textView.setTextSize(16);
                    textView.setPadding(16, 12, 16, 12);
                    return view;
                }

                @Override
                public View getDropDownView(int position, View convertView, ViewGroup parent) {
                    View view = super.getDropDownView(position, convertView, parent);
                    TextView textView = (TextView) view;

                    if (position == 0) {
                        textView.setTextColor(Color.GRAY);
                        textView.setBackgroundColor(Color.LTGRAY);
                    } else {
                        textView.setTextColor(Color.BLACK); // Use black for better visibility
                        textView.setBackgroundColor(Color.WHITE);
                    }
                    textView.setTextSize(16);
                    textView.setPadding(16, 12, 16, 12);
                    return view;
                }
            };

            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            genderSpinner.setAdapter(adapter);

            // Set default selection to first item (placeholder)
            genderSpinner.setSelection(0);
        }
    }

    private void setupBirthdayPicker() {
        if (birthdayInput != null) {
            birthdayInput.setOnClickListener(v -> showDatePicker());
        }
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    String selectedDate = String.format("%02d/%02d/%d", dayOfMonth, month + 1, year);
                    birthdayInput.setText(selectedDate);
                },
                calendar.get(Calendar.YEAR) - 18,
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );

        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());

        Calendar minDate = Calendar.getInstance();
        minDate.add(Calendar.YEAR, -100);
        datePickerDialog.getDatePicker().setMinDate(minDate.getTimeInMillis());

        datePickerDialog.show();
    }

    private void setupCreateAccountButton() {
        if (createAccount != null) {
            // Add debug logging
            System.out.println("Setting up create account button...");

            createAccount.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    System.out.println("Create account button clicked!");
                    Toast.makeText(CreateAccountDetails.this, "Button clicked!", Toast.LENGTH_SHORT).show();

                    if (validateForm()) {
                        createAccount();
                    }
                }
            });

            // Also set background and ensure it's clickable
            createAccount.setClickable(true);
            createAccount.setEnabled(true);

            // If you have a custom background, make sure it's applied
            // createAccount.setBackgroundResource(R.drawable.your_button_background);
        } else {
            System.out.println("Create account button is null!");
        }
    }

    private boolean validateForm() {
        // Validate username
        if (TextUtils.isEmpty(usernameInput.getText().toString().trim())) {
            usernameInput.setError("Username is required");
            usernameInput.requestFocus();
            return false;
        }

        // Validate password
        String password = passwordInput.getText().toString();
        if (TextUtils.isEmpty(password)) {
            passwordInput.setError("Password is required");
            passwordInput.requestFocus();
            return false;
        }

        if (password.length() < 6) {
            passwordInput.setError("Password must be at least 6 characters");
            passwordInput.requestFocus();
            return false;
        }

        // Validate birthday
        if (TextUtils.isEmpty(birthdayInput.getText().toString().trim())) {
            Toast.makeText(this, "Please select your birthday", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Validate voice type selection (check if not placeholder)
        if (genderSpinner.getSelectedItemPosition() == 0) {
            Toast.makeText(this, "Please select a voice type", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void createAccount() {
        // Get form data
        String username = usernameInput.getText().toString().trim();
        String password = passwordInput.getText().toString();
        String birthday = birthdayInput.getText().toString().trim();
        String voiceType = genderSpinner.getSelectedItem().toString();

        // Show success message
        Toast.makeText(this, "Account created successfully!", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(CreateAccountDetails.this, MainSliderActivity.class);
        intent.putExtra("USERNAME", username);
        intent.putExtra("VOICE_TYPE", voiceType);
        startActivity(intent);
        finish();
    }
}