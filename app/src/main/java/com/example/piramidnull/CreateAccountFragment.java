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
    private boolean isTestingVoice = false;

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
        try {
            voiceManager = VoiceManager.getInstance(requireContext());
            isVoiceInitialized = voiceManager.isInitialized();
            Log.d(TAG, "VoiceManager initialized: " + isVoiceInitialized);
        } catch (Exception e) {
            Log.e(TAG, "Error initializing VoiceManager", e);
            isVoiceInitialized = false;
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

        createAccountButton.setOnClickListener(v -> {
            String username = usernameInput.getText().toString().trim();
            String email = emailInput.getText().toString().trim();
            String password = passwordInput != null ? passwordInput.getText().toString().trim() : "";

            if (voiceTypeSpinner.getSelectedItemPosition() == 0) {
                speakAndToast("Please select a voice type");
                return;
            }

            if (validateInputs(username, email, password)) {
                createAccountButton.setEnabled(false);
                saveUserData(username, email, password);

                String welcomeMessage = "Welcome " + username + "! Your account has been created successfully.";

                if (isVoiceInitialized) {
                    voiceManager.speak(welcomeMessage, new VoiceManager.SpeechCallback() {
                        @Override
                        public void onSpeechCompleted() {
                            navigateToRoomsSlider(username, email);
                        }

                        @Override
                        public void onError(String error) {
                            Log.w(TAG, "Speech error: " + error);
                            navigateToRoomsSlider(username, email);
                        }
                    });
                } else {
                    Log.w(TAG, "Voice system not initialized.");
                    navigateToRoomsSlider(username, email);
                }

                showToast("Account created successfully!");
            }
        });
    }

    private void setupVoiceSpinner() {
        Context context = requireContext();

        voiceTypes = getResources().getStringArray(R.array.voice_types);
        int[] icons = {
                R.drawable.ic_arrow_down,
                R.drawable.ic_female,
                R.drawable.ic_male
        };

        CustomSpinnerAdapter adapter = new CustomSpinnerAdapter(context, voiceTypes, icons, R.drawable.arrow_right);
        voiceTypeSpinner.setAdapter(adapter);
        voiceTypeSpinner.setSelection(0);

        voiceTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    selectedVoiceType = "";
                    return;
                }

                String newVoice = voiceTypes[position];
                if (!newVoice.equals(selectedVoiceType)) {
                    selectedVoiceType = newVoice;
                    Log.d(TAG, "Voice selected: " + selectedVoiceType);

                    if (isVoiceInitialized && !isTestingVoice) {
                        voiceManager.stop();
                        voiceManager.setVoiceType(selectedVoiceType);

                        new Handler(Looper.getMainLooper()).postDelayed(() -> {
                            testVoice();
                        }, 200);
                    } else {
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
            return;
        }

        selectedVoiceType = voiceManager.getCurrentVoiceType();

        if (voiceTypes == null || voiceTypes.length == 0) {
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
        Log.d(TAG, "Loaded voice type: " + selectedVoiceType);
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
            voiceManager.stop();
            voiceManager.speak(message);
        }
    }

    private void saveUserData(String username, String email, String password) {
        Bundle args = getArguments();
        int avatarResId = args != null ? args.getInt("selected_avatar", R.drawable.avatar_4) : R.drawable.avatar_4;
        int backgroundResId = args != null ? args.getInt("selected_background", R.drawable.background_1) : R.drawable.background_1;

        voiceManager.setVoiceType(selectedVoiceType);
    }

    private void testVoice() {
        if (!isVoiceInitialized || isTestingVoice) return;

        isTestingVoice = true;

        voiceManager.testVoice(new VoiceManager.SpeechCallback() {
            @Override
            public void onSpeechStarted() {
                Log.d(TAG, "Voice test started");
            }

            @Override
            public void onSpeechCompleted() {
                Log.d(TAG, "Voice test completed");
                isTestingVoice = false;
            }

            @Override
            public void onError(String error) {
                Log.e(TAG, "Voice test error: " + error);
                showToast("Voice test failed: " + error);
                isTestingVoice = false;
            }
        });
    }

    private void navigateToRoomsSlider(String username, String email) {
        new Handler(Looper.getMainLooper()).post(() -> {
            Intent intent = new Intent(requireActivity(), RoomsSlider.class);
            intent.putExtra("username", username);
            intent.putExtra("email", email);
            intent.putExtra("voiceType", selectedVoiceType);
            startActivity(intent);
            requireActivity().finish();
        });
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
        isTestingVoice = false;
    }

    @Override
    public void onDestroyView() {
        if (isVoiceInitialized) {
            voiceManager.stop();
        }
        isTestingVoice = false;
        super.onDestroyView();
    }
}
