package com.example.myproject;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class Optika1Activity extends AppCompatActivity {
    OpticsCanvasView opticsCanvas;
    TextView textAngleValue, textAngleValue2;
    ImageView flashlight, protractor;
    SeekBar seekBarAngle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.optika_1);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);

        bottomNav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.nav_home) {
                    finish();
                    return true;
                } else if (id == R.id.nav_info) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(Optika1Activity.this);
                    builder.setTitle("О лабораторной работе")
                            .setMessage("В данной лабораторной работе пользователь наглядно может увидеть зависимость угла отражения от угла падения (всегда равны между собой). Изменение угла падения луча осуществляется ползунком.Для удобства внизу экрана выводятся их точные значения.")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.dismiss();
                                }
                            });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                    return true;
                } else if (id == R.id.nav_profile) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(Optika1Activity.this);
                    builder.setTitle("Об авторе")
                            .setMessage("Автор проекта: Степашин Андрей")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.dismiss();
                                }
                            });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                    return true;
                }
                return false;
            }
        });
        opticsCanvas = findViewById(R.id.opticsCanvas);
        textAngleValue = findViewById(R.id.textAngleValue);
        textAngleValue2 = findViewById(R.id.textAngleValue2);
        flashlight = findViewById(R.id.flashlight);
        protractor = findViewById(R.id.protractor);
        seekBarAngle = findViewById(R.id.seekBarAngle);
        flashlight.post(() -> {
            // Центр транспортира в % от lab_container: X=0.5, Y=0.75
            float containerWidth = ((android.view.View)flashlight.getParent()).getWidth();
            float containerHeight = ((android.view.View)flashlight.getParent()).getHeight();

            float targetX = containerWidth * 0.5f;
            float targetY = containerHeight * 0.75f;

            // Переводим абсолютные координаты контейнера в относительные координаты самого ImageView
            float pivotX = targetX - flashlight.getX();
            float pivotY = targetY - flashlight.getY();

            flashlight.setPivotX(pivotX);
            flashlight.setPivotY(pivotY);
        });

        // 2. Настройка SeekBar
        seekBarAngle.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // progress - это и есть наш угол в градусах (0-90)

                // Обновляем текст
                textAngleValue.setText("Угол падения: " + progress + "°");
                textAngleValue2.setText("Угол отражения: " + progress + "°");
                // Поворачиваем фонарик
                flashlight.setRotation(progress);

                // Передаем угол в Canvas для рисования лучей
                opticsCanvas.setAngle((float) progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        // Устанавливаем начальное состояние
        seekBarAngle.setProgress(30); // Например, начать с 30 градусов
    }
}