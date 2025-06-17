package com.example.piramidnull;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.landing);

        avatarOverlay = findViewById(R.id.avatarOverlay);
        overlayContentContainer = avatarOverlay.findViewById(R.id.overlayContentContainer);

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
            showStep(isAvatarStep);
        } else if (layoutResId == R.layout.createaccount_details) {
            bindDetailsViews(inflatedView);
            setupDetailsListeners(inflatedView);
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

    private void setupBackIconForDetails(View root) {
        ImageButton detailsBackIcon = root.findViewById(R.id.back_icon);
        if (detailsBackIcon != null) {
            detailsBackIcon.setOnClickListener(v -> {
                isAvatarStep = false;
                inflateOverlayContent(R.layout.createaccount);
            });
        }
    }

    // Custom Spinner Adapter
    public class CustomSpinnerAdapter extends ArrayAdapter<String> {
        private final Context context;
        private final String[] values;
        private final int[] icons;

        public CustomSpinnerAdapter(Context context, String[] values, int[] icons) {
            super(context, R.layout.spinner_item, values);
            this.context = context;
            this.values = values;
            this.icons = icons;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return createCustomView(position, convertView, parent, R.layout.spinner_item);
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            return createCustomView(position, convertView, parent, R.layout.spinner_item);
        }

        private View createCustomView(int position, View convertView, ViewGroup parent, int layoutId) {
            View view = LayoutInflater.from(context).inflate(layoutId, parent, false);
            TextView text = view.findViewById(R.id.spinnerText);
            ImageView icon = view.findViewById(R.id.spinnerIcon);

            text.setText(values[position]);
            icon.setImageResource(icons[position]);
            return view;
        }
    }

    private void setupDetailsListeners(View root) {
        // Spinner for voice types
        Spinner genderSpinner = root.findViewById(R.id.genderSpinner);

        // Array of values for voice types
        String[] voiceTypes = getResources().getStringArray(R.array.voice_types);
        int[] voiceTypeIcons = { R.drawable.ic_arrow_down, R.drawable.ic_female, R.drawable.ic_male };

        // Create and set the custom spinner adapter
        CustomSpinnerAdapter customAdapter = new CustomSpinnerAdapter(this, voiceTypes, voiceTypeIcons);
        genderSpinner.setAdapter(customAdapter);

        // Button Create Account to launch next activity with data
        Button createAccount = root.findViewById(R.id.createaccount_button);
        EditText username = root.findViewById(R.id.username_input);
        EditText email = root.findViewById(R.id.email_input);

        createAccount.setOnClickListener(v -> {
            String user = username.getText().toString().trim();
            String mail = email.getText().toString().trim();
            String voice = genderSpinner.getSelectedItem().toString();

            if (user.isEmpty() || mail.isEmpty()) {
                Toast.makeText(UserAccount.this, "Please enter username and email", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent = new Intent(UserAccount.this, MainSliderActivity.class);
            intent.putExtra("USERNAME", user);
            intent.putExtra("EMAIL", mail);
            intent.putExtra("VOICE_TYPE", voice);
            startActivity(intent);
            finish();
        });
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

        skipButton.setOnClickListener(v -> inflateOverlayContent(R.layout.createaccount_details));

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
        if (grid == null) {
            Log.w(TAG, "GridLayout is null, skipping loadGridItems");
            return;
        }
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
                    if (selectedAvatarButton != null) {
                        selectedAvatarButton.setBackgroundResource(R.drawable.square_avatar);
                    }
                    imgButton.setBackgroundResource(R.drawable.selected_frame);
                    selectedAvatarButton = imgButton;

                    avatarImage.setImageResource(drawableId);
                    selectedAvatarResId = drawableId;
                } else {
                    if (selectedBackgroundButton != null) {
                        selectedBackgroundButton.setBackgroundResource(R.drawable.square_avatar);
                    }
                    imgButton.setBackgroundResource(R.drawable.selected_frame);
                    selectedBackgroundButton = imgButton;

                    avatarBackground.setImageResource(drawableId);
                    selectedBackgroundResId = drawableId;
                }
            });

            grid.addView(imgButton);
        }
    }
}
