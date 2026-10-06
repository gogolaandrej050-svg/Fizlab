package com.example.myproject; // Замените на ваш package

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class OpticsLensCanvasView extends View {

    // Кисти для отрисовки лучей
    private Paint paintSolidRed;
    private Paint paintDashedRed;
    private Paint paintSolidGreen;
    private Paint paintDashedGreen;
    private Paint paintSolidBlue;
    private Paint paintDashedBlue;

    // Входные координаты и флаги
    private float penTopX, penTopY;
    private float imageTopX, imageTopY;
    private float lensCenterX, lensCenterY;
    private float manualOffsetX, manualOffsetY;

    private boolean isImageVisible = false;
    private boolean isVirtual = false;
    private boolean isBetweenFand2F = false;
    private boolean isConverging = true;

    public OpticsLensCanvasView(Context context) {
        super(context);
        initPaints();
    }

    public OpticsLensCanvasView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initPaints();
    }

    public OpticsLensCanvasView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initPaints();
    }

    private void initPaints() {
        // Красный сплошной (Падающий / Преломленный)
        paintSolidRed = new Paint();
        paintSolidRed.setColor(Color.parseColor("#E53935"));
        paintSolidRed.setStrokeWidth(4f);
        paintSolidRed.setStyle(Paint.Style.STROKE);
        paintSolidRed.setAntiAlias(true);

        // Красный пунктирный (Продолжение мнимого луча)
        paintDashedRed = new Paint(paintSolidRed);
        paintDashedRed.setPathEffect(new DashPathEffect(new float[]{12f, 12f}, 0));

        // Зеленый сплошной (Центральный луч)
        paintSolidGreen = new Paint();
        paintSolidGreen.setColor(Color.parseColor("#4CAF50"));
        paintSolidGreen.setStrokeWidth(4f);
        paintSolidGreen.setStyle(Paint.Style.STROKE);
        paintSolidGreen.setAntiAlias(true);

        paintDashedGreen = new Paint(paintSolidGreen);
        paintDashedGreen.setPathEffect(new DashPathEffect(new float[]{12f, 12f}, 0));

        // Синий сплошной (Вспомогательный луч)
        paintSolidBlue = new Paint();
        paintSolidBlue.setColor(Color.parseColor("#1E88E5"));
        paintSolidBlue.setStrokeWidth(4f);
        paintSolidBlue.setStyle(Paint.Style.STROKE);
        paintSolidBlue.setAntiAlias(true);

        // Синий пунктирный (Горизонтальное мнимое продолжение)
        paintDashedBlue = new Paint(paintSolidBlue);
        paintDashedBlue.setPathEffect(new DashPathEffect(new float[]{12f, 12f}, 0));
    }

    /**
     * Передача координат и состояний из Activity
     */
    public void CoordinatesFromActivity(
            float penX, float penY,
            float imgX, float imgY,
            float lensX, float lensY,
            float offsetX, float offsetY,
            boolean isVisible,
            boolean virtual,
            boolean betweenFand2F,
            boolean converging
    ) {
        this.penTopX = penX;
        this.penTopY = penY;
        this.imageTopX = imgX;
        this.imageTopY = imgY;
        this.lensCenterX = lensX;
        this.lensCenterY = lensY;
        this.manualOffsetX = offsetX;
        this.manualOffsetY = offsetY;

        this.isImageVisible = isVisible;
        this.isVirtual = virtual;
        this.isBetweenFand2F = betweenFand2F;
        this.isConverging = converging;

        invalidate(); // Перерисовка холста
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Защита от нулевых координат или скрытого изображения
        if (!isImageVisible || lensCenterX == 0 || getWidth() == 0) return;

        if (isConverging) {
            // =========================================================================
            // СОБИРАЮЩАЯ ЛИНЗА (ПОЛНОСТЬЮ РАБОЧИЙ КОД)
            // =========================================================================
            if (isVirtual) {
                // ---------------------------------------------------------------------
                // 1. МНИМОЕ ИЗОБРАЖЕНИЕ (Положение предмета: "До фокуса")
                // ---------------------------------------------------------------------
                float dPen = 2.1f * (lensCenterX - penTopX);
                if (dPen < 1f) dPen = 1f;

                float rawTargetX = imageTopX;
                if (rawTargetX >= penTopX - 15f) {
                    rawTargetX = penTopX - 15f;
                }

                float dImg = lensCenterX - rawTargetX;
                float scale = dImg / dPen;

                float maxScale = 1.45f;
                if (scale > maxScale) {
                    scale = maxScale;
                    rawTargetX = lensCenterX - (dPen * scale);
                }

                float targetX = rawTargetX + manualOffsetX;
                float penHeight = lensCenterY - penTopY;
                float targetY = lensCenterY - (penHeight * scale) + manualOffsetY;

                // 1. СИНИЙ ЛУЧ
                canvas.drawLine(getWidth(), targetY, lensCenterX, targetY, paintSolidBlue);
                canvas.drawLine(lensCenterX, targetY, penTopX, penTopY, paintSolidBlue);
                canvas.drawLine(lensCenterX, targetY, 0, targetY, paintDashedBlue);

                // 2. ЗЕЛЕНЫЙ ЛУЧ
                float dxGreen = lensCenterX - penTopX;
                if (Math.abs(dxGreen) > 0.001f) {
                    float slopeGreen = (lensCenterY - penTopY) / dxGreen;
                    float greenRightY = lensCenterY + (getWidth() - lensCenterX) * slopeGreen;

                    canvas.drawLine(getWidth(), greenRightY, penTopX, penTopY, paintSolidGreen);
                    canvas.drawLine(penTopX, penTopY, targetX, targetY, paintDashedGreen);

                    float greenLeftY = targetY - (targetX * slopeGreen);
                    canvas.drawLine(targetX, targetY, 0, greenLeftY, paintDashedGreen);
                }

                // 3. КРАСНЫЙ ЛУЧ
                float focusRightX = lensCenterX + dPen;
                float dxRedFocus = focusRightX - lensCenterX;
                if (Math.abs(dxRedFocus) > 0.001f) {
                    float slopeRed = (lensCenterY - penTopY) / dxRedFocus;

                    float redRightY = lensCenterY + (getWidth() - focusRightX) * slopeRed;
                    canvas.drawLine(getWidth(), redRightY, lensCenterX, penTopY, paintSolidRed);
                    canvas.drawLine(lensCenterX, penTopY, penTopX, penTopY, paintSolidRed);
                    canvas.drawLine(lensCenterX, penTopY, targetX, targetY, paintDashedRed);

                    float redLeftY = targetY - (targetX * slopeRed);
                    canvas.drawLine(targetX, targetY, 0, redLeftY, paintDashedRed);
                }

            } else {
                // ---------------------------------------------------------------------
                // 2. ДЕЙСТВИТЕЛЬНОЕ ИЗОБРАЖЕНИЕ ("В F", "Между F и 2F", "В 2F", "За 2F")
                // ---------------------------------------------------------------------
                float targetX = imageTopX;
                float targetY = imageTopY;

                // ЗЕЛЕНЫЙ ЛУЧ
                canvas.drawLine(penTopX, penTopY, targetX, targetY, paintSolidGreen);

                // КРАСНЫЙ ЛУЧ
                canvas.drawLine(penTopX, penTopY, lensCenterX, penTopY, paintSolidRed);
                canvas.drawLine(lensCenterX, penTopY, targetX, targetY, paintSolidRed);

                float dxRed = targetX - lensCenterX;
                if (Math.abs(dxRed) > 0.001f) {
                    float slopeRed = (targetY - penTopY) / dxRed;
                    float redRightY = targetY + (getWidth() - targetX) * slopeRed;
                    canvas.drawLine(targetX, targetY, getWidth(), redRightY, paintSolidRed);
                }

                // СИНИЙ ЛУЧ
                canvas.drawLine(penTopX, penTopY, lensCenterX, targetY, paintSolidBlue);
                canvas.drawLine(lensCenterX, targetY, getWidth(), targetY, paintSolidBlue);
            }

        } else {
            // =========================================================================
            // РАССЕИВАЮЩАЯ ЛИНЗА (!isConverging)
            // =========================================================================

            // 1. Расстояние от предмета до линзы d
            float dPen = lensCenterX - penTopX;
            if (dPen < 1f) dPen = 1f;

            // 2. Фокусное расстояние и координата переднего фокуса F (слева от линзы)
            // Если у вас из Activity передается точная координата засечки F,
            // лучше использовать ее напрямую (например, focusLeftX = this.focusX)
            float fDistance = getWidth() * 0.145f;
            float focusLeftX = lensCenterX - fDistance;

            // 3. Точные физические координаты вершины мнимого изображения (targetX, targetY)
            float dImg = (dPen * fDistance) / (dPen + fDistance);
            float targetX = lensCenterX - dImg;

            float penHeight = lensCenterY - penTopY;
            float targetY = lensCenterY - (penHeight * (dImg / dPen));

            // =========================================================================
            // 1. КРАСНЫЙ ЛУЧ (Жесткое соединение точки линзы с передней риской F)
            // =========================================================================
            float dxRed = lensCenterX - focusLeftX;
            if (Math.abs(dxRed) > 0.001f) {
                float slopeRed = (lensCenterY - penTopY) / dxRed;

                // Сплошной преломленный луч: Горизонтально от линзы в кончик предмета
                canvas.drawLine(lensCenterX, penTopY, penTopX, penTopY, paintSolidRed);

                // Сплошной падающий луч: Справа сверху в точку преломления на линзе
                float redRightY = penTopY - ((getWidth() - lensCenterX) * slopeRed);
                canvas.drawLine(getWidth(), redRightY, lensCenterX, penTopY, paintSolidRed);

                // Мнимый пунктирный луч (Отрезок 1): Гарантированно попадает точно в засечку F
                canvas.drawLine(lensCenterX, penTopY, focusLeftX, lensCenterY, paintDashedRed);

                // Мнимый пунктирный луч (Отрезок 2): Продолжение от F до левого края экрана
                float redLeftY = lensCenterY + (focusLeftX * slopeRed);
                canvas.drawLine(focusLeftX, lensCenterY, 0, redLeftY, paintDashedRed);
            }

            // =========================================================================
            // 2. ЗЕЛЕНЫЙ ЛУЧ
            // =========================================================================
            float dxGreen = lensCenterX - penTopX;
            if (Math.abs(dxGreen) > 0.001f) {
                float slopeGreen = (lensCenterY - penTopY) / dxGreen;

                // Сплошной луч снизу справа через центр линзы в кончик предмета
                float greenRightY = lensCenterY + (getWidth() - lensCenterX) * slopeGreen;
                canvas.drawLine(getWidth(), greenRightY, penTopX, penTopY, paintSolidGreen);

                // Пунктирный след вверх-влево за предмет
                float greenLeftY = penTopY - (penTopX * slopeGreen);
                canvas.drawLine(penTopX, penTopY, 0, greenLeftY, paintDashedGreen);
            }

            // =========================================================================
            // 3. СИНИЙ ЛУЧ
            // =========================================================================
            // Сплошной луч справа параллельно оси на высоте изображения (targetY) до линзы
            canvas.drawLine(getWidth(), targetY, lensCenterX, targetY, paintSolidBlue);

            // Сплошной преломленный луч от линзы под углом в вершину предмета
            canvas.drawLine(lensCenterX, targetY, penTopX, penTopY, paintSolidBlue);

            // Мнимый пунктирный след от линзы влево горизонтально сквозь вершину мнимого изображения
            canvas.drawLine(lensCenterX, targetY, 0, targetY, paintDashedBlue);
        }
    }
}