package com.example.piramidnull;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Handler;
import android.os.Looper;
import android.util.Base64;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GoogleTTSHelper {
    private static final String TAG = "GoogleTTSHelper";
    private static final String API_URL = "https://texttospeech.googleapis.com/v1/text:synthesize";
    private static final int MAX_TEXT_LENGTH = 6000;

    private final Context context;
    private AudioTrack audioTrack;
    private boolean isPlaying = false;
    private final ExecutorService executorService;
    private final Handler mainHandler;
    private final String languageCode;

    public interface AudioReadyCallback {
        void onAudioReady(byte[] audioData);
        void onError(Exception e);
        void onPlaybackStarted();
        void onPlaybackCompleted();
    }

    public GoogleTTSHelper(Context context, String languageCode) {
        this.context = context.getApplicationContext();
        this.executorService = Executors.newSingleThreadExecutor();
        this.mainHandler = new Handler(Looper.getMainLooper());
        this.languageCode = (languageCode != null && !languageCode.isEmpty()) ? languageCode : "en-GB";
    }

    private String createJWT(JSONObject serviceAccount) throws Exception {
        String privateKeyPem = serviceAccount.getString("private_key");
        String clientEmail = serviceAccount.getString("client_email");

        privateKeyPem = privateKeyPem
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s+", "");

        byte[] keyBytes = Base64.decode(privateKeyPem, Base64.DEFAULT);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        PrivateKey privateKey = kf.generatePrivate(spec);

        long nowSeconds = System.currentTimeMillis() / 1000;
        long expSeconds = nowSeconds + 3600;

        JSONObject header = new JSONObject();
        header.put("alg", "RS256");
        header.put("typ", "JWT");

        JSONObject claimSet = new JSONObject();
        claimSet.put("iss", clientEmail);
        claimSet.put("scope", "https://www.googleapis.com/auth/cloud-platform");
        claimSet.put("aud", "https://oauth2.googleapis.com/token");
        claimSet.put("exp", expSeconds);
        claimSet.put("iat", nowSeconds);

        String headerEncoded = base64UrlEncode(header.toString().getBytes("UTF-8"));
        String claimEncoded = base64UrlEncode(claimSet.toString().getBytes("UTF-8"));
        String jwtUnsigned = headerEncoded + "." + claimEncoded;

        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(privateKey);
        signature.update(jwtUnsigned.getBytes("UTF-8"));
        byte[] signedBytes = signature.sign();
        String signatureEncoded = base64UrlEncode(signedBytes);

        return jwtUnsigned + "." + signatureEncoded;
    }

    private String base64UrlEncode(byte[] input) {
        return Base64.encodeToString(input, Base64.URL_SAFE | Base64.NO_PADDING | Base64.NO_WRAP);
    }

    private String fetchAccessToken(String jwt) throws Exception {
        URL url = new URL("https://oauth2.googleapis.com/token");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        try {
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setDoOutput(true);
            conn.setConnectTimeout(15000);
            conn.setReadTimeout(15000);

            String requestBody = "grant_type=urn%3Aietf%3Aparams%3Aoauth%3Agrant-type%3Ajwt-bearer&assertion=" + jwt;

            try (OutputStream os = conn.getOutputStream()) {
                os.write(requestBody.getBytes("UTF-8"));
                os.flush();
            }

            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"))) {
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        response.append(line.trim());
                    }
                    JSONObject jsonResponse = new JSONObject(response.toString());
                    return jsonResponse.getString("access_token");
                }
            } else {
                try (BufferedReader errorReader = new BufferedReader(new InputStreamReader(conn.getErrorStream(), "UTF-8"))) {
                    StringBuilder errorResponse = new StringBuilder();
                    String line;
                    while ((line = errorReader.readLine()) != null) {
                        errorResponse.append(line.trim());
                    }
                    throw new IOException("Token fetch failed with code: " + responseCode + ", error: " + errorResponse.toString());
                }
            }
        } finally {
            conn.disconnect();
        }
    }

    public void synthesizeSpeech(String text, String voiceType, AudioReadyCallback callback) {
        if (text == null || text.trim().isEmpty()) {
            if (callback != null) {
                mainHandler.post(() -> callback.onError(new IllegalArgumentException("Text cannot be empty")));
            }
            return;
        }

        if (text.length() > MAX_TEXT_LENGTH) {
            if (callback != null) {
                mainHandler.post(() -> callback.onError(new IllegalArgumentException("Text too long. Maximum " + MAX_TEXT_LENGTH + " characters allowed.")));
            }
            return;
        }

        String googleVoiceName = ApiConfig.getGoogleVoiceForType(voiceType);
        Log.d(TAG, "Synthesizing speech with voice: " + googleVoiceName);

        executorService.execute(() -> {
            try {
                byte[] audioData = callGoogleTTSAPI(text, googleVoiceName);
                mainHandler.post(() -> {
                    if (callback != null) {
                        callback.onAudioReady(audioData);
                    }
                    playAudio(audioData, callback);
                });
            } catch (Exception e) {
                Log.e(TAG, "Error in TTS synthesis", e);
                mainHandler.post(() -> {
                    if (callback != null) {
                        callback.onError(e);
                    }
                });
            }
        });
    }

    private byte[] callGoogleTTSAPI(String text, String voiceName) throws Exception {
        JSONObject serviceAccount = ApiConfig.loadServiceAccount(context);
        String jwt = createJWT(serviceAccount);
        String accessToken = fetchAccessToken(jwt);

        URL url = new URL(API_URL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        try {
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            connection.setRequestProperty("Authorization", "Bearer " + accessToken);
            connection.setRequestProperty("User-Agent", "PyramidNull-Android/1.0");
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setConnectTimeout(30000);
            connection.setReadTimeout(30000);

            JSONObject requestBody = createRequestBody(text, voiceName);
            Log.d(TAG, "Sending TTS request for text length: " + text.length());

            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = requestBody.toString().getBytes("utf-8");
                os.write(input, 0, input.length);
                os.flush();
            }

            int responseCode = connection.getResponseCode();
            Log.d(TAG, "HTTP Response Code: " + responseCode);

            if (responseCode == HttpURLConnection.HTTP_OK) {
                StringBuilder response = new StringBuilder();
                try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"))) {
                    String responseLine;
                    while ((responseLine = br.readLine()) != null) {
                        response.append(responseLine.trim());
                    }
                }

                JSONObject jsonResponse = new JSONObject(response.toString());
                if (jsonResponse.has("audioContent")) {
                    String audioContent = jsonResponse.getString("audioContent");
                    byte[] audioData = Base64.decode(audioContent, Base64.DEFAULT);
                    Log.d(TAG, "Received audio data: " + audioData.length + " bytes");
                    return audioData;
                } else {
                    throw new IOException("No audioContent in response");
                }
            } else {
                StringBuilder errorResponse = new StringBuilder();
                try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getErrorStream(), "utf-8"))) {
                    String responseLine;
                    while ((responseLine = br.readLine()) != null) {
                        errorResponse.append(responseLine.trim());
                    }
                }

                String errorMsg = "API Error " + responseCode + ": " + errorResponse.toString();
                Log.e(TAG, errorMsg);
                throw new IOException(errorMsg);
            }

        } finally {
            connection.disconnect();
        }
    }

    private JSONObject createRequestBody(String text, String voiceName) throws JSONException {
        JSONObject requestBody = new JSONObject();

        JSONObject input = new JSONObject();
        input.put("text", text);
        requestBody.put("input", input);

        JSONObject voice = new JSONObject();
        voice.put("name", voiceName);
        voice.put("languageCode", languageCode);
        requestBody.put("voice", voice);

        JSONObject audioConfig = new JSONObject();
        audioConfig.put("audioEncoding", "LINEAR16");
        audioConfig.put("sampleRateHertz", 16000);
        audioConfig.put("pitch", ApiConfig.AudioConfig.PITCH);
        audioConfig.put("speakingRate", ApiConfig.AudioConfig.SPEAKING_RATE);
        requestBody.put("audioConfig", audioConfig);

        return requestBody;
    }

    private void playAudio(byte[] audioData, AudioReadyCallback callback) {
        stopPlayback();

        try {
            int sampleRate = ApiConfig.AudioConfig.SAMPLE_RATE; // 16000
            int channelConfig = AudioFormat.CHANNEL_OUT_MONO;
            int audioFormat = AudioFormat.ENCODING_PCM_16BIT;

            int minBufferSize = AudioTrack.getMinBufferSize(sampleRate, channelConfig, audioFormat);
            int bufferSize = Math.max(minBufferSize, audioData.length);

            audioTrack = new AudioTrack(
                    new AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                            .build(),
                    new AudioFormat.Builder()
                            .setSampleRate(sampleRate)
                            .setEncoding(audioFormat)
                            .setChannelMask(channelConfig)
                            .build(),
                    bufferSize,
                    AudioTrack.MODE_STREAM,
                    AudioManager.AUDIO_SESSION_ID_GENERATE
            );

            if (audioTrack.getState() == AudioTrack.STATE_INITIALIZED) {
                // Set the notification marker at the end of the audio (in frames)
                audioTrack.setNotificationMarkerPosition(audioData.length / 2); // 2 bytes per frame for 16-bit PCM

                audioTrack.setPlaybackPositionUpdateListener(new AudioTrack.OnPlaybackPositionUpdateListener() {
                    @Override
                    public void onMarkerReached(AudioTrack track) {
                        synchronized (GoogleTTSHelper.this) {
                            isPlaying = false;
                        }
                        Log.d(TAG, "Playback completed");
                        if (callback != null) {
                            mainHandler.post(callback::onPlaybackCompleted);
                        }
                    }
                    @Override
                    public void onPeriodicNotification(AudioTrack track) {
                        // Not needed here
                    }
                });

                audioTrack.play();
                isPlaying = true;
                Log.d(TAG, "Audio playback started");

                if (callback != null) {
                    callback.onPlaybackStarted();
                }

                int written = audioTrack.write(audioData, 0, audioData.length);
                Log.d(TAG, "Audio data written: " + written + " bytes");

            } else {
                throw new RuntimeException("AudioTrack initialization failed");
            }

        } catch (Exception e) {
            Log.e(TAG, "Error playing audio", e);
            synchronized (this) {
                isPlaying = false;
            }
            if (callback != null) {
                mainHandler.post(() -> callback.onError(e));
            }
        }
    }



    public synchronized void stopPlayback() {
        if (audioTrack != null) {
            try {
                if (audioTrack.getState() == AudioTrack.STATE_INITIALIZED && isPlaying) {
                    audioTrack.stop();
                }
                audioTrack.release();
            } catch (Exception e) {
                Log.e(TAG, "Error stopping audio", e);
            } finally {
                audioTrack = null;
                isPlaying = false;
            }
        }
    }

    public synchronized boolean isPlaying() {
        return isPlaying && audioTrack != null &&
                audioTrack.getPlayState() == AudioTrack.PLAYSTATE_PLAYING;
    }

    public void release() {
        stopPlayback();
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
        Log.d(TAG, "GoogleTTSHelper released");
    }
}
