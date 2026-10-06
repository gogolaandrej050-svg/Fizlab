package com.example.myproject;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
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

public class Lab1Activity extends AppCompatActivity {
    float I = 1;
    float Ro = 0.4f;
    float L = 1;
    float S = 1;
    float r = 1;
    float U = I*r;
    TextView textVolt, textAmp;
    ImageView arrowV;
    ImageView arrowA;
    EditText inputS;
    EditText inputL;
    EditText inputI;
    //логика стрелки вольтметра
    private void updateArrowV() {
        String strL = inputL.getText().toString();
        String strS = inputS.getText().toString();
        String strI = inputI.getText().toString();
        if (!strL.isEmpty() && !strS.isEmpty() && !strI.isEmpty()) {
            try {
                L = Float.parseFloat(strL);
                S = Float.parseFloat(strS);
                I = Float.parseFloat(strI);
                if (S != 0) {
                    r = (Ro * L) / S;
                    U = I * r;
                    float arrowU_val = Math.min(U, 5.0f);
                    float angle = -45f + (arrowU_val * 18f);
                    arrowV.setRotation(angle);
                    textVolt.setText(String.format("Точные показания вольтметра: %.2f В", U));
                }
            } catch (NumberFormatException e) { }
        } else {
            arrowV.setRotation(-45);
            textVolt.setText("Точные показания вольтметра: 0.00 В");
        }
    }
    private void updateArrowA() {
        String strL = inputL.getText().toString();
        String strS = inputS.getText().toString();
        String strI = inputI.getText().toString();
        if (!strL.isEmpty() && !strS.isEmpty() && !strI.isEmpty()) {
            try {
                L = Float.parseFloat(strL);
                S = Float.parseFloat(strS);
                I = Float.parseFloat(strI);
                if (S != 0) {
                    r = (Ro * L) / S;
                    U = I * r;
                    float arrowI_val = Math.min(I, 5.0f);
                    float angle = -42f + (arrowI_val * 18f);
                    arrowA.setRotation(angle);
                    textAmp.setText(String.format("Точные показания амперметра: %.2f А", I));
                }
            } catch (NumberFormatException e) { }
        } else {
            arrowA.setRotation(-42);
            textAmp.setText("Точные показания амперметра: 0.00 А");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.lab1_activity);
        inputL = findViewById(R.id.editR);
        arrowV = findViewById(R.id.arrow_voltage);
        arrowA = findViewById(R.id.arrow_amperage);
        inputS = findViewById(R.id.editR2);
        inputI = findViewById(R.id.editI);
        textVolt = findViewById(R.id.textVolt);
        textAmp = findViewById(R.id.textAmp);
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
                    AlertDialog.Builder builder = new AlertDialog.Builder(Lab1Activity.this);
                    builder.setTitle("О лабораторной работе")
                            .setMessage("В данной лабораторной работе пользователь наглядно может увидеть зависимость сопротивления проводника от его длины, площади поперечного сечения и материала. Погрешность измерений на схеме - 1 деление амперметра/вольтметра. Для удобства внизу экрана выводятся точные значения измерений.")
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
                    AlertDialog.Builder builder = new AlertDialog.Builder(Lab1Activity.this);
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
        arrowV.post(() -> {
            arrowV.setPivotX(arrowV.getWidth() / 2f);
            arrowV.setPivotY(arrowV.getHeight());
        });
        arrowA.post(() -> {
            arrowA.setPivotX(arrowA.getWidth() / 2f);
            arrowA.setPivotY(arrowA.getHeight());
        });
        arrowV.setRotation(-45);
        arrowA.setRotation(-45);
        inputL.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence l, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence l, int start, int before, int count) {
                updateArrowV();
                updateArrowA();
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
        inputS.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateArrowV();
                updateArrowA();
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
        inputI.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence l, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence l, int start, int before, int count) {
                updateArrowV();
                updateArrowA();
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
        RadioGroup radioGroup = findViewById(R.id.radioGroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.radioNicel) {
                    Ro = 0.4f;    // Удельное эл. сопротивление никелина
                } else if (checkedId == R.id.radioNichrome) {
                    Ro = 1.1f;     // Удельное эл. сопротивление нихрома
                } else if (checkedId == R.id.radioFehral) {
                    Ro = 1.2f;     // Удельное эл. сопротивление фехраля
                }
                //апдейт отклонения стрелок на экране
                updateArrowV();
                updateArrowA();
            }
        });
    }
}
