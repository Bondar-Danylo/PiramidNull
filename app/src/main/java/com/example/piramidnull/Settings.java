package com.example.piramidnull;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.ImageButton;


public class Settings extends AppCompatActivity {

     TextView titleText;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_settings);

        @SuppressLint("UseSwitchCompatOrMaterialCode") Switch musicSwitch = findViewById(R.id.musicSwitch);
        @SuppressLint("UseSwitchCompatOrMaterialCode") Switch guideVoiceSwitch = findViewById(R.id.guideVoiceSwitch);
        Spinner voiceTypeSpinner = findViewById(R.id.voiceTypeSpinner);
        Button smallTextBtn = findViewById(R.id.smallTextBtn);
        Button largeTextBtn = findViewById(R.id.largeTextBtn);
        titleText = findViewById(R.id.title);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.voice_types,
                android.R.layout.simple_spinner_item
        );

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        voiceTypeSpinner.setAdapter(adapter);

        musicSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            Toast.makeText(this, "Music: " + (isChecked ? "ON" : "OFF"), Toast.LENGTH_SHORT).show();
        });

        guideVoiceSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            Toast.makeText(this, "Guide Voice: " + (isChecked ? "ON" : "OFF"), Toast.LENGTH_SHORT).show();
        });

        smallTextBtn.setOnClickListener(v -> {
            Toast.makeText(this, "Small text selected", Toast.LENGTH_SHORT).show();
            titleText.setTextSize(12f);  // You can also apply to all views
        });

        largeTextBtn.setOnClickListener(v -> {
            Toast.makeText(this, "Large text selected", Toast.LENGTH_SHORT).show();
            titleText.setTextSize(18f);
        });
    }
}
