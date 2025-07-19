package com.example.piramidnull;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ObjectPage extends AppCompatActivity {
    ImageView arrowLeft;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_object_page);



        ImageView objectImage = findViewById(R.id.object_image);
        TextView objectName = findViewById(R.id.object_name);
        TextView objectStory = findViewById(R.id.object_story);
        TextView objectFound = findViewById(R.id.object_found);
        TextView objectOrigin = findViewById(R.id.object_origin);
        TextView objectDate = findViewById(R.id.object_date);
        TextView objectFunction = findViewById(R.id.object_function);

        // getting the data that we need to fill into the views
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String name = extras.getString("object_name");
            String number = extras.getString("object_number");
            String material = extras.getString("object_material");
            String size = extras.getString("object_size");
            int imageResId = extras.getInt("object_image");
            String story = extras.getString("object_story");
            String found = extras.getString("object_found");
            String origin = extras.getString("object_origin");
            String date = extras.getString("object_date");
            String function = extras.getString("object_function");

            //after getting it we use it/place it
            objectName.setText(name);
            objectImage.setImageResource(imageResId);
            objectStory.setText(story);
            objectFound.setText(found);
            objectOrigin.setText(origin);
            objectDate.setText(date);
            objectFunction.setText(function);

        }

        arrowLeft = findViewById(R.id.arrowLeft);

        arrowLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ObjectPage.this, FindingsPage.class);
                startActivity(intent);
            }
        });
    }
}