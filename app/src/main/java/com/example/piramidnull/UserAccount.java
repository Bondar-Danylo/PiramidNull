package com.example.piramidnull;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.imageview.ShapeableImageView;

public class UserAccount extends AppCompatActivity {

    private ShapeableImageView avatarImage;
    private ImageView avatarBackground;
    private GridLayout avatarGrid, backgroundGrid;
    private LinearLayout avatarStepLayout, backgroundStepLayout;
    private Button btnCharacter, btnBackground, skipButton;
    private View avatarOverlay;
    private ImageView btnPrevious;
    private ImageButton btnNext;
    private ImageButton backIcon;

    private final int[] avatarIds = {
            R.drawable.avatar_1, R.drawable.avatar_2, R.drawable.avatar_3,
            R.drawable.avatar_4, R.drawable.avatar_5, R.drawable.avatar_6
    };

    private final int[] backgroundIds = {
            R.drawable.background_1, R.drawable.background_2, R.drawable.background_3,
            R.drawable.background_4, R.drawable.background_5, R.drawable.background_6
    };

    private int selectedAvatarResId = R.drawable.avatar_1;
    private int selectedBackgroundResId = R.drawable.background_1;

    private boolean isAvatarStep = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.landing);

        avatarOverlay = findViewById(R.id.avatarOverlay);
        if (avatarOverlay != null) {
            ViewCompat.setOnApplyWindowInsetsListener(avatarOverlay, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        }

        initViews();
        setupListeners();
        loadGridItems(avatarGrid, avatarIds, true);
        loadGridItems(backgroundGrid, backgroundIds, false);
        showStep(true);
        showOverlayWithDelay();
    }

    private void initViews() {
        avatarImage = findViewById(R.id.avatarImage);
        avatarBackground = findViewById(R.id.avatarBackground);

        // Set default avatar and background when activity loads
        selectedAvatarResId = R.drawable.avatar_4;

        avatarImage.setImageResource(selectedAvatarResId);


        avatarGrid = findViewById(R.id.avatarGrid);
        backgroundGrid = findViewById(R.id.backgroundGrid);
        avatarStepLayout = findViewById(R.id.avatarStepLayout);
        backgroundStepLayout = findViewById(R.id.backgroundStepLayout);
        btnCharacter = findViewById(R.id.btn_character);
        btnBackground = findViewById(R.id.btn_background);
        skipButton = findViewById(R.id.btn_skip);
        btnPrevious = findViewById(R.id.btn_previous);
        btnNext = findViewById(R.id.btn_next);
        backIcon = findViewById(R.id.back_icon);
    }

    private void setupListeners() {
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
                // Move from avatar to background step
                isAvatarStep = false;
                showStep(false);
            } else {startActivity(new Intent(UserAccount.this, CreateAccountDetails.class));
            }
        });
        btnPrevious.setOnClickListener(v -> {
            isAvatarStep = true;
            showStep(true);
        });
        backIcon.setOnClickListener(v -> {
            finish();
        });

        skipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(UserAccount.this, CreateAccountDetails.class));
            }
        });

        if (avatarOverlay != null) {
            avatarOverlay.setOnClickListener(v -> avatarOverlay.setVisibility(View.GONE));
        }
    }

    private void goToDetailsActivity() {
        Intent intent = new Intent(UserAccount.this, CreateAccountDetails.class);
        intent.putExtra("SELECTED_AVATAR", selectedAvatarResId);
        intent.putExtra("SELECTED_BACKGROUND", selectedBackgroundResId);
        startActivity(intent);
    }

    private void showOverlayWithDelay() {
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (avatarOverlay != null && !isFinishing()) {
                avatarOverlay.setVisibility(View.VISIBLE);
                avatarOverlay.setAlpha(0f);
                avatarOverlay.animate().alpha(1f).setDuration(300).start();
            }
        }, 2500);
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

            imgButton.setOnClickListener(v -> {
                if (isAvatarGrid) {
                    avatarImage.setImageResource(drawableId);
                    selectedAvatarResId = drawableId;
                } else {
                    avatarBackground.setImageResource(drawableId);
                    avatarImage.setBackgroundResource(drawableId);
                    selectedBackgroundResId = drawableId;
                }
            });

            grid.addView(imgButton);
        }
    }
}
