package com.example.myproject;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

public class OpticsCanvasView extends View {

    private Paint paintBeam;
    private float currentAngle = 0f; // Угол
    private float centerX = 0f;
    private float centerY = 0f;
    // Длина лучей
    private float beamLength = 1000f;
    public OpticsCanvasView(Context context) {
        super(context);
        init();
    }
    public OpticsCanvasView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }
    private void init() {
        paintBeam = new Paint();
        paintBeam.setColor(Color.RED); // Цвет лазерного луча
        paintBeam.setStrokeWidth(8f); // Толщина луча
        paintBeam.setStyle(Paint.Style.STROKE);
        paintBeam.setAntiAlias(true); // Сглаживание
        paintBeam.setShadowLayer(10, 0, 0, Color.RED); // Эффект свечения лазера
    }
    // Метод для обновления угла
    public void setAngle(float angle) {
        this.currentAngle = angle;
        invalidate(); // Заставляет View перерисоваться (вызвать onDraw)
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //Центр транспортира
        centerX = getWidth() / 2f;
        centerY = getHeight() * 0.75f;
        //Угол в радианах
        double angleRad = Math.toRadians(currentAngle);
        // Расстояние от центра до стекла фонарика (подберите коэффициент под вашу графику)
        float distanceToFlashlight = getHeight() * 0.255f;
        // Начало луча
        float incidentStartX = centerX + (float)(distanceToFlashlight * Math.sin(angleRad));
        float incidentStartY = centerY - (float)(distanceToFlashlight * Math.cos(angleRad));

        // Рисуем Падающий луч
        canvas.drawLine(incidentStartX, incidentStartY, centerX, centerY, paintBeam);

        float infiniteLength = 2000f;

        float reflectedEndX = centerX - (float)(infiniteLength * Math.sin(angleRad));
        float reflectedEndY = centerY - (float)(infiniteLength * Math.cos(angleRad));

        // Рисуем Отраженный луч
        canvas.drawLine(centerX, centerY, reflectedEndX, reflectedEndY, paintBeam);

        Paint paintNormal = new Paint();
        paintNormal.setColor(Color.GRAY);
        paintNormal.setStrokeWidth(3f);
        paintNormal.setPathEffect(new android.graphics.DashPathEffect(new float[]{10, 10}, 0));
        canvas.drawLine(centerX, centerY - infiniteLength, centerX, centerY, paintNormal);
    }
}
