package com.example.zadanie1;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.widget.MediaController;
import android.widget.VideoView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Locale;

public class VideoActivity extends AppCompatActivity {

    private VideoView videoView;
    private AudioManager audioManager;
    private AudioManager.OnAudioFocusChangeListener audioFocusChangeListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        applySavedLanguage();
        setContentView(R.layout.activity_video);

        videoView = findViewById(R.id.videoView);
        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);

        // Запрашиваем аудиофокус
        audioFocusChangeListener = focusChange -> {
            if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
                if (videoView.isPlaying()) {
                    videoView.pause();
                }
            }
        };

        audioManager.requestAudioFocus(audioFocusChangeListener,
                AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);

        Uri videoUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.omlet_ru_video);
        videoView.setVideoURI(videoUri);

        MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(videoView);
        videoView.setMediaController(mediaController);

        videoView.setOnCompletionListener(mp -> {
            audioManager.abandonAudioFocus(audioFocusChangeListener);
        });

        videoView.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (videoView != null) {
            videoView.stopPlayback();
        }
        if (audioManager != null && audioFocusChangeListener != null) {
            audioManager.abandonAudioFocus(audioFocusChangeListener);
        }
    }

    private void applySavedLanguage() {
        String languageCode = getSharedPreferences("settings", MODE_PRIVATE)
                .getString("app_language", "en");

        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);

        Resources resources = getResources();
        Configuration config = resources.getConfiguration();
        config.setLocale(locale);
        resources.updateConfiguration(config, resources.getDisplayMetrics());
    }
}