package com.example.piramidnull;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import java.util.ArrayList;
import java.util.List;

public class MainSliderActivity extends AppCompatActivity {

    public ViewPager2 viewPager;
    public ChallengeAdapter adapter;

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("MainSlider", "onStart called");
    }
    @Override
    protected void onResume() {
        super.onResume();
        Log.d("MainSlider", "onResume called");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("MainSlider", "onCreate started");
        setContentView(R.layout.main_slider);
        Log.d("MainSlider", "Layout set");

        viewPager = findViewById(R.id.viewPager);

        List<Challenge> challengeList = new ArrayList<>();
        challengeList.add(new Challenge("CHALLENGES", "The Maze", R.drawable.maze_room, "OPEN TOOL", 1));
        challengeList.add(new Challenge("CHALLENGES", "The Puzzle", R.drawable.puzzle_room, "SOLVE PUZZLE", 2));
        challengeList.add(new Challenge("CHALLENGES", "Laser Room", R.drawable.laser_room, "DODGE LASERS", 3));

//        Need to change Redirect! Now it leeds to wrong pages.
        adapter = new ChallengeAdapter(challengeList, challenge -> {
            switch (challenge.getDestinationId()) {
                case 1:
                    startActivity(new Intent(MainSliderActivity.this, UserAccount.class));
                    break;
                case 2:
                    startActivity(new Intent(MainSliderActivity.this, MainPage.class));
                    break;
                case 3:
                    startActivity(new Intent(MainSliderActivity.this, Main.class));
                    break;
            }
        });

        viewPager.setAdapter(adapter);
    }
}

