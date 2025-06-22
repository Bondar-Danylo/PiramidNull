package com.example.piramidnull;

import android.os.Bundle;
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

    // Receive voice type from Intent
    String selectedVoiceType = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rooms_slider);

        dotsContainer = findViewById(R.id.dotsContainer);
        viewPager = findViewById(R.id.roomSlider);
        chatbotButton = findViewById(R.id.chatbotButton);  // Initialize here after setContentView

        // Get voice type from intent and assign to class variable
        selectedVoiceType = getIntent().getStringExtra("voiceType");

        // Set chatbotButton icon based on selected voice type
        if ("Cleopatra".equalsIgnoreCase(selectedVoiceType)) {
            chatbotButton.setImageResource(R.drawable.ic_female);
        } else if ("Pharaoh".equalsIgnoreCase(selectedVoiceType)) {
            chatbotButton.setImageResource(R.drawable.ic_male);
        } else {
            chatbotButton.setImageResource(R.drawable.guidebot);  // Default icon
        }

        // Prepare rooms list
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

        addDotsIndicator(0);
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                currentPosition = position;
                addDotsIndicator(position);
            }
        });

        ImageView navPuzzle = findViewById(R.id.navigation_notes);
        ImageView navLaser = findViewById(R.id.navigation_profile);
        ImageView navMaze = findViewById(R.id.navigation_bio);

        navPuzzle.setOnClickListener(v -> {
            viewPager.setCurrentItem(0, true);
            currentPosition = 0;
        });
        navLaser.setOnClickListener(v -> {
            viewPager.setCurrentItem(1, true);
            currentPosition = 1;
        });
        navMaze.setOnClickListener(v -> {
            viewPager.setCurrentItem(2, true);
            currentPosition = 2;
        });

        chatbotButton.setOnClickListener(v -> openChatbot());
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

    private void addDotsIndicator(int position) {
        dotsContainer.removeAllViews();
        dots = new ImageView[3];

        for (int i = 0; i < dots.length; i++) {
            dots[i] = new ImageView(this);
            dots[i].setImageResource(i == position ? R.drawable.active_dot : R.drawable.inactive_dot);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(-4, 0, -4, 0);
            dotsContainer.addView(dots[i], params);
        }
    }
}
