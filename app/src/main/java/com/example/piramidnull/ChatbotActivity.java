package com.example.piramidnull;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ChatbotActivity extends Dialog {

    private static final String TAG = "ChatbotActivity";

    private TextView chatText, continueButton;
    private ImageView closeButton, botAvatar;
    private View dialogContainer;

    private final Context context;
    private final String roomType;
    private final String selectedVoiceType;
    private List<String> roomMessages;
    private int currentMessageIndex = 0;

    private final int botButtonX;
    private final int botButtonY;

    public final GoogleTTSHelper googleTTSHelper;

    private final HashMap<String, List<String>> roomGuidanceMap = new HashMap<>();

    public ChatbotActivity(Context context, String roomType, int botButtonX, int botButtonY, String selectedVoiceType) {
        super(context, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        this.context = context;
        this.roomType = roomType != null ? roomType : "puzzle";
        this.botButtonX = botButtonX;
        this.botButtonY = botButtonY;
        this.selectedVoiceType = selectedVoiceType != null ? selectedVoiceType : "Pharaoh";

        this.googleTTSHelper = new GoogleTTSHelper(context, "en-GB");

        initializeMessages();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.chatbot);

        Window window = getWindow();
        if (window != null) {
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.MATCH_PARENT);
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }

        initializeViews();
        setupClickListeners();
        showFirstMessage();
        positionDialogRelativeToAvatar();
    }

    private void initializeViews() {
        dialogContainer = findViewById(R.id.dialogContainer);
        chatText = findViewById(R.id.chatText);
        continueButton = findViewById(R.id.continueButton);
        closeButton = findViewById(R.id.closeButton);
        botAvatar = findViewById(R.id.botAvatar);

        roomMessages = roomGuidanceMap.get(roomType);
        if (roomMessages != null && !roomMessages.isEmpty()) {
            chatText.setText(roomMessages.get(0));
        }
        Animation slideUp = AnimationUtils.loadAnimation(context, R.anim.slide_up);
        dialogContainer.startAnimation(slideUp);
    }

    private void setupClickListeners() {
        continueButton.setOnClickListener(v -> {
            showNextMessage();
        });

        closeButton.setOnClickListener(v -> {
            dismissWithAnimation();
        });

        View.OnClickListener speakListener = v -> {
            speakCurrentMessage();
        };

        botAvatar.setOnClickListener(speakListener);
        chatText.setOnClickListener(speakListener);
    }

    // Check SharedPreferences if greeting was already spoken
    private boolean isGreetingSpoken() {
        SharedPreferences prefs = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        return prefs.getBoolean("greeting_spoken", false);
    }

    private void setGreetingSpokenFlag() {
        SharedPreferences prefs = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        prefs.edit().putBoolean("greeting_spoken", true).apply();
    }

    private void initializeMessages() {
        boolean greetingDone = isGreetingSpoken();

        String greeting = "Cleopatra".equalsIgnoreCase(selectedVoiceType) ?
                "Greetings, young explorer! I’m Cleopatra, your guide through the ancient mysteries." :
                "Salutations, brave adventurer! I am Pharaoh, and I shall guide you through these sacred chambers.";

        List<String> puzzleMessages;
        List<String> mazeMessages;
        List<String> laserMessages;

        if (!greetingDone) {
            puzzleMessages = new ArrayList<>(List.of(
                    greeting,
                    "Welcome to the Puzzle Room! This room will test your logic and problem-solving skills.",
                    "Look for patterns, numbers, and hidden symbols. They often contain the key to solving puzzles.",
                    "Take your time to analyze each puzzle carefully. Rushing often leads to mistakes.",
                    "If you get stuck, try approaching the problem from a different angle or perspective.",
                    "Remember: every puzzle has a logical solution. Trust your reasoning and stay focused!",
                    "You're all set! Good luck solving the puzzles. Tap the X to close and start your challenge!"
            ));
            mazeMessages = new ArrayList<>(List.of(
                    greeting,
                    "Welcome to the Maze Room! Navigation and spatial awareness are your keys to success here.",
                    "Always keep track of where you've been to avoid walking in circles endlessly.",
                    "Look for visual cues like arrows, colored lights, or special markings on the walls.",
                    "Some walls might be illusions or secret passages. Don't be afraid to test boundaries!",
                    "Stay calm and move methodically. Panic and rushing lead to poor decisions in mazes.",
                    "You're ready to navigate! Remember to stay focused. Tap the X to close and enter the maze!"
            ));
            laserMessages = new ArrayList<>(List.of(
                    greeting,
                    "Welcome to the Laser Room! Precision, timing, and quick reflexes are everything here.",
                    "Observe the laser patterns carefully before making any moves. Each pattern has a rhythm.",
                    "Look for mirrors, crystals, or reflective objects that might redirect laser beams.",
                    "Many lasers have predictable on/off cycles. Study the timing before you move.",
                    "Move slowly and deliberately. One wrong step could trigger alarms or reset your progress!",
                    "You're prepared for the laser challenge! Stay sharp and move carefully. Tap X to begin!"
            ));
        } else {
            puzzleMessages = new ArrayList<>(List.of(
                    "Welcome to the Puzzle Room! This room will test your logic and problem-solving skills.",
                    "Look for patterns, numbers, and hidden symbols. They often contain the key to solving puzzles.",
                    "Take your time to analyze each puzzle carefully. Rushing often leads to mistakes.",
                    "If you get stuck, try approaching the problem from a different angle or perspective.",
                    "Remember: every puzzle has a logical solution. Trust your reasoning and stay focused!",
                    "You're all set! Good luck solving the puzzles. Tap the X to close and start your challenge!"
            ));
            mazeMessages = new ArrayList<>(List.of(
                    "Welcome to the Maze Room! Navigation and spatial awareness are your keys to success here.",
                    "Always keep track of where you've been to avoid walking in circles endlessly.",
                    "Look for visual cues like arrows, colored lights, or special markings on the walls.",
                    "Some walls might be illusions or secret passages. Don't be afraid to test boundaries!",
                    "Stay calm and move methodically. Panic and rushing lead to poor decisions in mazes.",
                    "You're ready to navigate! Remember to stay focused. Tap the X to close and enter the maze!"
            ));
            laserMessages = new ArrayList<>(List.of(
                    "Welcome to the Laser Room! Precision, timing, and quick reflexes are everything here.",
                    "Observe the laser patterns carefully before making any moves. Each pattern has a rhythm.",
                    "Look for mirrors, crystals, or reflective objects that might redirect laser beams.",
                    "Many lasers have predictable on/off cycles. Study the timing before you move.",
                    "Move slowly and deliberately. One wrong step could trigger alarms or reset your progress!",
                    "You're prepared for the laser challenge! Stay sharp and move carefully. Tap X to begin!"
            ));
        }

        roomGuidanceMap.put("puzzle", puzzleMessages);
        roomGuidanceMap.put("maze", mazeMessages);
        roomGuidanceMap.put("laser", laserMessages);
    }

    private void showFirstMessage() {
        currentMessageIndex = 0;
        if (roomMessages != null && !roomMessages.isEmpty()) {
            chatText.setText(roomMessages.get(0));
        }
    }

    private void showNextMessage() {
        if (roomMessages == null || currentMessageIndex >= roomMessages.size() - 1) {
            closeButton.performClick();
            return;
        }

        currentMessageIndex++;
        Animation fadeOut = AnimationUtils.loadAnimation(context, android.R.anim.fade_out);
        fadeOut.setDuration(200);
        fadeOut.setAnimationListener(new Animation.AnimationListener() {
            @Override public void onAnimationStart(Animation animation) {}

            @Override public void onAnimationEnd(Animation animation) {
                String newMessage = roomMessages.get(currentMessageIndex);
                chatText.setText(newMessage);
                Animation fadeIn = AnimationUtils.loadAnimation(context, android.R.anim.fade_in);
                fadeIn.setDuration(300);
                chatText.startAnimation(fadeIn);
                new Handler(Looper.getMainLooper()).postDelayed(ChatbotActivity.this::speakCurrentMessage, 100);

                if (currentMessageIndex == roomMessages.size() - 1) {
                    continueButton.setText("Start Game ▶");
                } else {
                    continueButton.setText("Continue ▶");
                }
            }

            @Override public void onAnimationRepeat(Animation animation) {}
        });
        chatText.startAnimation(fadeOut);
    }

    private void speakCurrentMessage() {
        if (roomMessages == null || currentMessageIndex >= roomMessages.size()) return;

        String message = roomMessages.get(currentMessageIndex);
        googleTTSHelper.synthesizeSpeech(message, selectedVoiceType, new GoogleTTSHelper.AudioReadyCallback() {
            @Override
            public void onAudioReady(byte[] audioData) {
                Log.d(TAG, "Audio playback started");
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(context, "TTS Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e(TAG, "TTS failed", e);
            }

            @Override
            public void onPlaybackStarted() {
                Log.d(TAG, "Playback started");
            }

            @Override
            public void onPlaybackCompleted() {
                Log.d(TAG, "Playback completed");
                // Mark greeting as spoken after it finishes playing
                if (currentMessageIndex == 0 && !isGreetingSpoken()) {
                    setGreetingSpokenFlag();
                }
            }
        });
    }

    private void dismissWithAnimation() {
        googleTTSHelper.stopPlayback();
        Animation slideDown = AnimationUtils.loadAnimation(context, R.anim.slide_down);
        slideDown.setAnimationListener(new Animation.AnimationListener() {
            @Override public void onAnimationStart(Animation animation) {}
            @Override public void onAnimationEnd(Animation animation) { dismiss(); }
            @Override public void onAnimationRepeat(Animation animation) {}
        });
        dialogContainer.startAnimation(slideDown);
    }

    private void positionDialogRelativeToAvatar() {
        dialogContainer.post(() -> {
            int dialogWidth = dialogContainer.getWidth();
            int dialogHeight = dialogContainer.getHeight();
            float density = context.getResources().getDisplayMetrics().density;

            float shiftLeftPx = 110 * density;
            float posX = botButtonX + (botAvatar.getWidth() / 2f) - (dialogWidth / 2f) - shiftLeftPx;

            int marginPx = (int) (-70 * density);
            float posY = botButtonY - dialogHeight - marginPx;

            dialogContainer.setX(posX);
            dialogContainer.setY(posY);
        });
    }

    @Override
    public void dismiss() {
        googleTTSHelper.release();
        super.dismiss();
    }

    @Override
    protected void onStop() {
        super.onStop();
        googleTTSHelper.stopPlayback();
    }
}
