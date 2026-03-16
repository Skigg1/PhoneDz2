package com.example.zadanie1;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Toast;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private RadioGroup radioGroupLanguages;
    private Button btnGoToRecipes;
    private Button btnApplyLanguage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SoundManager.playSoundWithDuration(this, R.raw.app_start_sound, 5000);

        // Применяем сохраненный язык при запуске
        applySavedLanguage();

        setContentView(R.layout.activity_main);

        // Инициализация
        radioGroupLanguages = findViewById(R.id.radioGroupLanguages);
        btnGoToRecipes = findViewById(R.id.btnGoToRecipes);
        btnApplyLanguage = findViewById(R.id.btnApplyLanguage);

        // Устанавливаем выбранную радио-кнопку
        setSelectedRadioButton();

        // Кнопка применения языка
        btnApplyLanguage.setOnClickListener(v -> {

            SoundManager.playSound(this, R.raw.transition_sound); // Короткий звук
            Intent intent = new Intent(MainActivity.this, RecipeListActivity.class);
            startActivity(intent);

            // Получаем выбранный язык
            String selectedLanguage = getSelectedLanguage();

            // Сохраняем язык
            saveLanguage(selectedLanguage);

            // Применяем язык принудительно
            boolean success = setAppLocale(selectedLanguage);

            if (success) {
                Toast.makeText(MainActivity.this,
                        "Язык изменен на: " + selectedLanguage,
                        Toast.LENGTH_SHORT).show();

                // Перезапускаем активность с задержкой
                new Handler().postDelayed(() -> {
                    recreate();
                }, 300);
            } else {
                Toast.makeText(MainActivity.this,
                        "Ошибка при смене языка",
                        Toast.LENGTH_SHORT).show();
            }
        });

        // Переход к рецептам
        btnGoToRecipes.setOnClickListener(v -> {
            SoundManager.playSound(this, R.raw.transition_sound);
            Intent intent = new Intent(MainActivity.this, RecipeListActivity.class);
            startActivity(intent);
        });
    }


    private boolean setAppLocale(String languageCode) {
        try {
            Locale locale = new Locale(languageCode);
            Locale.setDefault(locale);

            Resources resources = getResources();
            Configuration config = resources.getConfiguration();
            config.setLocale(locale);

            resources.updateConfiguration(config, resources.getDisplayMetrics());

            // Также обновляем через LocaleHelper для совместимости
            LocaleHelper.setLocale(this, languageCode);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    private void applySavedLanguage() {
        String savedLanguage = getSavedLanguage();
        if (!savedLanguage.isEmpty()) {
            setAppLocale(savedLanguage);
        }
    }


    private String getSelectedLanguage() {
        int checkedId = radioGroupLanguages.getCheckedRadioButtonId();

        if (checkedId == R.id.radioRussian) {
            return "ru";
        } else if (checkedId == R.id.radioGerman) {
            return "de";
        } else {
            return "en";
        }
    }


    private void saveLanguage(String languageCode) {
        getSharedPreferences("settings", MODE_PRIVATE)
                .edit()
                .putString("app_language", languageCode)
                .apply();
    }


    private String getSavedLanguage() {
        return getSharedPreferences("settings", MODE_PRIVATE)
                .getString("app_language", "en");
    }


    private void setSelectedRadioButton() {
        String savedLanguage = getSavedLanguage();

        if (savedLanguage.equals("ru")) {
            radioGroupLanguages.check(R.id.radioRussian);
        } else if (savedLanguage.equals("de")) {
            radioGroupLanguages.check(R.id.radioGerman);
        } else {
            radioGroupLanguages.check(R.id.radioEnglish);
        }
    }
}