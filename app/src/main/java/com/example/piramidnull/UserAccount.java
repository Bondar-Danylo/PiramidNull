package com.example.piramidnull;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.imageview.ShapeableImageView;

public class UserAccount extends AppCompatActivity {

    private FrameLayout avatarOverlay, overlayContentContainer;
    private ShapeableImageView avatarImage;
    private ImageView avatarBackground, btnPrevious;
    private GridLayout avatarGrid, backgroundGrid;
    private LinearLayout avatarStepLayout, backgroundStepLayout;
    private Button btnCharacter, btnBackground, skipButton;
    private ImageButton btnNext, backIcon;

    private final int[] avatarIds = {
            R.drawable.avatar_1, R.drawable.avatar_2, R.drawable.avatar_3,
            R.drawable.avatar_4, R.drawable.avatar_5, R.drawable.avatar_6
    };

    private final int[] backgroundIds = {
            R.drawable.background_1, R.drawable.background_2, R.drawable.background_3,
            R.drawable.background_4, R.drawable.background_5, R.drawable.background_6
    };

    private int selectedAvatarResId = R.drawable.avatar_4;
    private int selectedBackgroundResId = R.drawable.background_1;
    private boolean isAvatarStep = true;

    private ImageButton selectedAvatarButton = null;
    private ImageButton selectedBackgroundButton = null;

