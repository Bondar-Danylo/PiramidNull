package com.example.piramidnull;

import android.content.Context;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;

public class ApiConfig {
    private static final String TAG = "ApiConfig";

    // Audio configuration for Google TTS API
    public static class AudioConfig {
        public static final String ENCODING = "LINEAR16";
        public static final int SAMPLE_RATE = 16000;
        public static final double PITCH = 0.0;
        public static final double SPEAKING_RATE = 1.0;
    }

    public static JSONObject loadServiceAccount(Context context) throws Exception {
        InputStream is = null;
        try {
            is = context.getAssets().open("pyramidnull.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            int readBytes = is.read(buffer);

            String json = new String(buffer, "UTF-8");
            return new JSONObject(json);
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }

    public static String getGoogleVoiceForType(String voiceType) {
        switch (voiceType.toLowerCase()) {
            case "cleopatra":
                return "en-GB-Chirp-HD-F";
            case "pharaoh":
                return "en-GB-Chirp3-HD-Zubenelgenubi";
            default:
                return "en-GB-Chirp-HD-F";
        }
    }
}
