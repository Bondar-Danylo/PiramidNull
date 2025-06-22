package com.example.piramidnull;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.widget.Toast;

import java.util.Locale;

public class VoiceManager {

    private static final String TAG = "VoiceManager";
    private static VoiceManager instance;

    private final Context context;
    private TextToSpeech tts;
    private boolean isInitialized = false;
    private String currentVoiceType = "female"; // Default to female

    private VoiceManager(Context context) {
        this.context = context.getApplicationContext();
        initTextToSpeech();
    }

    public static synchronized VoiceManager getInstance(Context context) {
        if (instance == null) {
            instance = new VoiceManager(context);
        }
        return instance;
    }

    private void initTextToSpeech() {
        tts = new TextToSpeech(context, status -> {
            if (status == TextToSpeech.SUCCESS) {
                // Set default voice to female UK English
                int result = tts.setLanguage(Locale.UK);
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Log.e(TAG, "Language not supported");
                    Toast.makeText(context, "TTS language not supported", Toast.LENGTH_SHORT).show();
                } else {
                    isInitialized = true;
                    setVoice(currentVoiceType);
                    Log.d(TAG, "TTS initialized successfully");
                }
            } else {
                Log.e(TAG, "TTS Initialization failed");
                Toast.makeText(context, "TTS Initialization failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public synchronized void setVoice(String voiceType) {
        if (!isInitialized) {
            Log.w(TAG, "TTS not initialized yet");
            return;
        }

        currentVoiceType = voiceType.toLowerCase();

        // Android TTS does not have direct male/female voice setting for all devices,
        // but we can try to select voice with different pitch and locale variants.
        // For demonstration, we'll adjust pitch for male/female:

        if ("male".equals(currentVoiceType)) {
            tts.setPitch(0.8f); // Slightly lower pitch for male
            tts.setSpeechRate(1.0f);
            Log.d(TAG, "Voice set to MALE");
        } else {
            // Default female voice
            tts.setPitch(1.2f); // Slightly higher pitch for female
            tts.setSpeechRate(1.0f);
            Log.d(TAG, "Voice set to FEMALE");
        }
    }

    public synchronized String getCurrentVoiceType() {
        return currentVoiceType;
    }

    public boolean isInitialized() {
        return isInitialized;
    }

    public synchronized void speak(String text) {
        if (!isInitialized) {
            Log.e(TAG, "TTS not initialized");
            Toast.makeText(context, "TextToSpeech not initialized", Toast.LENGTH_SHORT).show();
            return;
        }
        if (text == null || text.trim().isEmpty()) {
            Log.w(TAG, "Empty text to speak");
            return;
        }

        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "tts_utterance_id");
        Log.d(TAG, "Speaking: " + text);
    }

    public synchronized void stop() {
        if (tts != null && tts.isSpeaking()) {
            tts.stop();
            Log.d(TAG, "TTS stopped");
        }
    }

    public synchronized void destroy() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
            tts = null;
            isInitialized = false;
            Log.d(TAG, "TTS destroyed");
        }
    }
}
