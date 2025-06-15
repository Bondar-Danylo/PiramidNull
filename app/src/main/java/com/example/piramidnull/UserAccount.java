package com.example.piramidnull;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.*;

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

    // Track selected buttons for visual feedback
    private ImageButton selectedAvatarButton = null;
    private ImageButton selectedBackgroundButton = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.landing);

        avatarOverlay = findViewById(R.id.avatarOverlay);
        overlayContentContainer = findViewById(R.id.overlayContentContainer);

        ViewCompat.setOnApplyWindowInsetsListener(avatarOverlay, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        inflateOverlayContent(R.layout.createaccount);
        showOverlayWithDelay();
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
            showStep(isAvatarStep);  // Restore to correct step (avatar/background)
        } else if (layoutResId == R.layout.createaccount_details) {
            bindDetailsViews(inflatedView);
            setupBackIconForDetails(inflatedView);
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

        detailAvatarBackground.setImageResource(selectedBackgroundResId);
        detailAvatarImage.setImageResource(selectedAvatarResId);
    }

    // NEW: Back button inside details screen
    private void setupBackIconForDetails(View root) {
        ImageButton detailsBackIcon = root.findViewById(R.id.back_icon);
        if (detailsBackIcon != null) {
            detailsBackIcon.setOnClickListener(v -> {
                isAvatarStep = false; // Go back to background selection
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
                inflateOverlayContent(R.layout.createaccount_details);
            }
        });

        btnPrevious.setOnClickListener(v -> {
            isAvatarStep = true;
            showStep(true);
        });

        backIcon.setOnClickListener(v -> finish());

        skipButton.setOnClickListener(v -> {
            inflateOverlayContent(R.layout.createaccount_details);
        });

        avatarOverlay.setOnClickListener(v -> avatarOverlay.setVisibility(View.GONE));
    }

    private void showOverlayWithDelay() {
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (!isFinishing()) {
                avatarOverlay.setVisibility(View.VISIBLE);
                avatarOverlay.setAlpha(0f);
                avatarOverlay.animate().alpha(1f).setDuration(300).start();
            }
        }, 1500);
    }

    private void showStep(boolean isCharacterStep) {
        avatarStepLayout.setVisibility(isCharacterStep ? View.VISIBLE : View.GONE);
        backgroundStepLayout.setVisibility(isCharacterStep ? View.GONE : View.VISIBLE);
        btnCharacter.setBackgroundResource(isCharacterStep ? R.drawable.btn_selected : R.drawable.btn_unselected);
        btnBackground.setBackgroundResource(!isCharacterStep ? R.drawable.btn_selected : R.drawable.btn_unselected);
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

            // This shows the blue frame when selected
            if (isAvatarGrid && drawableId == selectedAvatarResId) {
                imgButton.setBackgroundResource(R.drawable.selected_frame);  // ← BLUE FRAME APPLIED HERE
                selectedAvatarButton = imgButton;
            } else if (!isAvatarGrid && drawableId == selectedBackgroundResId) {
                imgButton.setBackgroundResource(R.drawable.selected_frame);  // ← BLUE FRAME APPLIED HERE
                selectedBackgroundButton = imgButton;
            }

            imgButton.setOnClickListener(v -> {
                if (isAvatarGrid) {
                    // Remove selection from previous avatar button
                    if (selectedAvatarButton != null) {
                        selectedAvatarButton.setBackgroundResource(R.drawable.square_avatar);  // ← BLUE FRAME REMOVED HERE
                    }

                    // Set new selection
                    imgButton.setBackgroundResource(R.drawable.selected_frame);  // ← BLUE FRAME APPLIED HERE
                    selectedAvatarButton = imgButton;

                    avatarImage.setImageResource(drawableId);
                    selectedAvatarResId = drawableId;
                } else {
                    // Remove selection from previous background button
                    if (selectedBackgroundButton != null) {
                        selectedBackgroundButton.setBackgroundResource(R.drawable.square_avatar);  // ← BLUE FRAME REMOVED
                    }

                    imgButton.setBackgroundResource(R.drawable.selected_frame);  // ← BLUE FRAME
                    selectedBackgroundButton = imgButton;

                    avatarBackground.setImageResource(drawableId);
                    selectedBackgroundResId = drawableId;
                }
            });

            grid.addView(imgButton);
        }
    }
}