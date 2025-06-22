package com.example.piramidnull;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

public class FindingsPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_findings_page);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ImageView arrowLeft = (ImageView) findViewById(R.id.arrowLeft);
        arrowLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FindingsPage.this, MainPage.class);
                startActivity(intent);
            }
        });

        FrameLayout frameLayout;
        frameLayout = (FrameLayout) findViewById(R.id.frame_findings);
        getSupportFragmentManager().beginTransaction().add(R.id.frame_findings, new FragmentObjects()).commit();

        Button objectsBtn = (Button) findViewById(R.id.objects_btn);
        Button notesBtn = (Button) findViewById(R.id.notes_btn);

        //selected button
        objectsBtn.setSelected(true);
        notesBtn.setSelected(false);

        if (objectsBtn.isSelected()) {
            // Button is now selected
            objectsBtn.setTextColor(Color.parseColor("#100F0F"));
            objectsBtn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#68C5E0")));
            notesBtn.setTextColor(Color.parseColor("#68C5E0"));
            notesBtn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#100F0F")));
        } else {
            // Button is now unselected
            objectsBtn.setTextColor(Color.parseColor("#68C5E0"));
            objectsBtn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#100F0F")));
            notesBtn.setTextColor(Color.parseColor("#100F0F"));
            notesBtn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#68C5E0")));
        }

        objectsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSupportFragmentManager().beginTransaction().replace(R.id.frame_findings, new FragmentObjects()).commit();
                objectsBtn.setSelected(true);
                notesBtn.setSelected(false);
                if (objectsBtn.isSelected()) {
                    // Button is now selected
                    objectsBtn.setTextColor(Color.parseColor("#100F0F"));
                    objectsBtn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#68C5E0")));
                    notesBtn.setTextColor(Color.parseColor("#68C5E0"));
                    notesBtn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#100F0F")));
                } else {
                    // Button is now unselected
                    objectsBtn.setTextColor(Color.parseColor("#68C5E0"));
                    objectsBtn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#100F0F")));
                    notesBtn.setTextColor(Color.parseColor("#100F0F"));
                    notesBtn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#68C5E0")));
                }
            }
        });

        notesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSupportFragmentManager().beginTransaction().replace(R.id.frame_findings, new FragmentNotes()).commit();
                notesBtn.setSelected(true);
                objectsBtn.setSelected(false);
                if (objectsBtn.isSelected()) {
                    // Button is now selected
                    objectsBtn.setTextColor(Color.parseColor("#100F0F"));
                    objectsBtn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#68C5E0")));
                    notesBtn.setTextColor(Color.parseColor("#68C5E0"));
                    notesBtn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#100F0F")));
                } else {
                    // Button is now unselected
                    objectsBtn.setTextColor(Color.parseColor("#68C5E0"));
                    objectsBtn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#100F0F")));
                    notesBtn.setTextColor(Color.parseColor("#100F0F"));
                    notesBtn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#68C5E0")));
                }

            }
        });
    }
}