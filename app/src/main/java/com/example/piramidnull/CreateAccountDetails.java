package com.example.piramidnull;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class CreateAccountDetails extends AppCompatActivity {

    private ImageView detailAvatarBackground, detailAvatarImage;
    private Spinner genderSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.createaccount_details);

        // Initialize avatar views
        detailAvatarBackground = findViewById(R.id.detailAvatarBackground);
        detailAvatarImage = findViewById(R.id.detailAvatarImage);

        // Get data from Intent
        Intent intent = getIntent();
        int avatarResId = intent.getIntExtra("SELECTED_AVATAR", R.drawable.avatar_1);
        int backgroundResId = intent.getIntExtra("SELECTED_BACKGROUND", R.drawable.background_1);

        detailAvatarBackground.setImageResource(backgroundResId);
        detailAvatarImage.setImageResource(avatarResId);

        // Initialize gender spinner
        genderSpinner = findViewById(R.id.genderSpinner);

        // Create custom adapter for spinner
        final String[] genderOptions = getResources().getStringArray(R.array.gender_options);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, genderOptions) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView textView = (TextView) view.findViewById(android.R.id.text1);

                if (position == 0) {
                    textView.setTextColor(getResources().getColor(R.color.sand));
                } else {
                    textView.setTextColor(getResources().getColor(android.R.color.white));
                }
                return view;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView textView = (TextView) view.findViewById(android.R.id.text1);

                if (position == 0) {
                    // Disable selection for the first item (the hint)
                    textView.setTextColor(getResources().getColor(R.color.sand));
                } else {
                    textView.setTextColor(getResources().getColor(android.R.color.black));
                }
                return view;
            }
        };

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderSpinner.setAdapter(adapter);
        genderSpinner.setSelection(0); // Show "Select Type" initially

        // Handle item selection
        genderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    // Hint selected, do nothing or clear selection
                } else {
                    String selectedGender = genderOptions[position];
                    // TODO: Use the selected gender here
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Optional: Handle when nothing is selected
            }
        });
    }
}
