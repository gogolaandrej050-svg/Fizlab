package com.example.myproject;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioGroup;
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

public class Optika3Activity extends AppCompatActivity {

    private ImageView pen, imagePen, lens, plane;     // Изображение линзы
    private SeekBar seekBar;
    private TextView textDescription, distancetolenseText;
    private RadioGroup radioGroupLens;
    private OpticsLensCanvasView lensCanvasView;
    private boolean isConverging = true; // true = Собирающая, false = Рассеивающая
    private float manualOffsetX, manualOffsetY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.optika_3);
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
                    AlertDialog.Builder builder = new AlertDialog.Builder(Optika3Activity.this);
                    builder.setTitle("О лабораторной работе")
                            .setMessage("В данной лабораторной работе пользователь наглядно может изучить законы построения изображений в тонких линзах. Изменение расстояния от предмета до линзы осуществляется ползунком. Чуть выше него есть кнопки для выбора типа линзы. Для удобства в правой части экрана выводится описание изображения, а также чуть выше ползунка подписывается положение предмета.")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.dismiss();
                                }
                            });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                    return true;
                } else if (id == R.id.nav_profile) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(Optika3Activity.this);
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
        plane = findViewById(R.id.plane);
        pen = findViewById(R.id.pen);
        imagePen = findViewById(R.id.image);
        lens = findViewById(R.id.lens);
        seekBar = findViewById(R.id.distancetolense);
        textDescription = findViewById(R.id.BiggerOrSmallerText); // Поле для описания
        distancetolenseText = findViewById(R.id.distancetolenseText);
        radioGroupLens = findViewById(R.id.radioGroupopt3);
        lensCanvasView = findViewById(R.id.lensCanvasView);
        seekBar.setProgress(0);
        imagePen.setAlpha(0.6f);
        pen.post(() -> {
            //Изображение ручки
            imagePen.setPivotX(imagePen.getWidth() / 2f);
            imagePen.setPivotY(0);
            updateOptics(0);
        });

        // Фиксируем Pivot внизу изображения, чтобы оно не проваливалось под ось
        imagePen.post(() -> {
            imagePen.setPivotX(imagePen.getWidth() / 2f);
            imagePen.setPivotY(imagePen.getHeight());
        });
        updateOptics(0);
        //Выбор типа линзы
        radioGroupLens.setOnCheckedChangeListener((group, checkedId) -> {
            isConverging = (checkedId == R.id.radioButtonopt2); //Собирающая
            // Смена изображения линзы
            if (isConverging) {
                lens.setImageResource(R.drawable.converginglensdraw); //Собирающая
                plane.setImageResource(R.drawable.converginglens);
            } else {
                lens.setImageResource(R.drawable.diverginglensdraw); //Рассеивающая
                plane.setImageResource(R.drawable.diverginglens);
            }

            updateOptics(seekBar.getProgress());
        });


        // Настройка SeekBar на 5 делений
        seekBar.setMax(4);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                updateOptics(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        // Стартовое положение
        seekBar.setProgress(0);
    }

    private void updateRays(int position) {
        if (lensCanvasView == null) return;

        // 1. Координаты предмета
        float penTopX = pen.getX() + (pen.getWidth() / 2f);
        float penTopY = pen.getY();

        // 2. Центр линзы
        float lensCenterX = lens.getX() + (lens.getWidth() / 2f);
        float lensCenterY = plane.getY() + (plane.getHeight() / 2f);

        // 3. Расчет позиционирования для рассеивающей линзы
        if (!isConverging) {
            float dPen = lensCenterX - penTopX;
            if (dPen < 1f) dPen = 1f;

            View parent = (View) pen.getParent();
            float fDistance = parent.getWidth() * 0.175f;

            // Вычисляем физический X и масштаб
            float dImg = (dPen * fDistance) / (dPen + fDistance);
            float targetX = lensCenterX - dImg;
            float scale = dImg / dPen;

            // Устанавливаем координаты и масштаб изображения
            imagePen.setX(targetX - (imagePen.getWidth() / 3f));
            imagePen.setScaleX(scale);
            imagePen.setScaleY(scale);
        }

        // 4. Базовые координаты изображения
        float imgTopX = imagePen.getX() + (imagePen.getWidth() / 2f);
        float imgTopY = getImageTopY(position, isConverging);

        // 5. Флаги режимов
        boolean isVirtual = (!isConverging) || (position == 0);
        boolean isBetweenFand2F = isConverging && (position == 2);

        float offsetX = 0f;
        float offsetY = 0f;

        if (isConverging && position == 0) {
            float dPen = lensCenterX - penTopX;
            if (dPen < 1f) dPen = 1f;

            float dynamicShift = dPen * 0.25f;
            float newImgX = imgTopX - dynamicShift;

            imagePen.setX(newImgX - (imagePen.getWidth() / 2f));
            offsetX = -dynamicShift + 15;
            offsetY = -210f;
        }

        // 6. Передача координат в CanvasView
        lensCanvasView.CoordinatesFromActivity(
                penTopX, penTopY,
                imagePen.getX() + (imagePen.getWidth() / 2f),
                imgTopY,
                lensCenterX, lensCenterY,
                offsetX,
                offsetY,
                imagePen.getVisibility() == View.VISIBLE,
                isVirtual,
                isBetweenFand2F,
                isConverging
        );
    }

    /**
     * ТОЧНАЯ РУЧНАЯ НАСТРОЙКА Y-КООРДИНАТЫ ВЕРШИНЫ ДЛЯ КАЖДОГО ПОЛОЖЕНИЯ
     * За основу взята формула из 2F (baseY + scaledHeight)
     */
    private float getImageTopY(int position, boolean isConverging) {
        float baseY = imagePen.getY();

        if (isConverging) {
            float scaledHeight = imagePen.getHeight() * imagePen.getScaleY();
            // ================= СОБИРАЮЩАЯ ЛИНЗА =================
            switch (position) {
                case 0: // До фокуса (МНИМОЕ: Прямое)
                    return baseY - (1.2f * scaledHeight);
                case 1: // В фокусе
                    return baseY;
                case 2: // Между F и 2F (Перевернутое)
                    return baseY + (1.6f * scaledHeight);
                case 3: // В 2F (Перевернутое)
                    return baseY + (2.0f * scaledHeight);
                case 4: // За 2F (Перевернутое)
                    return baseY + (2.65f * scaledHeight);
            }
        } else {
            // ================= РАССЕИВАЮЩАЯ ЛИНЗА =================
            // Изображение прямое, Pivot зафиксирован снизу (setPivotY(getHeight()))
            // Верхняя точка верхушки ручки находится строго в baseY:
            return baseY;
        }

        return baseY;
    }
    private void updateOptics(int position) {
        View parent = (View) pen.getParent();
        if (parent == null || parent.getWidth() == 0) return;

        float parentWidth = parent.getWidth();
        float lensX = parentWidth * 0.5f;
        float fDistance = parentWidth * 0.175f; // Расстояние до фокуса F

        float penX = 0;

        switch (position) {
            case 0: // До фокуса (0.5 * F)
                penX = lensX - (fDistance * 0.5f);
                distancetolenseText.setText("До фокуса");
                break;
            case 1: // В фокусе (F)
                penX = lensX - fDistance;
                distancetolenseText.setText("В фокусе");
                break;
            case 2: // Между F и 2F (1.5 * F)
                penX = lensX - (fDistance * 1.5f);
                distancetolenseText.setText("Между F и 2F");
                break;
            case 3: // В 2F (1.95 * F)
                penX = lensX - (fDistance * 1.95f);
                distancetolenseText.setText("В 2F");
                break;
            case 4: // За 2F (2.5 * F)
                penX = lensX - (fDistance * 2.5f);
                distancetolenseText.setText("За 2F");
                break;
        }

        // Позиционируем предмет (ручку)
        pen.setX(penX - (pen.getWidth() / 2f));

        if (isConverging) {
            // ================= СОБИРАЮЩАЯ ЛИНЗА =================
            switch (position) {
                case 0: // До фокуса
                    textDescription.setText("Тип изображения:\n• Увеличенное\n• Прямое\n• Мнимое");
                    imagePen.setVisibility(View.VISIBLE);
                    imagePen.setX(penX - 60);
                    imagePen.setScaleY(1.6f);
                    imagePen.setScaleX(1.6f);
                    imagePen.setRotation(0);
                    break;

                case 1: // В фокусе
                    textDescription.setText("Тип изображения:\n• Изображения нет\n(лучи параллельны)");
                    imagePen.setVisibility(View.INVISIBLE);
                    break;

                case 2: // Между F и 2F
                    textDescription.setText("Тип изображения:\n• Увеличенное\n• Перевернутое\n• Действительное");
                    imagePen.setVisibility(View.VISIBLE);
                    imagePen.setX(lensX + (fDistance * 2.2f));
                    imagePen.setScaleY(1.6f);
                    imagePen.setScaleX(1.6f);
                    imagePen.setRotation(180);
                    break;

                case 3: // В 2F
                    textDescription.setText("Тип изображения:\n• Равное (1:1)\n• Перевернутое\n• Действительное");
                    imagePen.setVisibility(View.VISIBLE);
                    imagePen.setX(lensX + (fDistance * 1.825f));
                    imagePen.setScaleY(1.0f);
                    imagePen.setScaleX(1.0f);
                    imagePen.setRotation(180);
                    break;

                case 4: // За 2F
                    textDescription.setText("Тип изображения:\n• Уменьшенное\n• Перевернутое\n• Действительное");
                    imagePen.setVisibility(View.VISIBLE);
                    imagePen.setX(lensX + (fDistance * 1.3f));
                    imagePen.setScaleY(0.6f);
                    imagePen.setScaleX(0.6f);
                    imagePen.setRotation(180);
                    break;
            }
        } else {
            // ================= РАССЕИВАЮЩАЯ ЛИНЗА =================
            textDescription.setText("Тип изображения:\n• Уменьшенное\n• Прямое\n• Мнимое");
            imagePen.setVisibility(View.VISIBLE);
            imagePen.setRotation(0); // Всегда прямое
            // Координаты и масштаб будут точно рассчитаны в updateRays()
        }

        updateRays(position); // Обновление лучей и трансформаций
    }
}
