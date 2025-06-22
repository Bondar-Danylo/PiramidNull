package com.example.piramidnull;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class CreateAccountFragment extends Fragment {

    private static final String TAG = "CreateAccountFragment";

    private VoiceManager voiceManager;
    private Spinner voiceTypeSpinner;
    private String selectedVoiceType = "";
    private boolean isVoiceInitialized = false;

    private String[] voiceTypes;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_create_account, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initVoiceManager();
        setupViews(view);
        loadUserPreferences();
    }

    private void initVoiceManager() {
        voiceManager = VoiceManager.getInstance(requireContext());
        isVoiceInitialized = voiceManager != null && voiceManager.isInitialized();

        Log.d(TAG, "VoiceManager initialized: " + isVoiceInitialized);

        if (!isVoiceInitialized) {
            Log.e(TAG, "VoiceManager not initialized in CreateAccountFragment");
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                isVoiceInitialized = voiceManager != null && voiceManager.isInitialized();
                if (isVoiceInitialized) {
                    Log.i(TAG, "VoiceManager initialized successfully on retry");
                }
            }, 1000);
        }
    }

    private void setupViews(View view) {
        ImageView detailAvatarBackground = view.findViewById(R.id.detailAvatarBackground);
        ImageView detailAvatarImage = view.findViewById(R.id.detailAvatarImage);
        voiceTypeSpinner = view.findViewById(R.id.genderSpinner);
        Button createAccountButton = view.findViewById(R.id.createaccount_button);
        EditText usernameInput = view.findViewById(R.id.username_input);
        EditText emailInput = view.findViewById(R.id.email_input);
        EditText passwordInput = view.findViewById(R.id.password_input);
        ImageButton backIcon = view.findViewById(R.id.back_icon);

        Bundle args = getArguments();
        if (args != null) {
            int avatarResId = args.getInt("selected_avatar", R.drawable.avatar_4);
            int backgroundResId = args.getInt("selected_background", R.drawable.background_1);
            detailAvatarBackground.setImageResource(backgroundResId);
            detailAvatarImage.setImageResource(avatarResId);
        }

        setupVoiceSpinner();

        backIcon.setOnClickListener(v -> {
            if (isVoiceInitialized) voiceManager.stop();
            requireActivity().onBackPressed();
        });

        createAccountButton.setOnClickListener(v -> {
            String username = usernameInput.getText().toString().trim();
            String email = emailInput.getText().toString().trim();
            String password = passwordInput != null ? passwordInput.getText().toString().trim() : "";

            if (voiceTypeSpinner.getSelectedItemPosition() == 0) {
                speakAndToast("Please select a voice type");
                return;
            }

            if (validateInputs(username, email, password)) {
                saveUserData(username, email, password);

                String welcomeMessage = "Welcome " + username + "! Your account has been created successfully.";
                if (isVoiceInitialized) {
                    voiceManager.speak(welcomeMessage);
                } else {
                    Log.w(TAG, "Cannot play welcome message - VoiceManager not initialized");
                }

                showToast("Account created successfully!");

                // Pass selectedVoiceType via Intent to RoomsSlider
                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    Intent intent = new Intent(requireActivity(), RoomsSlider.class);
                    intent.putExtra("username", username);
                    intent.putExtra("email", email);
                    intent.putExtra("voiceType", selectedVoiceType);  // important!
                    startActivity(intent);
                    requireActivity().finish();
                }, 2000);
            }
        });
    }

    private void setupVoiceSpinner() {
        Context context = requireContext();

        voiceTypes = getResources().getStringArray(R.array.voice_types); // ["Select Type", "Cleopatra", "Pharaoh"]
        int[] icons = {
                R.drawable.ic_arrow_down,
                R.drawable.ic_female,  // Cleopatra
                R.drawable.ic_male     // Pharaoh
        };

        CustomSpinnerAdapter adapter = new CustomSpinnerAdapter(context, voiceTypes, icons, R.drawable.arrow_right);
        voiceTypeSpinner.setAdapter(adapter);
        voiceTypeSpinner.setSelection(0); // Default to Select Type

        voiceTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    selectedVoiceType = "";
                    return;
                }

                String newVoiceType = voiceTypes[position];
                if (!newVoiceType.equals(selectedVoiceType)) {
                    selectedVoiceType = newVoiceType;
                    Log.d(TAG, "Voice selection changed to: " + selectedVoiceType);

                    if (isVoiceInitialized) {
                        voiceManager.stop();

                        new Handler(Looper.getMainLooper()).postDelayed(() -> {
                            String voiceId = UserAccount.getVoiceIdFromType(selectedVoiceType);
                            Log.d(TAG, "Setting voice ID: " + voiceId);

                            new Handler(Looper.getMainLooper()).postDelayed(() -> CreateAccountFragment.this.testVoice(), 300);                        }, 100);
                    } else {
                        Log.w(TAG, "VoiceManager not initialized, cannot change voice");
                        showToast("Voice system not ready");
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedVoiceType = "";
            }
        });
    }

    private void loadUserPreferences() {
        if (!isVoiceInitialized) {
            Log.w(TAG, "Cannot load voice preferences - VoiceManager not initialized");
            return;
        }

        String savedVoiceType = voiceManager.getCurrentVoiceType();
        selectedVoiceType = savedVoiceType != null ? savedVoiceType : "";

        if (voiceTypes == null) {
            voiceTypes = getResources().getStringArray(R.array.voice_types);
        }

        int index = 0;
        for (int i = 0; i < voiceTypes.length; i++) {
            if (voiceTypes[i].equals(selectedVoiceType)) {
                index = i;
                break;
            }
        }

        voiceTypeSpinner.setSelection(index);
    }

    private boolean validateInputs(String username, String email, String password) {
        if (username.isEmpty()) {
            speakAndToast("Please enter a username");
            return false;
        }
        if (username.length() < 3) {
            speakAndToast("Username must be at least 3 characters");
            return false;
        }
        if (email.isEmpty()) {
            speakAndToast("Please enter an email address");
            return false;
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            speakAndToast("Please enter a valid email address");
            return false;
        }
        if (!password.isEmpty() && password.length() < 6) {
            speakAndToast("Password must be at least 6 characters");
            return false;
        }
        return true;
    }

    private void speakAndToast(String message) {
        showToast(message);
        if (isVoiceInitialized) {
            voiceManager.speak(message);
        }
    }

    private void saveUserData(String username, String email, String password) {
        String voiceId = UserAccount.getVoiceIdFromType(selectedVoiceType);

        Bundle args = getArguments();
        int avatarResId = args != null ? args.getInt("selected_avatar", R.drawable.avatar_4) : R.drawable.avatar_4;
        int backgroundResId = args != null ? args.getInt("selected_background", R.drawable.background_1) : R.drawable.background_1;

        // Store user data and preferences here as needed (DB, SharedPreferences etc.)
    }

    private void testVoice() {
        if (!isVoiceInitialized) {
            Log.w(TAG, "Cannot test voice - VoiceManager not initialized");
            showToast("Voice test unavailable");
            return;
        }

        String testMessage;
        switch (selectedVoiceType) {
            case "Pharaoh":
                testMessage = "Hello! I am Pharaoh, your male voice assistant.";
                break;
            case "Cleopatra":
                testMessage = "Hello! I am Cleopatra, your female voice assistant.";
                break;
            default:
                testMessage = "Please select a valid voice type.";
                break;
        }

        voiceManager.speak(testMessage);
    }

    private void showToast(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (isVoiceInitialized) {
            voiceManager.stop();
        }
    }

    @Override
    public void onDestroyView() {
        if (isVoiceInitialized) {
            voiceManager.stop();
        }
        super.onDestroyView();
    }
}
