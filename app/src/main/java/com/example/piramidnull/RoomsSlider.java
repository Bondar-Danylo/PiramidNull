package com.example.piramidnull;

import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import java.util.ArrayList;
import java.util.List;

public class RoomsSlider extends AppCompatActivity {

    LinearLayout dotsContainer;
    ImageView[] dots;
    ViewPager2 viewPager;
    RoomFragmentAdapter adapter;

    ImageView chatbotButton;
    int currentPosition = 0;

    String selectedVoiceType = "";

    private static final int ANIMATION_DURATION = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rooms_slider);

        dotsContainer = findViewById(R.id.dotsContainer);
        viewPager = findViewById(R.id.roomSlider);
        chatbotButton = findViewById(R.id.chatbotButton);

        selectedVoiceType = getIntent().getStringExtra("voiceType");

        if ("Cleopatra".equalsIgnoreCase(selectedVoiceType)) {
            chatbotButton.setImageResource(R.drawable.ic_female);
        } else if ("Pharaoh".equalsIgnoreCase(selectedVoiceType)) {
            chatbotButton.setImageResource(R.drawable.ic_male);
        } else {
            chatbotButton.setImageResource(R.drawable.guidebot);
        }

        List<Room> rooms = new ArrayList<>();
        rooms.add(new Room("The Puzzle", R.drawable.puzzle_room));
        rooms.add(new Room("The Maze", R.drawable.maze_room));
        rooms.add(new Room("The Laser", R.drawable.laser_room));

        adapter = new RoomFragmentAdapter(this, rooms);
        viewPager.setAdapter(adapter);

        viewPager.setOffscreenPageLimit(3);
        viewPager.setClipToPadding(false);
        viewPager.setClipChildren(true);
        viewPager.setPadding(-40, 0, -40, 0);
        viewPager.setPageTransformer(new ZoomOutPageTransformer());

        setupDots(rooms.size());

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                currentPosition = position;
                animateDots(position);
            }
        });

        ImageView navPuzzle = findViewById(R.id.navigation_notes);
        ImageView navLaser = findViewById(R.id.navigation_profile);
        ImageView navMaze = findViewById(R.id.navigation_bio);

        navPuzzle.setOnClickListener(v -> {
            viewPager.setCurrentItem(0, true);
            currentPosition = 0;
            animateDots(currentPosition);
        });
        navLaser.setOnClickListener(v -> {
            viewPager.setCurrentItem(1, true);
            currentPosition = 1;
            animateDots(currentPosition);
        });
        navMaze.setOnClickListener(v -> {
            viewPager.setCurrentItem(2, true);
            currentPosition = 2;
            animateDots(currentPosition);
        });

        chatbotButton.setOnClickListener(v -> openChatbot());
    }

    private void setupDots(int count) {
        dots = new ImageView[count];
        dotsContainer.removeAllViews();

        for (int i = 0; i < count; i++) {
            final int index = i;
            dots[i] = new ImageView(this);
            dots[i].setImageResource(i == 0 ? R.drawable.active_dot : R.drawable.inactive_dot);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(12, 0, 12, 0);
            dotsContainer.addView(dots[i], params);

            // Make dot clickable
            dots[i].setOnClickListener(v -> {
                viewPager.setCurrentItem(index, true);
                currentPosition = index;
                animateDots(index);
            });
        }
    }

    private void animateDots(int position) {
        for (int i = 0; i < dots.length; i++) {
            if (i == position) {
                dots[i].setImageResource(R.drawable.active_dot);
                animateDotScale(dots[i], 1.0f, 1.4f);  // Animate active dot
            } else {
                dots[i].setImageResource(R.drawable.inactive_dot);
                animateDotScale(dots[i], 1.4f, 1.0f);  // Animate inactive dot back to normal
            }
        }
    }

    private void animateDotScale(View dot, float fromScale, float toScale) {
        ScaleAnimation scaleAnimation = new ScaleAnimation(
                fromScale, toScale,
                fromScale, toScale,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f
        );
        scaleAnimation.setFillAfter(true);
        scaleAnimation.setDuration(ANIMATION_DURATION);
        dot.startAnimation(scaleAnimation);
    }

    private void openChatbot() {
        String roomType = getCurrentRoomType();

        int[] location = new int[2];
        chatbotButton.getLocationOnScreen(location);
        int botButtonX = location[0];
        int botButtonY = location[1];

        ChatbotActivity chatbotDialog = new ChatbotActivity(
                this,
                roomType,
                botButtonX,
                botButtonY,
                selectedVoiceType
        );
        chatbotDialog.show();
    }

    private String getCurrentRoomType() {
        switch (currentPosition) {
            case 0:
                return "puzzle";
            case 1:
                return "maze";
            case 2:
                return "laser";
            default:
                return "puzzle";
        }
    }
}
