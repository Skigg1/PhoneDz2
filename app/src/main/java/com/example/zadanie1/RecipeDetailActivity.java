package com.example.zadanie1;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.TextView;
import java.util.Locale;
import android.media.MediaPlayer;
import android.widget.Button;
import android.widget.Toast;

public class RecipeDetailActivity extends AppCompatActivity {
    private Button btnPlayVoice;
    private MediaPlayer voicePlayer;
    private TextView tvName, tvIngredients, tvSteps;
    private RecipeTimer timer1, timer2, timer3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ПРИМЕНЯЕМ СОХРАНЕННЫЙ ЯЗЫК ПЕРЕД setContentView
        applySavedLanguage();

        setContentView(R.layout.activity_recipe_detail);

        tvName = findViewById(R.id.tvDetailName);
        tvIngredients = findViewById(R.id.tvDetailIngredients);
        tvSteps = findViewById(R.id.tvDetailSteps);

        // ПЕРВЫЙ intent - для получения данных
        Intent intent = getIntent();
        int nameId = intent.getIntExtra("name_id", 0);
        int ingredientsId = intent.getIntExtra("ingredients_id", 0);
        int stepsId = intent.getIntExtra("steps_id", 0);

        tvName.setText(nameId);
        tvIngredients.setText(ingredientsId);
        tvSteps.setText(stepsId);

        btnPlayVoice = findViewById(R.id.btnPlayRecipeVoice);
        btnPlayVoice.setOnClickListener(v -> playRecipeVoice());

        Button btnWatchVideo = findViewById(R.id.btnWatchVideo);
        btnWatchVideo.setOnClickListener(v -> {
            // ИСПРАВЛЕНО: используем другое имя - videoIntent
            Intent videoIntent = new Intent(RecipeDetailActivity.this, VideoActivity.class);
            startActivity(videoIntent);
        });

        timer1 = new RecipeTimer(this,
                findViewById(R.id.timer1_display),
                findViewById(R.id.timer1_start),
                findViewById(R.id.timer1_pause),
                findViewById(R.id.timer1_reset),
                60000, // 1 минута
                R.raw.timer1_finish_sound); // ID вашего звука

// Таймер 2: Жарить омлет (4 минуты = 240000 мс)
        timer2 = new RecipeTimer(this,
                findViewById(R.id.timer2_display),
                findViewById(R.id.timer2_start),
                findViewById(R.id.timer2_pause),
                findViewById(R.id.timer2_reset),
                240000, // 4 минуты
                R.raw.timer2_finish_sound); // ID вашего звука

// Таймер 3: Настоять чай (3 минуты = 180000 мс)
        timer3 = new RecipeTimer(this,
                findViewById(R.id.timer3_display),
                findViewById(R.id.timer3_start),
                findViewById(R.id.timer3_pause),
                findViewById(R.id.timer3_reset),
                180000, // 3 минуты
                R.raw.timer3_finish_sound); // ID вашего звука

// Кнопка для настройки таймера 3
        Button btnSetTimer3 = findViewById(R.id.btn_set_timer3);
        btnSetTimer3.setOnClickListener(v -> showTimer3SettingsDialog());

    }

    private void showTimer3SettingsDialog() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Настройка времени");

        // Создаем поле ввода
        final android.widget.EditText input = new android.widget.EditText(this);
        input.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        input.setHint("Время в минутах");
        builder.setView(input);

        builder.setPositiveButton("OK", (dialog, which) -> {
            String value = input.getText().toString();
            if (!value.isEmpty()) {
                try {
                    long minutes = Long.parseLong(value);
                    long newTime = minutes * 60 * 1000; // Конвертируем в миллисекунды
                    // Устанавливаем новое время для таймера 3, только если он не запущен
                    if (!timer3.isRunning()) {
                        timer3.setNewTime(newTime);
                        // Также обновляем текст на дисплее, если нужно
                        ((TextView)findViewById(R.id.timer3_display)).setText(
                                String.format(Locale.getDefault(), "%02d:00", minutes));
                    } else {
                        android.widget.Toast.makeText(this,
                                "Остановите таймер перед настройкой",
                                android.widget.Toast.LENGTH_SHORT).show();
                    }
                } catch (NumberFormatException e) {
                    android.widget.Toast.makeText(this, "Неверный формат", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton("Отмена", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void playRecipeVoice() {
        String languageCode = getSharedPreferences("settings", MODE_PRIVATE)
                .getString("app_language", "en");

        int voiceResId;
        switch (languageCode) {
            case "ru":
                voiceResId = R.raw.omlet_ru;
                break;
            case "de":
                voiceResId = R.raw.omlet_de;
                break;
            default:
                voiceResId = R.raw.omlet_en;
                break;
        }

        // Используем обновленный SoundManager
        SoundManager.playSound(this, voiceResId);


        // Останавливаем предыдущее воспроизведение
        if (voicePlayer != null) {
            voicePlayer.release();
        }

        // Воспроизводим
        voicePlayer = MediaPlayer.create(this, voiceResId);
        voicePlayer.setOnCompletionListener(mp -> {
            mp.release();
            voicePlayer = null;
        });
        voicePlayer.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (voicePlayer != null) {
            voicePlayer.release();
            voicePlayer = null;
        }
        SoundManager.stopSound(); // Останавливаем фоновые звуки
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