    private static final String TAG = "UserAccount";
    private VoiceManager voiceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.landing);

        voiceManager = VoiceManager.getInstance(this);

        if (!voiceManager.isInitialized()) {
            Log.e(TAG, "VoiceManager failed to initialize - TTS features may not work");
            Toast.makeText(this, "Voice features may not be available", Toast.LENGTH_SHORT).show();
        }

        avatarOverlay = findViewById(R.id.avatarOverlay);
        overlayContentContainer = avatarOverlay.findViewById(R.id.overlayContentContainer);

        ViewCompat.setOnApplyWindowInsetsListener(avatarOverlay, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        inflateOverlayContent(R.layout.createaccount);
    }

    private void inflateOverlayContent(int layoutResId) {
        overlayContentContainer.removeAllViews();
        View inflatedView = getLayoutInflater().inflate(layoutResId, overlayContentContainer, false);
        overlayContentContainer.addView(inflatedView);

        if (layoutResId == R.layout.createaccount) {
            bindCreateAccountViews(inflatedView);
            setupCreateAccountListeners();
            loadGridItems(avatarGrid, avatarIds, true);
            loadGridItems(backgroundGrid, backgroundIds, false);
            showStep(isAvatarStep);
        } else if (layoutResId == R.layout.fragment_create_account) {
            bindDetailsViews(inflatedView);
            setupBackIconForDetails(inflatedView);
            avatarOverlay.setVisibility(View.VISIBLE);
        }
    }

    private void bindCreateAccountViews(View root) {
        avatarImage = root.findViewById(R.id.avatarImage);
        avatarBackground = root.findViewById(R.id.avatarBackground);
        avatarGrid = root.findViewById(R.id.avatarGrid);
        backgroundGrid = root.findViewById(R.id.backgroundGrid);
        avatarStepLayout = root.findViewById(R.id.avatarStepLayout);
        backgroundStepLayout = root.findViewById(R.id.backgroundStepLayout);
        btnCharacter = root.findViewById(R.id.btn_character);
        btnBackground = root.findViewById(R.id.btn_background);
        skipButton = root.findViewById(R.id.btn_skip);
        btnPrevious = root.findViewById(R.id.btn_previous);
        btnNext = root.findViewById(R.id.btn_next);
        backIcon = root.findViewById(R.id.back_icon);

        avatarImage.setImageResource(selectedAvatarResId);
        avatarBackground.setImageResource(selectedBackgroundResId);
    }

    private void bindDetailsViews(View root) {
        ImageView detailAvatarBackground = root.findViewById(R.id.detailAvatarBackground);
        ImageView detailAvatarImage = root.findViewById(R.id.detailAvatarImage);

        if (detailAvatarBackground != null)
            detailAvatarBackground.setImageResource(selectedBackgroundResId);
        if (detailAvatarImage != null)
            detailAvatarImage.setImageResource(selectedAvatarResId);

        setupVoiceSpinner(root);
        setupCreateAccountButton(root);
    }

    private void setupVoiceSpinner(View root) {
        Spinner genderSpinner = root.findViewById(R.id.genderSpinner);
        String[] voiceLabels = getResources().getStringArray(R.array.voice_types);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_item, voiceLabels);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        genderSpinner.setAdapter(adapter);

        String currentVoice = voiceManager.getCurrentVoiceType();
        genderSpinner.setSelection("Arthur".equals(currentVoice) ? 2 : 1);

        genderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                if (pos == 0) return;
                String label = (String) parent.getItemAtPosition(pos);
                String voiceId = label.equals("Pharaoh") ? "Arthur" : "Emma";
                voiceManager.setVoice(voiceId);
                voiceManager.speak("Hello! I am " + label + ", your voice assistant.");
            }

            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void setupCreateAccountButton(View root) {
        Button createAccountButton = root.findViewById(R.id.createaccount_button);
        createAccountButton.setOnClickListener(v -> {
            String username = ((EditText) root.findViewById(R.id.username_input)).getText().toString().trim();
            String password = ((EditText) root.findViewById(R.id.password_input)).getText().toString().trim();
            String email = ((EditText) root.findViewById(R.id.email_input)).getText().toString().trim();

            if (validateInputs(username, password, email)) {
                saveUserAccount(username, password, email);
                Toast.makeText(this, "Your account created successfully!", Toast.LENGTH_SHORT).show();
                voiceManager.speak("Welcome " + username + "! Your account has been created successfully.");
            }
        });
    }

    private void setupBackIconForDetails(View root) {
        ImageButton detailsBackIcon = root.findViewById(R.id.back_icon);
        if (detailsBackIcon != null) {
            detailsBackIcon.setOnClickListener(v -> {
                stopVoice();
                isAvatarStep = false;
                inflateOverlayContent(R.layout.createaccount);
            });
        }
    }

    private void setupCreateAccountListeners() {
        btnCharacter.setOnClickListener(v -> {
            isAvatarStep = true;
            showStep(true);
        });

        btnBackground.setOnClickListener(v -> {
            isAvatarStep = false;
            showStep(false);
        });

        btnNext.setOnClickListener(v -> {
            if (isAvatarStep) {
                isAvatarStep = false;
                showStep(false);
            } else {
                navigateToDetails();
            }
        });

        btnPrevious.setOnClickListener(v -> {
            isAvatarStep = true;
            showStep(true);
        });

        backIcon.setOnClickListener(v -> {
            stopVoice();
            finish();
        });

        skipButton.setOnClickListener(v -> navigateToDetails());

        avatarOverlay.setOnClickListener(v -> {
            stopVoice();
            avatarOverlay.setVisibility(View.GONE);
        });
    }

    private void navigateToDetails() {
        avatarOverlay.setVisibility(View.GONE);
        CreateAccountFragment fragment = new CreateAccountFragment();

        Bundle bundle = new Bundle();
        bundle.putInt("selected_avatar", selectedAvatarResId);
        bundle.putInt("selected_background", selectedBackgroundResId);
        bundle.putString("voice_type", voiceManager.getCurrentVoiceType());
        fragment.setArguments(bundle);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.DetailsFragment, fragment)
                .addToBackStack(null)
                .commit();
    }
    private void showStep(boolean isCharacterStep) {
        avatarStepLayout.setVisibility(isCharacterStep ? View.VISIBLE : View.GONE);
        backgroundStepLayout.setVisibility(isCharacterStep ? View.GONE : View.VISIBLE);

        btnCharacter.setBackgroundResource(isCharacterStep ? R.drawable.btn_selected : R.drawable.btn_unselected);
        btnBackground.setBackgroundResource(!isCharacterStep ? R.drawable.btn_selected : R.drawable.btn_unselected);
        btnCharacter.setTextColor(getResources().getColor(isCharacterStep ? R.color.black : R.color.sand));
        btnBackground.setTextColor(getResources().getColor(!isCharacterStep ? R.color.black : R.color.sand));

        skipButton.setVisibility(isCharacterStep ? View.VISIBLE : View.GONE);
        btnPrevious.setVisibility(!isCharacterStep ? View.VISIBLE : View.GONE);
    }

    private void loadGridItems(GridLayout grid, int[] drawableIds, boolean isAvatarGrid) {
        if (grid == null) return;
        grid.removeAllViews();

        for (int drawableId : drawableIds) {
            ImageButton imgButton = new ImageButton(this);
            imgButton.setImageResource(drawableId);
            imgButton.setScaleType(ImageView.ScaleType.CENTER);
            imgButton.setBackgroundResource(R.drawable.square_avatar);

            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = 300;
            params.height = 300;
            params.setMargins(16, 16, 16, 16);
            imgButton.setLayoutParams(params);

            if (isAvatarGrid && drawableId == selectedAvatarResId) {
                imgButton.setBackgroundResource(R.drawable.selected_frame);
                selectedAvatarButton = imgButton;
            } else if (!isAvatarGrid && drawableId == selectedBackgroundResId) {
                imgButton.setBackgroundResource(R.drawable.selected_frame);
                selectedBackgroundButton = imgButton;
            }

            imgButton.setOnClickListener(v -> {
                if (isAvatarGrid) {
                    updateSelection(imgButton, true, drawableId);
                } else {
                    updateSelection(imgButton, false, drawableId);
                }
            });

            grid.addView(imgButton);
        }
    }

    private void updateSelection(ImageButton button, boolean isAvatar, int drawableId) {
        if (isAvatar) {
            if (selectedAvatarButton != null)
                selectedAvatarButton.setBackgroundResource(R.drawable.square_avatar);
            button.setBackgroundResource(R.drawable.selected_frame);
            selectedAvatarButton = button;
            avatarImage.setImageResource(drawableId);
            selectedAvatarResId = drawableId;
        } else {
            if (selectedBackgroundButton != null)
                selectedBackgroundButton.setBackgroundResource(R.drawable.square_avatar);
            button.setBackgroundResource(R.drawable.selected_frame);
            selectedBackgroundButton = button;
            avatarBackground.setImageResource(drawableId);
            selectedBackgroundResId = drawableId;
        }
    }

    private boolean validateInputs(String username, String password, String email) {
        if (username.isEmpty()) {
            showError("Please enter a username");
            return false;
        }
        if (password.isEmpty()) {
            showError("Please enter a password");
            return false;
        }
        if (password.length() < 6) {
            showError("Password must be at least 6 characters long");
            return false;
        }
        if (email.isEmpty()) {
            showError("Please enter an email address");
            return false;
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showError("Please enter a valid email address");
            return false;
        }
        return true;
    }

    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        voiceManager.speak(message);
    }

    private void saveUserAccount(String username, String password, String email) {
        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putString("username", username);
        editor.putString("password", password);
        editor.putString("email", email);
        editor.putInt("selected_avatar", selectedAvatarResId);
        editor.putInt("selected_background", selectedBackgroundResId);
        editor.putString("voice_id", voiceManager.getCurrentVoiceType());
        editor.putBoolean("account_created", true);
        editor.apply();
    }

    private void stopVoice() {
        if (voiceManager != null) voiceManager.stop();
    }

    @Override protected void onPause() {
        super.onPause();
        stopVoice();
    }

    @Override protected void onDestroy() {
        stopVoice();
        super.onDestroy();
    }

    @Override public void onBackPressed() {
        stopVoice();
        super.onBackPressed();
    }

    public VoiceManager getVoiceManager() {
        return voiceManager;
    }

    public int getSelectedAvatarResId() {
        return selectedAvatarResId;
    }

    public int getSelectedBackgroundResId() {
        return selectedBackgroundResId;
    }

    public void showBackgroundStep() {
        isAvatarStep = false;
        inflateOverlayContent(R.layout.createaccount);
    }
    public static final String VOICE_TYPE_CLEOPATRA = "Cleopatra";
    public static final String VOICE_TYPE_PHARAOH = "Pharaoh";
    public static String getVoiceIdFromType(String voiceType) {
        if (voiceType == null) return "en-us-x-iol-local"; // fallback default
        switch (voiceType) {
            case VOICE_TYPE_PHARAOH:
                return "en-us-x-iol-local"; // Male voice ID
            case VOICE_TYPE_CLEOPATRA:
                return "en-us-x-iob-local"; // Female voice ID
            default:
                return "en-us-x-iol-local"; // Default to male voice if unknown
        }
    }
}

