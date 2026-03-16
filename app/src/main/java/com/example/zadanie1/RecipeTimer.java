package com.example.zadanie1;

import android.content.Context;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class RecipeTimer {

    private static final String TAG = "RecipeTimer";
    private final Context context;
    private final TextView timeDisplay;
    private final Button startButton, pauseButton, resetButton;
    private final int finishSoundResId; // ID звука для этого таймера

    private CountDownTimer countDownTimer;
    private long timeInMillis; // Текущее оставшееся время
    private final long initialTimeInMillis; // Изначальное время
    private boolean isRunning = false;
    private boolean isPaused = false;

    public RecipeTimer(Context context, TextView timeDisplay,
                       Button startButton, Button pauseButton, Button resetButton,
                       long initialTimeInMillis, int finishSoundResId) {
        this.context = context;
        this.timeDisplay = timeDisplay;
        this.startButton = startButton;
        this.pauseButton = pauseButton;
        this.resetButton = resetButton;
        this.initialTimeInMillis = initialTimeInMillis;
        this.timeInMillis = initialTimeInMillis;
        this.finishSoundResId = finishSoundResId;

        setupButtons();
        updateDisplay();
    }

    private void setupButtons() {
        startButton.setOnClickListener(v -> startTimer());
        pauseButton.setOnClickListener(v -> pauseTimer());
        resetButton.setOnClickListener(v -> resetTimer());

        // Начальное состояние кнопок
        pauseButton.setEnabled(false);
    }

    private void startTimer() {
        // Предотвращаем двойной запуск
        if (isRunning) {
            Log.w(TAG, "Таймер уже запущен");
            return;
        }

        isRunning = true;
        isPaused = false;
        startButton.setEnabled(false);
        pauseButton.setEnabled(true);
        resetButton.setEnabled(true);

        countDownTimer = new CountDownTimer(timeInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeInMillis = millisUntilFinished;
                updateDisplay();

                // Меняем цвет на красный, если меньше минуты
                if (TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) < 1) {
                    timeDisplay.setTextColor(0xFFFF0000);
                }
            }

            @Override
            public void onFinish() {
                timeInMillis = 0;
                updateDisplay();
                isRunning = false;
                isPaused = false;
                startButton.setEnabled(true);
                pauseButton.setEnabled(false);
                resetButton.setEnabled(true);
                timeDisplay.setTextColor(0xFF000000); // Черный

                // Воспроизводим свой звук для таймера
                SoundManager.playTimerFinishSound(context, finishSoundResId);
            }
        }.start();
    }

    private void pauseTimer() {
        if (countDownTimer != null && isRunning && !isPaused) {
            countDownTimer.cancel();
            isRunning = false;
            isPaused = true;
            startButton.setEnabled(true);
            pauseButton.setEnabled(false);
            resetButton.setEnabled(true);
        }
    }

    private void resetTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        timeInMillis = initialTimeInMillis;
        isRunning = false;
        isPaused = false;
        updateDisplay();
        startButton.setEnabled(true);
        pauseButton.setEnabled(false);
        resetButton.setEnabled(true);
        timeDisplay.setTextColor(0xFF000000); // Черный
    }

    private void updateDisplay() {
        long minutes = TimeUnit.MILLISECONDS.toMinutes(timeInMillis);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(timeInMillis) -
                TimeUnit.MINUTES.toSeconds(minutes);
        timeDisplay.setText(String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds));
    }

    public void setNewTime(long newTimeInMillis) {
        if (!isRunning && !isPaused) {
            this.timeInMillis = newTimeInMillis;
            updateDisplay();
        }
    }

    public long getTimeInMillis() {
        return timeInMillis;
    }

    public boolean isRunning() {
        return isRunning;
    }
}