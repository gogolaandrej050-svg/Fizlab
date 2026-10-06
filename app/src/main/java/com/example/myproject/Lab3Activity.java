package com.example.myproject;

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
public class Lab3Activity extends AppCompatActivity {
    EditText editR;
    SeekBar seekBar;
    ImageView arrowV_resistor, arrowV_rheostat, arrow_amp, slider;
    TextView textVoltResistor, textVoltRheostat, textAmp, textR;
    float I = 0;
    float R_rheostat = 0;
    View guidelineStart, guidelineEnd;
    float totalWidth = 0;
    boolean isLayoutReady = false;
    private void resetArrows() {
        arrowV_resistor.setRotation(-52f);
        arrowV_rheostat.setRotation(-54f);
        arrow_amp.setRotation(-56f);
        textVoltResistor.setText("Точные показания центрального вольтметра: 0.00 В");
        textVoltRheostat.setText("Точные показания вольтметра реостата: 0.00 В");
        textAmp.setText("Точные показания центрального амперметра: 0.00 А");
    }
    // Константа напряжения источника тока
    final float U_SOURCE = 6.0f;

    private void updateCircuit() {
        String strR = editR.getText().toString();
        if (!strR.isEmpty()) {
            try {
                float R_resistor = Float.parseFloat(strR);

                //Общее сопротивление цепи
                float R_total = R_resistor + R_rheostat;

                if (R_total != 0) {
                    I = U_SOURCE / R_total;
                    float U_resistor = I * R_resistor;
                    float U_rheostat_val = I * R_rheostat;
                    float arrowV_res_val = Math.min(U_resistor, 5.0f);
                    float arrowV_rheo_val = Math.min(U_rheostat_val, 5.0f);
                    float arrowAmp_val = Math.min(I, 5.0f);
                    float angleV_res = -52f + (arrowV_res_val * 20.8f);
                    float angleV_rheo = -54f + (arrowV_rheo_val * 21.6f);
                    float angleAmp = -56f + (arrowAmp_val * 22.4f);
                    arrowV_resistor.setRotation(angleV_res);
                    arrowV_rheostat.setRotation(angleV_rheo);
                    arrow_amp.setRotation(angleAmp);
                    textVoltResistor.setText(String.format(Locale.US, "Точные показания центрального вольтметра: %.2f В", U_resistor));
                    textVoltRheostat.setText(String.format(Locale.US, "Точные показания вольтметра реостата: %.2f В", U_rheostat_val));
                    textAmp.setText(String.format(Locale.US, "Точные показания амперметра: %.2f А", I));
                    textR.setText(String.format(Locale.US, "Сопротивление реостата: %.2f Ом", R_rheostat));
                }
            } catch (NumberFormatException e) {
                resetArrows();
            }
        } else {
            resetArrows();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.lab3_activity);
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
                    AlertDialog.Builder builder = new AlertDialog.Builder(Lab3Activity.this);
                    builder.setTitle("О лабораторной работе")
                            .setMessage("В данной лабораторной работе пользователь наглядно может увидеть зависимость напряжения цепи от положения ползунка реостата и изучить принцип его действия.  Погрешность измерений приборов на схеме - 2 деления амперметра/вольтметра (±0.2А и ±0.2В соответсвенно). Для удобства внизу экрана выводятся точные значения измерений. В данной лабораторной работе напряжение источника тока является величиной постоянной, а именно U=6В")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.dismiss();
                                }
                            });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                    return true;
                } else if (id == R.id.nav_profile) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(Lab3Activity.this);
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
        seekBar = findViewById(R.id.seekBarRheostat);
        seekBar.setEnabled(false);
        editR = findViewById(R.id.editR);
        editR.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().isEmpty()) {
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


        arrowV_resistor = findViewById(R.id.arrow_voltage);
        arrowV_rheostat = findViewById(R.id.arrow_voltage2);
        arrow_amp = findViewById(R.id.arrow_amp_main);
        slider = findViewById(R.id.rheostat_slider);

        textVoltResistor = findViewById(R.id.textVolt);
        textVoltRheostat = findViewById(R.id.textVolt2);
        textAmp = findViewById(R.id.textAmp);
        textR = findViewById(R.id.textR);
        guidelineStart = findViewById(R.id.guidelineRheostat_Start);
        guidelineEnd = findViewById(R.id.guidelineRheostat_End);

        arrowV_resistor.post(() -> {
            arrowV_resistor.setPivotX(arrowV_resistor.getWidth() / 2f);
            arrowV_resistor.setPivotY(arrowV_resistor.getHeight());
        });
        arrowV_rheostat.post(() -> {
            arrowV_rheostat.setPivotX(arrowV_rheostat.getWidth() / 2f);
            arrowV_rheostat.setPivotY(arrowV_rheostat.getHeight());
        });
        arrow_amp.post(() -> {
            arrow_amp.setPivotX(arrow_amp.getWidth() / 2f);
            arrow_amp.setPivotY(arrow_amp.getHeight());
        });

        getWindow().getDecorView().post(() -> {
            if (guidelineStart != null && guidelineEnd != null) {
                float startX = guidelineStart.getX();
                float endX = guidelineEnd.getX();
                totalWidth = endX - startX;
                isLayoutReady = true;
            }
        });

        resetArrows();

        editR.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateCircuit();
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                R_rheostat = (progress / 100f) * 5f;
                updateCircuit();

                if (isLayoutReady && totalWidth > 0 && slider != null) {
                    float translationX = totalWidth * (progress / 100f);
                    slider.setTranslationX(translationX);
                }
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }
}
