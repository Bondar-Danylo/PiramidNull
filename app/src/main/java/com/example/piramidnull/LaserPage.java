package com.example.piramidnull;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.tbuonomo.viewpagerdotsindicator.WormDotsIndicator;

public class LaserPage extends AppCompatActivity {

    ImageView arrowLeft;
    ViewPager2 viewPager2;
    LaserAdapter laserAdapter;
    WormDotsIndicator dotsIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_laser_page);

        arrowLeft = findViewById(R.id.arrowLeft);

        arrowLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LaserPage.this, MainPage.class);
                startActivity(intent);
            }
        });

        viewPager2 = findViewById(R.id.frame_laserRoom);
        int numberOfTips = 3;
        laserAdapter = new LaserAdapter(this, numberOfTips);
        viewPager2.setAdapter(laserAdapter);

//        Fragment fragment = FragmentLaserPage.newInstance(0);
//        getSupportFragmentManager()
//                .beginTransaction()
//                .replace(R.id.frame_laserRoom, fragment)
//                .commit();

        dotsIndicator = findViewById(R.id.dots_indicator);
        dotsIndicator.setViewPager2(viewPager2);

    }
}