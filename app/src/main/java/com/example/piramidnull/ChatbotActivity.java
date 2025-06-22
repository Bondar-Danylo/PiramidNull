package com.example.piramidnull;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.speech.tts.TextToSpeech;
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
import java.util.Locale;

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

    private TextToSpeech textToSpeech;

    private final HashMap<String, List<String>> roomGuidanceMap = new HashMap<>();

    public ChatbotActivity(Context context, String roomType, int botButtonX, int botButtonY, String selectedVoiceType) {
        super(context, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        this.context = context;
        this.roomType = roomType != null ? roomType : "puzzle";
        this.botButtonX = botButtonX;
        this.botButtonY = botButtonY;
        this.selectedVoiceType = selectedVoiceType != null ? selectedVoiceType : "Pharaoh";

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

        textToSpeech = new TextToSpeech(context, status -> {
            if (status == TextToSpeech.SUCCESS) {
                int result = textToSpeech.setLanguage(Locale.US);
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Log.e(TAG, "TTS Language not supported");
                    Toast.makeText(context, "Text to speech language not supported", Toast.LENGTH_SHORT).show();
                } else {
                    Log.d(TAG, "TextToSpeech initialized");
                }
            } else {
                Log.e(TAG, "TTS Initialization failed");
                Toast.makeText(context, "Text to speech initialization failed", Toast.LENGTH_SHORT).show();
            }
        });

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
            Log.d(TAG, "Set first message: " + roomMessages.get(0).substring(0, Math.min(50, roomMessages.get(0).length())));
        } else {
            Log.w(TAG, "No messages found for room type: " + roomType);
        }

        Animation slideUp = AnimationUtils.loadAnimation(context, R.anim.slide_up);
        dialogContainer.startAnimation(slideUp);
    }

    private void setupClickListeners() {
        continueButton.setOnClickListener(v -> {
            Log.d(TAG, "Continue button clicked");
            showNextMessage();
        });

        closeButton.setOnClickListener(v -> {
            Log.d(TAG, "Close button clicked");
            dismissWithAnimation();
        });

        View.OnClickListener speakListener = v -> {
            Log.d(TAG, "Speak listener triggered");
            speakCurrentMessage();
        };

        botAvatar.setOnClickListener(speakListener);
        chatText.setOnClickListener(speakListener);
    }

    private void initializeMessages() {
        String greeting;
        if ("Cleopatra".equalsIgnoreCase(selectedVoiceType)) {
            greeting = "Greetings, young explorer! I’m Cleopatra, your guide through the ancient mysteries.";
        } else {
            greeting = "Salutations, brave adventurer! I am Pharaoh, and I shall guide you through these sacred chambers.";
        }

        List<String> puzzleMessages = new ArrayList<>();
        puzzleMessages.add(greeting);
        puzzleMessages.add("Welcome to the Puzzle Room! This room will test your logic and problem-solving skills.");
        puzzleMessages.add("Look for patterns, numbers, and hidden symbols. They often contain the key to solving puzzles.");
        puzzleMessages.add("Take your time to analyze each puzzle carefully. Rushing often leads to mistakes.");
        puzzleMessages.add("If you get stuck, try approaching the problem from a different angle or perspective.");
        puzzleMessages.add("Remember: every puzzle has a logical solution. Trust your reasoning and stay focused!");
        puzzleMessages.add("You're all set! Good luck solving the puzzles. Tap the X to close and start your challenge!");

        List<String> mazeMessages = new ArrayList<>();
        mazeMessages.add(greeting);
        mazeMessages.add("Welcome to the Maze Room! Navigation and spatial awareness are your keys to success here.");
        mazeMessages.add("Always keep track of where you've been to avoid walking in circles endlessly.");
        mazeMessages.add("Look for visual cues like arrows, colored lights, or special markings on the walls.");
        mazeMessages.add("Some walls might be illusions or secret passages. Don't be afraid to test boundaries!");
        mazeMessages.add("Stay calm and move methodically. Panic and rushing lead to poor decisions in mazes.");
        mazeMessages.add("You're ready to navigate! Remember to stay focused. Tap the X to close and enter the maze!");

        List<String> laserMessages = new ArrayList<>();
        laserMessages.add(greeting);
        laserMessages.add("Welcome to the Laser Room! Precision, timing, and quick reflexes are everything here.");
        laserMessages.add("Observe the laser patterns carefully before making any moves. Each pattern has a rhythm.");
        laserMessages.add("Look for mirrors, crystals, or reflective objects that might redirect laser beams.");
        laserMessages.add("Many lasers have predictable on/off cycles. Study the timing before you move.");
        laserMessages.add("Move slowly and deliberately. One wrong step could trigger alarms or reset your progress!");
        laserMessages.add("You're prepared for the laser challenge! Stay sharp and move carefully. Tap X to begin!");

        roomGuidanceMap.put("puzzle", puzzleMessages);
        roomGuidanceMap.put("maze", mazeMessages);
        roomGuidanceMap.put("laser", laserMessages);
    }

    private void showFirstMessage() {
        currentMessageIndex = 0;
        if (roomMessages != null && !roomMessages.isEmpty()) {
            chatText.setText(roomMessages.get(0));
            new Handler(Looper.getMainLooper()).postDelayed(this::speakCurrentMessage, 200);
        } else {
            Log.e(TAG, "No room messages available for first message");
        }
    }

    private void showNextMessage() {
        if (roomMessages == null || currentMessageIndex >= roomMessages.size() - 1) {
            closeButton.performClick();
            return;
        }

        if (textToSpeech != null && textToSpeech.isSpeaking()) {
            textToSpeech.stop();
        }

        currentMessageIndex++;

        Animation fadeOut = AnimationUtils.loadAnimation(context, android.R.anim.fade_out);
        fadeOut.setDuration(200);
        fadeOut.setAnimationListener(new Animation.AnimationListener() {
            @Override public void onAnimationStart(Animation animation) {}

            @Override public void onAnimationEnd(Animation animation) {
                String newMessage = roomMessages.get(currentMessageIndex);
                chatText.setText(newMessage);
                Log.d(TAG, "Set new message: " + newMessage.substring(0, Math.min(50, newMessage.length())));

                Animation fadeIn = AnimationUtils.loadAnimation(context, android.R.anim.fade_in);
                fadeIn.setDuration(300);
                chatText.startAnimation(fadeIn);

                new Handler(Looper.getMainLooper()).postDelayed(() -> speakCurrentMessage(), 300);

                if (currentMessageIndex == roomMessages.size() - 1) {
                    continueButton.setText("Start Game ▶");
                }
            }

            @Override public void onAnimationRepeat(Animation animation) {}
        });

        chatText.startAnimation(fadeOut);
    }

    private void speakCurrentMessage() {
        String message = roomMessages.get(currentMessageIndex);
        if (textToSpeech != null) {
            int speakResult = textToSpeech.speak(message, TextToSpeech.QUEUE_FLUSH, null, "chatbotMessage");
            if (speakResult == TextToSpeech.ERROR) {
                Log.e(TAG, "Error speaking text");
                Toast.makeText(context, "Error with text to speech", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(context, "Voice service not available", Toast.LENGTH_SHORT).show();
        }
    }

    private void dismissWithAnimation() {
        if (textToSpeech != null && textToSpeech.isSpeaking()) {
            textToSpeech.stop();
        }

        Animation slideDown = AnimationUtils.loadAnimation(context, R.anim.slide_down);
        slideDown.setAnimationListener(new Animation.AnimationListener() {
            @Override public void onAnimationStart(Animation animation) {}

            @Override public void onAnimationEnd(Animation animation) {
                dismiss();
            }
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
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
            textToSpeech = null;
        }
        super.dismiss();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (textToSpeech != null && textToSpeech.isSpeaking()) {
            textToSpeech.stop();
        }
    }
}
