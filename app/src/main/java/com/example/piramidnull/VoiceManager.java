package com.example.piramidnull;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

public class VoiceManager {
    private static final String TAG = "VoiceManager";
    private static final String PREFS_NAME = "voice_preferences";
    private static final String KEY_VOICE_TYPE = "voice_type";

    private static VoiceManager instance;
    private final Context context;
    private final GoogleTTSHelper ttsHelper;
    private final SharedPreferences preferences;
    private final Handler mainHandler;

    private String currentVoiceType = "Cleopatra"; // Default
    private boolean isInitialized = false;
    private boolean isSpeaking = false;

    private String lastSpokenText = null; // For replay support

    private VoiceManager(Context context) {
        this.context = context.getApplicationContext();
        this.ttsHelper = new GoogleTTSHelper(this.context, "en-GB");
        this.preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        this.mainHandler = new Handler(Looper.getMainLooper());

        // Load saved voice preference
        currentVoiceType = preferences.getString(KEY_VOICE_TYPE, "Cleopatra");
        isInitialized = true;

        Log.d(TAG, "VoiceManager initialized with voice type: " + currentVoiceType);
    }

    public static synchronized VoiceManager getInstance(Context context) {
        if (instance == null) {
            instance = new VoiceManager(context);
        }
        return instance;
    }

    public void speak(String text) {
        speak(text, null);
    }

    public void speak(String text, SpeechCallback callback) {
        if (!isInitialized) {
            Log.w(TAG, "VoiceManager not initialized");
            runOnMainThread(() -> {
                if (callback != null) callback.onError("VoiceManager not initialized");
            });
            return;
        }

        if (text == null || text.trim().isEmpty()) {
            Log.w(TAG, "Empty text provided for speech");
            runOnMainThread(() -> {
                if (callback != null) callback.onError("Empty text");
            });
            return;
        }

        stop(); // Stop any ongoing playback

        lastSpokenText = text;
        isSpeaking = true;

        Log.d(TAG, "Speaking text: " + text + " with voice: " + currentVoiceType);

        ttsHelper.synthesizeSpeech(text, currentVoiceType, new GoogleTTSHelper.AudioReadyCallback() {
            @Override
            public void onAudioReady(byte[] audioData) {
                runOnMainThread(() -> {
                    Log.d(TAG, "Audio data received");
                    if (callback != null) callback.onSpeechReady();
                });
            }

            @Override
            public void onError(Exception e) {
                runOnMainThread(() -> {
                    Log.e(TAG, "TTS Error: " + e.getMessage(), e);
                    isSpeaking = false;
                    if (callback != null) callback.onError(e.getMessage());
                });
            }

            @Override
            public void onPlaybackStarted() {
                runOnMainThread(() -> {
                    Log.d(TAG, "Playback started");
                    isSpeaking = true;
                    if (callback != null) callback.onSpeechStarted();
                });
            }

            @Override
            public void onPlaybackCompleted() {
                runOnMainThread(() -> {
                    Log.d(TAG, "Playback completed");
                    isSpeaking = false;
                    if (callback != null) callback.onSpeechCompleted();
                });
            }
        });
    }

    public void stop() {
        if (ttsHelper != null) {
            ttsHelper.stopPlayback();
        }
        isSpeaking = false;
        Log.d(TAG, "Speech stopped");
    }

    public void setVoiceType(String voiceType) {
        if (voiceType == null || voiceType.isEmpty()) {
            Log.w(TAG, "Invalid voice type");
            return;
        }

        String oldVoiceType = currentVoiceType;
        currentVoiceType = voiceType;

        preferences.edit()
                .putString(KEY_VOICE_TYPE, voiceType)
                .apply();

        Log.d(TAG, "Voice type changed from " + oldVoiceType + " to " + currentVoiceType);
    }

    public String getCurrentVoiceType() {
        return currentVoiceType;
    }

    public boolean isInitialized() {
        return isInitialized;
    }

    public boolean isSpeaking() {
        return isSpeaking;
    }

    public void testVoice(SpeechCallback callback) {
        String testMessage = getTestMessage(currentVoiceType);
        speak(testMessage, callback);
    }

    private String getTestMessage(String voiceType) {
        switch (voiceType) {
            case "Pharaoh":
                return "Greetings! I am Pharaoh, your commanding voice assistant.";
            case "Cleopatra":
                return "Hello! I am Cleopatra, your elegant voice assistant.";
            default:
                return "Hello! I am your guide bot assistant.";
        }
    }

    public void replayLastMessage(SpeechCallback callback) {
        if (lastSpokenText != null) {
            speak(lastSpokenText, callback);
        } else {
            Log.w(TAG, "No message to replay");
            runOnMainThread(() -> {
                if (callback != null) callback.onError("No previous message");
            });
        }
    }

    public void release() {
        stop();
        if (ttsHelper != null) {
            ttsHelper.release();
        }
        Log.d(TAG, "VoiceManager resources released");
    }

    private void runOnMainThread(Runnable runnable) {
        mainHandler.post(runnable);
    }

    public interface SpeechCallback {
        default void onSpeechReady() {}
        default void onSpeechStarted() {}
        default void onSpeechCompleted() {}
        default void onError(String error) {}
    }
}
