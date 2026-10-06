package com.example.myproject;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
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

import java.util.Locale;

public class Lab2Activity extends AppCompatActivity {
    EditText editR1, editR2;
    View guidelineStart, guidelineEnd;
    float totalWidth = 0;
    boolean isLayoutReady = false;
    float I = 1; float R1 = 1; float R2 = 1; float R_rheostat = 0;
    float U1, U2, U_total;

    SeekBar seekBar;
    ImageView arrowV1, arrowV2, arrowV_main, arrow_amp, slider;
    TextView textVolt1, textVolt2, textVoltMain, textAmp, textR;
    //метод ресета стрелок при ошибке или недостатке данных
    private void resetArrows() {
        arrowV1.setRotation(-45);
        arrowV2.setRotation(-45);
        arrowV_main.setRotation(-49);
        arrow_amp.setRotation(-51);
        textVolt1.setText("Точные показания вольтметра 1: 0.00 В");
        textVolt2.setText("Точные показания вольтметра 2: 0.00 В");
        textVoltMain.setText("Точные показания общего вольтметра: 0.00 В");
        textAmp.setText("Точные показания амперметра: 0.00 А");
    }
    //Напряжение источника тока постоянно
    final float U_CONST = 12;
    //метод обновления всех стрелок
    private void updateCircuit() {
        String strR1 = editR1.getText().toString();
        String strR2 = editR2.getText().toString();
        if (!strR1.isEmpty() && !strR2.isEmpty()) {
            try {
                R1 = Float.parseFloat(strR1);
                R2 = Float.parseFloat(strR2);
                float R_total = R1 + R2 + R_rheostat;
                if (R_total != 0) {
                    // Вычисляем физику
                    I = U_CONST / R_total;
                    U1 = I * R1;
                    U2 = I * R2;
                    U_total = U1 + U2;

                    float arrowU1_val = Math.min(U1, 5.0f);
                    float arrowU2_val = Math.min(U2, 5.0f);
                    float arrowU_total_val = Math.min(U_total, 5.0f);
                    float arrowI_val = Math.min(I, 5.0f);

// 2. ИНДИВИДУАЛЬНЫЕ ФОРМУЛЫ ДЛЯ КАЖДОГО ПРИБОРА
                    float angleV1 = -45f + (arrowU1_val * 18f); // Маленький вольтметр 1
                    float angleV2 = -45f + (arrowU2_val * 18f); // Маленький вольтметр 2

// Общий вольтметр (увеличиваем шаг с 18 до 19.5, чтобы дотянуть до 5В)
                    float angleVMain = -45f + (arrowU_total_val * 19.25f);

// Общий амперметр (стартует с -51 и имеет более крутой шаг, например, 20.2)
                    float angleAmp = -51f + (arrowI_val * 20.2f);

// 3. Поворачиваем стрелки
                    arrowV1.setRotation(angleV1);
                    arrowV2.setRotation(angleV2);
                    arrowV_main.setRotation(angleVMain);
                    arrow_amp.setRotation(angleAmp);

// 4. Выводим ТОЧНЫЕ данные в текстовые поля (они растут без ограничений)
                    textVolt1.setText(String.format(Locale.US, "Точные показания вольтметра 1: %.2f В", U1));
                    textVolt2.setText(String.format(Locale.US, "Точные показания вольтметра 2: %.2f В", U2));
                    textVoltMain.setText(String.format(Locale.US, "Общее напряжение U: %.2f В", U_total));
                    textAmp.setText(String.format(Locale.US, "Точные показания амперметра: %.2f А", I));
                    textR.setText(String.format(Locale.US, "Сопротивление реостата R: %.2f Ом", R_rheostat));
                }
            } catch (NumberFormatException e) {
                resetArrows();
            }
        } else {
            resetArrows();
        }
    }
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.lab2_activity);
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
                    // Код для открытия Инфо
                    AlertDialog.Builder builder = new AlertDialog.Builder(Lab2Activity.this);
                    builder.setTitle("О лабораторной работе")
                            .setMessage("В данной лабораторной работе пользователь наглядно может увидеть работу законов последовательного соединений проводников, значения напряжения на каждом отдельном резисторе и на обоих в целом, зависимость напряжения цепи от положения ползунка реостата.  Погрешность измерений приборов на схеме - 2 деления амперметра/вольтметра (±0.2А и ±0.2В соответсвенно). Для удобства внизу экрана выводятся точные значения измерений. В данной лабораторной работе напряжение источника тока является величиной постоянной, а именно U=12В")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // Здесь код, который выполнится при нажатии на ОК
                                    dialog.dismiss();
                                }
                            });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                    return true;
                } else if (id == R.id.nav_profile) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(Lab2Activity.this);
                    builder.setTitle("Об авторе")
                            .setMessage("Автор проекта: Степашин Андрей")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // Здесь код, который выполнится при нажатии на ОК
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
        seekBar = findViewById(R.id.seekBarRheostat);
        seekBar.setEnabled(false);

        editR1 = findViewById(R.id.editR);
        editR2 = findViewById(R.id.editR2);

        editR1.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().isEmpty() && !editR2.getText().toString().isEmpty()) {
                    seekBar.setEnabled(true);
                }
                else {
                    seekBar.setEnabled(false);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
        });

        editR2.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().isEmpty() && !editR1.getText().toString().isEmpty()) {
                    seekBar.setEnabled(true);
                }
                else {
                    seekBar.setEnabled(false);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
        });

        arrowV1 = findViewById(R.id.arrow_voltage);
        arrowV2 = findViewById(R.id.arrow_voltage2);
        arrowV_main = findViewById(R.id.arrow_volt_main);
        arrow_amp = findViewById(R.id.arrow_amp_main);
        textVolt1 = findViewById(R.id.textVolt1);
        textVolt2 = findViewById(R.id.textVolt2);
        textVoltMain = findViewById(R.id.textVoltMain);
        textAmp = findViewById(R.id.textamp);
        textR = findViewById(R.id.textR);
        slider = findViewById(R.id.rheostat_slider);
        guidelineStart = findViewById(R.id.guidelineRheostat_Start);
        guidelineEnd = findViewById(R.id.guidelineRheostat_End);
        arrowV1.post(() -> {
            arrowV1.setPivotX(arrowV1.getWidth() / 2f);
            arrowV1.setPivotY(arrowV1.getHeight());
        });
        arrowV2.post(() -> {
            arrowV2.setPivotX(arrowV2.getWidth() / 2f);
            arrowV2.setPivotY(arrowV2.getHeight());
        });
        arrowV_main.post(() -> {
            arrowV_main.setPivotX(arrowV_main.getWidth() / 2f);
            arrowV_main.setPivotY(arrowV_main.getHeight());
        });
        arrow_amp.post(() -> {
            arrow_amp.setPivotX(arrow_amp.getWidth() / 2f);
            arrow_amp.setPivotY(arrow_amp.getHeight());
        });
        resetArrows();
        getWindow().getDecorView().post(() -> {
            float startX = guidelineStart.getX();
            float endX = guidelineEnd.getX();
            totalWidth = endX - startX;
            isLayoutReady = true;
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                String strR1 = editR1.getText().toString();
                String strR2 = editR2.getText().toString();
                if (!strR1.isEmpty() && !strR2.isEmpty()) {
                    R_rheostat = (progress / 100f) * 50;
                    updateCircuit();
                    if (isLayoutReady && totalWidth > 0 && slider != null) {
                        float translationX = totalWidth * (progress / 100f);
                        slider.setTranslationX(translationX);
                    }
                }
                else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(Lab2Activity.this);
                    builder.setTitle("Недостаточно данных")
                            .setMessage("Перед началом рассчетов введите значения сопротивления первого и второго резисторов")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.dismiss();
                                }
                            });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }
}
