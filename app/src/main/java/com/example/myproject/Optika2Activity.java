package com.example.myproject;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class Optika2Activity extends AppCompatActivity {

    private ImageView flashlight;
    private OpticsRefractionCanvasView opticsCanvas;
    private SeekBar seekBarAngle;
    private TextView textAngleValue, textAngleValue2, firstmedium, secondmedium;
    private RadioGroup radioGroupFirst, radioGroupSecond;

    private float n1 = 1.0f; // По умолчанию Воздух
    private float n2 = 1.33f; // По умолчанию Вода
    private int currentAngleA = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.optika_2);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        //Нижнее меню
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.nav_home) {
                    finish();
                    return true;
                } else if (id == R.id.nav_info) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(Optika2Activity.this);
                    builder.setTitle("О лабораторной работе")
                            .setMessage("В данной лабораторной работе пользователь наглядно может изучить законы преломления света. Изменение угла падения луча осуществляется ползунком. Для удобства внизу экрана выводятся точные значения углов падения и преломления, а также правее транспортира подписываются каждые из двух сред с их абсолютным показателем преломления n. Расчеты углов падения и преломления проводятся в соответствии с законом Снеллиуса: n1*sin(a)=n2*sin(b).\n*АПП вакуума равен 1, а воздуха — примерно 1.00029. Разница между ними слишком мала, поэтому ею можно пренебречь.")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.dismiss();
                                }
                            });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                    return true;
                } else if (id == R.id.nav_profile) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(Optika2Activity.this);
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
        firstmedium = findViewById(R.id.firstmedium);
        secondmedium = findViewById(R.id.secondmedium);
        flashlight = findViewById(R.id.flashlight);
        opticsCanvas = findViewById(R.id.opticsCanvas);
        seekBarAngle = findViewById(R.id.seekBarAngle);
        textAngleValue = findViewById(R.id.textAngleValue);
        textAngleValue2 = findViewById(R.id.textAngleValue2);
        radioGroupFirst = findViewById(R.id.radioGroupfirst);
        radioGroupSecond = findViewById(R.id.radioGroupsecond);
        flashlight.post(() -> {
            float containerWidth = ((android.view.View)flashlight.getParent()).getWidth();
            float containerHeight = ((android.view.View)flashlight.getParent()).getHeight();
            float pivotX = (containerWidth * 0.5f) - flashlight.getX();
            float pivotY = (containerHeight * 0.6f) - flashlight.getY();
            flashlight.setPivotX(pivotX);
            flashlight.setPivotY(pivotY);
        });

        // Слушатели для первой радиогруппы (откуда падает свет)
        radioGroupFirst.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radioButton1) {
                n1 = 1.33f;      // Вода
                firstmedium.setText("Вода\n n=1.33");
            }
            else if (checkedId == R.id.radioButton12) {
                n1 = 1.0f; // Воздух
                firstmedium.setText("Воздух (=ваакум*)\n n=1");
            }
            else if (checkedId == R.id.radioButton13) {
                n1 = 2.42f; // Алмаз
                firstmedium.setText("Алмаз\n n=2.42");
            }
            calculateRefraction();
        });

        // Слушатели для второй радиогруппы (куда преломляется свет)
        radioGroupSecond.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radioButton2) {
                n2 = 1.33f;      // Вода
                secondmedium.setText("Вода\n n=1.33");
            }
            else if (checkedId == R.id.radioButton22) {
                n2 = 1.0f;  // Воздух
                secondmedium.setText("Воздух (=ваакум*)\n n=1");
            }
            else if (checkedId == R.id.radioButton23) {
                n2 = 2.42f; // Алмаз
                secondmedium.setText("Алмаз\n n=2.42");
            }
            calculateRefraction();
        });

        // Слушатель SeekBar
        seekBarAngle.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                currentAngleA = progress;
                flashlight.setRotation(progress); // Поворачиваем фонарик
                calculateRefraction();
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
        // Стартовое значение
        seekBarAngle.setProgress(30);
    }

    private void calculateRefraction() {
        textAngleValue.setText("Угол падения: " + currentAngleA + "°");

        // Переводим угол падения в радианы
        double angleARad = Math.toRadians(currentAngleA);

        // Закон Снеллиуса: sin(b) = sin(a) * n1 / n2
        double sinB = Math.sin(angleARad) * (n1 / n2);

        float currentAngleB;
        //Так как полное отражение происходит при sin(b) > 1:
        if (sinB <= 1.0) {
            // Преломление возможно
            double angleBRad = Math.asin(sinB);
            currentAngleB = (float) Math.toDegrees(angleBRad);
            textAngleValue2.setText("Угол преломления: " + Math.round(currentAngleB) + "°");

            // Отправляем на холст оба угла. Сигнализируем, что полного отражения нет
            if (opticsCanvas != null) {
                opticsCanvas.setAngles(currentAngleA, currentAngleB, false);
            }
        } else {
            // Эффект полного внутреннего отражения
            textAngleValue2.setText("Угол преломления: Полное отражение!");
            currentAngleB = currentAngleA; // Угол отражения равен числу градусов падения

            if (opticsCanvas != null) {
                opticsCanvas.setAngles(currentAngleA, currentAngleB, true);
            }
        }
    }
}
