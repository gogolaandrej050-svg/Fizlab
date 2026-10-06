package com.example.myproject;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class OpticsRefractionCanvasView extends View {

    private Paint paintBeam;
    private Paint paintNormal;
    private float angleA = 0f;
    private float angleB = 0f;
    private boolean isTotalInternalReflection = false;

    public OpticsRefractionCanvasView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        paintBeam = new Paint();
        paintBeam.setColor(Color.RED);
        paintBeam.setStrokeWidth(8f);
        paintBeam.setAntiAlias(true);
        paintBeam.setShadowLayer(10, 0, 0, Color.RED); // Эффект свечения лазера

        paintNormal = new Paint();
        paintNormal.setColor(Color.GRAY);
        paintNormal.setStrokeWidth(3f);
        paintNormal.setPathEffect(new DashPathEffect(new float[]{10, 10}, 0));
    }

    public void setAngles(float angleA, float angleB, boolean isTotalReflection) {
        this.angleA = angleA;
        this.angleB = angleB;
        this.isTotalInternalReflection = isTotalReflection;
        invalidate(); // Перерисовать экран
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Установленные вами координаты центра транспортира
        float centerX = getWidth() / 2f;
        float centerY = getHeight() * 0.6f;

        // Длина лучей
        float distanceToFlashlight = getHeight() * 0.255f;
        float infiniteLength = 2000f;

        // ==========================================
        // 1. ПАДАЮЩИЙ ЛУЧ (ВЕРХНЯЯ СРЕДА)
        // ==========================================
        double radA = Math.toRadians(angleA);
        // Фонарик движется по верхней дуге
        float incidentStartX = centerX + (float) (distanceToFlashlight * Math.sin(radA));
        float incidentStartY = centerY - (float) (distanceToFlashlight * Math.cos(radA));
        canvas.drawLine(incidentStartX, incidentStartY, centerX, centerY, paintBeam);

        // ==========================================
        // 2. ВЫХОДЯЩИЙ ЛУЧ (НИЖНЯЯ СРЕДА ИЛИ ОТРАЖЕНИЕ)
        // ==========================================
        double radB = Math.toRadians(angleB);
        float exitEndX;
        float exitEndY;

        if (isTotalInternalReflection) {
            // Свет зеркально отражается назад вверх в первую среду
            exitEndX = centerX - (float) (infiniteLength * Math.sin(radB));
            exitEndY = centerY - (float) (infiniteLength * Math.cos(radB));
        } else {
            // Обычное преломление: свет уходит вниз во вторую среду
            // Меняем знаки для Y, так как луч идет вниз от центра
            exitEndX = centerX - (float) (infiniteLength * Math.sin(radB));
            exitEndY = centerY + (float) (infiniteLength * Math.cos(radB));
        }
        canvas.drawLine(centerX, centerY, exitEndX, exitEndY, paintBeam);

        // ==========================================
        // 3. ПЕРПЕНДИКУЛЯР К ГРАНИЦЕ СРЕД (НОРМАЛЬ)
        // ==========================================
        // Рисуем пунктирную линию через весь транспортир сверху вниз
        canvas.drawLine(centerX, centerY - 500f, centerX, centerY + 500f, paintNormal);
    }
}