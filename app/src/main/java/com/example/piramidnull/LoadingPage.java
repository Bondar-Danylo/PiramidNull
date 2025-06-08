package com.example.piramidnull;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class LoadingPage extends AppCompatActivity {

    private ImageView imageView;
    private int[] images = {
            R.drawable.loading_1,
            R.drawable.loading_3,
            R.drawable.loading_2,
    };
    private int currentIndex = 0;
    private Handler handler = new Handler();

    private Runnable imageSwitcher = new Runnable() {
        @Override
        public void run() {
            imageView.setImageResource(images[currentIndex]);
            currentIndex = (currentIndex + 1) % images.length;
            handler.postDelayed(this, 1000);
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.loading_page);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.loadingPage), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        imageView = findViewById(R.id.loadingImage);
        handler.post(imageSwitcher);

        new Handler().postDelayed(() -> {
            Intent intent = new Intent(LoadingPage.this, LandingPage.class);
            startActivity(intent);
            finish();
        }, 3000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(imageSwitcher);
    }
}