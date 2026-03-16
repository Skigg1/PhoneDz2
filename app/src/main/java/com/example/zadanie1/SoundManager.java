package com.example.zadanie1;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.Log;

public class SoundManager {
    private static final String TAG = "SoundManager";
    private static MediaPlayer mediaPlayer;
    private static AudioManager audioManager;
    private static AudioManager.OnAudioFocusChangeListener afChangeListener;
    private static Context appContext;


    public static void playSound(Context context, int soundResId) {
        Log.d(TAG, "Simple playSound: " + soundResId);
        stopSound();
        try {
            mediaPlayer = MediaPlayer.create(context, soundResId);
            if (mediaPlayer != null) {
                mediaPlayer.setOnCompletionListener(mp -> {
                    mp.release();
                    mediaPlayer = null;
                });
                mediaPlayer.start();
                Log.d(TAG, "Sound started (simple mode)");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error: " + e.getMessage());
        }
    }


    public static void playSoundWithDuration(Context context, int soundResId, int maxDurationMs) {
        Log.d(TAG, "playSoundWithDuration called, resource: " + soundResId + ", duration: " + maxDurationMs);
        appContext = context.getApplicationContext();

        if (requestAudioFocus()) {
            Log.d(TAG, "Audio focus granted");
            stopSound();

            try {
                mediaPlayer = MediaPlayer.create(context, soundResId);
                if (mediaPlayer == null) {
                    Log.e(TAG, "Failed to create MediaPlayer");
                    abandonAudioFocus();
                    return;
                }

                mediaPlayer.setOnCompletionListener(mp -> {
                    Log.d(TAG, "Sound completed naturally");
                    mp.release();
                    mediaPlayer = null;
                    abandonAudioFocus();
                });

                mediaPlayer.start();
                Log.d(TAG, "Sound started, setting timeout: " + maxDurationMs + "ms");


                new android.os.Handler().postDelayed(() -> {
                    if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                        Log.d(TAG, "Timeout reached, stopping sound");
                        stopSound();
                        abandonAudioFocus();
                    }
                }, maxDurationMs);

            } catch (Exception e) {
                Log.e(TAG, "Exception: " + e.getMessage());
                e.printStackTrace();
                abandonAudioFocus();
            }
        } else {
            Log.e(TAG, "Could not get audio focus");
        }
    }

    // Запрос аудиофокуса
    private static boolean requestAudioFocus() {
        Log.d(TAG, "Requesting audio focus");

        if (audioManager == null && appContext != null) {
            audioManager = (AudioManager) appContext.getSystemService(Context.AUDIO_SERVICE);
            Log.d(TAG, "AudioManager initialized");
        }

        if (audioManager == null) {
            Log.e(TAG, "AudioManager is null");
            return false;
        }

        afChangeListener = focusChange -> {
            Log.d(TAG, "Audio focus changed: " + focusChange);
            if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
                Log.d(TAG, "Audio focus lost permanently");
                stopSound();
            } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT) {
                Log.d(TAG, "Audio focus lost transiently");
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                }
            } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
                Log.d(TAG, "Audio focus gained");
                if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
                    mediaPlayer.start();
                }
            }
        };

        int result = audioManager.requestAudioFocus(afChangeListener,
                AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);

        Log.d(TAG, "Audio focus request result: " + (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED ? "GRANTED" : "DENIED"));
        return result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED;
    }

    // Освобождение аудиофокуса
    private static void abandonAudioFocus() {
        Log.d(TAG, "Abandoning audio focus");
        if (audioManager != null && afChangeListener != null) {
            audioManager.abandonAudioFocus(afChangeListener);
        }
    }

    // Остановка звука
    public static void stopSound() {
        Log.d(TAG, "stopSound called");
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                Log.d(TAG, "Stopping playing sound");
                mediaPlayer.stop();
            }
            Log.d(TAG, "Releasing MediaPlayer");
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    // Метод для сброса
    public static void release() {
        Log.d(TAG, "release called");
        stopSound();
        abandonAudioFocus();
        audioManager = null;
        afChangeListener = null;
        appContext = null;
    }

    public static void playTimerFinishSound(Context context, int soundResId) {
        MediaPlayer timerPlayer = MediaPlayer.create(context, soundResId);
        if (timerPlayer != null) {
            timerPlayer.setOnCompletionListener(mp -> {
                mp.release();
            });
            timerPlayer.start();
        } else {
            Log.e(TAG, "Не удалось создать MediaPlayer для звука: " + soundResId);
        }
    }
}