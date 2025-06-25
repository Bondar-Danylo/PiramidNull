package com.example.piramidnull;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.imageview.ShapeableImageView;

public class EditAvatar extends AppCompatActivity {

    private ShapeableImageView avatarImage;
    private ShapeableImageView avatarBackground;
    private GridLayout avatarGrid, backgroundGrid;
    private LinearLayout avatarStepLayout, backgroundStepLayout;
    private Button btnCharacter, btnBackground, btnSaveChanges;
    private ImageButton btnNext, btnPrevious, backIcon;

    private final int[] avatarIds = {
            R.drawable.avatar_1, R.drawable.avatar_2, R.drawable.avatar_3,
            R.drawable.avatar_4, R.drawable.avatar_5, R.drawable.avatar_6
    };

    private final int[] backgroundIds = {
            R.drawable.background_1, R.drawable.background_2, R.drawable.background_3,
            R.drawable.background_4, R.drawable.background_5, R.drawable.background_6
    };

    // Current selections
    private int selectedAvatarResId;
    private int selectedBackgroundResId;
    private boolean isAvatarStep = true;
    private boolean hasUnsavedChanges = false;

    // Selection tracking
    private ImageButton selectedAvatarButton = null;
    private ImageButton selectedBackgroundButton = null;

    private static final String TAG = "EditAvatarActivity";
    private VoiceManager voiceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_avatar);

        // Initialize voice manager
        voiceManager = VoiceManager.getInstance(this);
        if (!voiceManager.isInitialized()) {
            Log.e(TAG, "VoiceManager failed to initialize");
        }

        // Apply window insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initializeViews();
        loadCurrentUserData();
        setupEventListeners();
        loadGridItems(avatarGrid, avatarIds, true);
        loadGridItems(backgroundGrid, backgroundIds, false);
        showStep(isAvatarStep);
    }

    private void initializeViews() {
        avatarImage = findViewById(R.id.avatarImage);
        avatarBackground = findViewById(R.id.avatarBackground);
        avatarGrid = findViewById(R.id.avatarGrid);
        backgroundGrid = findViewById(R.id.backgroundGrid);
        avatarStepLayout = findViewById(R.id.avatarStepLayout);
        backgroundStepLayout = findViewById(R.id.backgroundStepLayout);
        btnCharacter = findViewById(R.id.btn_character);
        btnBackground = findViewById(R.id.btn_background);
        btnSaveChanges = findViewById(R.id.btn_save_changes);
        btnNext = findViewById(R.id.btn_next);
        btnPrevious = findViewById(R.id.btn_previous);
        backIcon = findViewById(R.id.back_icon);
    }

    private void loadCurrentUserData() {
        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);

        // Load current avatar and background, with defaults if not found
        selectedAvatarResId = prefs.getInt("selected_avatar", R.drawable.avatar_1);
        selectedBackgroundResId = prefs.getInt("selected_background", R.drawable.background_1);

        // Update preview images
        avatarImage.setImageResource(selectedAvatarResId);
        avatarBackground.setImageResource(selectedBackgroundResId);
    }

    private void setupEventListeners() {
        // Step navigation buttons
        btnCharacter.setOnClickListener(v -> {
            if (!isAvatarStep) {
                isAvatarStep = true;
                showStep(true);
                voiceManager.speak("Character selection");
            }
        });

        btnBackground.setOnClickListener(v -> {
            if (isAvatarStep) {
                isAvatarStep = false;
                showStep(false);
                voiceManager.speak("Background selection");
            }
        });

        // Navigation arrows
        btnNext.setOnClickListener(v -> {
            if (isAvatarStep) {
                isAvatarStep = false;
                showStep(false);
                voiceManager.speak("Background selection");
            }
        });

        btnPrevious.setOnClickListener(v -> {
            if (!isAvatarStep) {
                isAvatarStep = true;
                showStep(true);
                voiceManager.speak("Character selection");
            }
        });

        // Save changes button
        btnSaveChanges.setOnClickListener(v -> saveChanges());

        // Back button
        backIcon.setOnClickListener(v -> {
            if (hasUnsavedChanges) {
                showUnsavedChangesDialog();
            } else {
                finish();
            }
        });
    }

    private void showStep(boolean isCharacterStep) {
        // Toggle visibility of step layouts
        avatarStepLayout.setVisibility(isCharacterStep ? View.VISIBLE : View.GONE);
        backgroundStepLayout.setVisibility(isCharacterStep ? View.GONE : View.VISIBLE);

        // Update button appearances
        btnCharacter.setBackgroundResource(isCharacterStep ? R.drawable.btn_selected : R.drawable.btn_unselected);
        btnBackground.setBackgroundResource(!isCharacterStep ? R.drawable.btn_selected : R.drawable.btn_unselected);
        btnCharacter.setTextColor(getResources().getColor(isCharacterStep ? R.color.black : R.color.sand, null));
        btnBackground.setTextColor(getResources().getColor(!isCharacterStep ? R.color.black : R.color.sand, null));

        // Show/hide navigation buttons
        btnPrevious.setVisibility(!isCharacterStep ? View.VISIBLE : View.GONE);
        btnNext.setVisibility(isCharacterStep ? View.VISIBLE : View.GONE);
    }

    private void loadGridItems(GridLayout grid, int[] drawableIds, boolean isAvatarGrid) {
        if (grid == null) return;
        grid.removeAllViews();

        for (int drawableId : drawableIds) {
            ImageButton imgButton = new ImageButton(this);
            imgButton.setImageResource(drawableId);
            imgButton.setScaleType(ImageView.ScaleType.CENTER);
            imgButton.setBackgroundResource(R.drawable.square_avatar);

            // Set grid layout parameters
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = 280;
            params.height = 280;
            params.setMargins(12, 12, 12, 12);
            imgButton.setLayoutParams(params);

            // Highlight currently selected item
            if (isAvatarGrid && drawableId == selectedAvatarResId) {
                imgButton.setBackgroundResource(R.drawable.selected_frame);
                selectedAvatarButton = imgButton;
            } else if (!isAvatarGrid && drawableId == selectedBackgroundResId) {
                imgButton.setBackgroundResource(R.drawable.selected_frame);
                selectedBackgroundButton = imgButton;
            }

            // Set click listener
            imgButton.setOnClickListener(v -> {
                if (isAvatarGrid) {
                    updateAvatarSelection(imgButton, drawableId);
                } else {
                    updateBackgroundSelection(imgButton, drawableId);
                }
            });

            grid.addView(imgButton);
        }
    }

    private void updateAvatarSelection(ImageButton button, int drawableId) {
        // Remove previous selection highlight
        if (selectedAvatarButton != null) {
            selectedAvatarButton.setBackgroundResource(R.drawable.square_avatar);
        }

        // Set new selection
        button.setBackgroundResource(R.drawable.selected_frame);
        selectedAvatarButton = button;

        // Update preview and track changes
        avatarImage.setImageResource(drawableId);

        if (selectedAvatarResId != drawableId) {
            selectedAvatarResId = drawableId;
            hasUnsavedChanges = true;
            voiceManager.speak("Avatar selected");
        }
    }

    private void updateBackgroundSelection(ImageButton button, int drawableId) {
        // Remove previous selection highlight
        if (selectedBackgroundButton != null) {
            selectedBackgroundButton.setBackgroundResource(R.drawable.square_avatar);
        }

        // Set new selection
        button.setBackgroundResource(R.drawable.selected_frame);
        selectedBackgroundButton = button;

        // Update preview and track changes
        avatarBackground.setImageResource(drawableId);

        if (selectedBackgroundResId != drawableId) {
            selectedBackgroundResId = drawableId;
            hasUnsavedChanges = true;
            voiceManager.speak("Background selected");
        }
    }

    private void saveChanges() {
        if (!hasUnsavedChanges) {
            Toast.makeText(this, "No changes to save", Toast.LENGTH_SHORT).show();
            voiceManager.speak("No changes to save");
            return;
        }

        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putInt("selected_avatar", selectedAvatarResId);
        editor.putInt("selected_background", selectedBackgroundResId);

        boolean success = editor.commit();

        if (success) {
            hasUnsavedChanges = false;
            Toast.makeText(this, "Avatar updated successfully!", Toast.LENGTH_SHORT).show();
            voiceManager.speak("Avatar updated successfully!");

            Log.d(TAG, "Avatar changes saved - Avatar: " + selectedAvatarResId + ", Background: " + selectedBackgroundResId);

            // TODO: Update database here when you implement it
            // updateAvatarInDatabase(selectedAvatarResId, selectedBackgroundResId);

        } else {
            Toast.makeText(this, "Failed to save changes", Toast.LENGTH_SHORT).show();
            voiceManager.speak("Failed to save changes");
            Log.e(TAG, "Failed to save avatar changes");
        }
    }

    // TODO: Implement this method when you add database connectivity
    /*
    private void updateAvatarInDatabase(int avatarId, int backgroundId) {
        // Your database update logic here
        // Example:
        // DatabaseHelper dbHelper = new DatabaseHelper(this);
        // dbHelper.updateUserAvatar(getCurrentUserId(), avatarId, backgroundId);
    }
    */

    private void showUnsavedChangesDialog() {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Unsaved Changes")
                .setMessage("You have unsaved changes. Do you want to save them before leaving?")
                .setPositiveButton("Save", (dialog, which) -> {
                    saveChanges();
                    if (!hasUnsavedChanges) { // Only finish if save was successful
                        finish();
                    }
                })
                .setNegativeButton("Discard", (dialog, which) -> {
                    hasUnsavedChanges = false;
                    finish();
                })
                .setNeutralButton("Cancel", null)
                .show();
    }

    private void stopVoice() {
        if (voiceManager != null) {
            voiceManager.stop();
        }
    }

    @Override
    public void onBackPressed() {
        if (hasUnsavedChanges) {
            showUnsavedChangesDialog();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopVoice();
    }

    @Override
    protected void onDestroy() {
        stopVoice();
        super.onDestroy();
    }

    // Getter methods for accessing current selections (useful for testing or external access)
    public int getSelectedAvatarResId() {
        return selectedAvatarResId;
    }

    public int getSelectedBackgroundResId() {
        return selectedBackgroundResId;
    }

    public boolean hasUnsavedChanges() {
        return hasUnsavedChanges;
    }
}