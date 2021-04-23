package com.smb.accuracymeter;

import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import androidx.core.graphics.ColorUtils;

public class AccuracyMeter extends View {

    private final Paint linesPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint linesBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private LinearGradient gradient = null;

    public int totalLinesCount = 30;
    public int progress = 1;
    private float[] totalLinesPoints;


    public AccuracyMeter(Context context) {
        super(context);
        initAttributes(context, null);
    }

    public AccuracyMeter(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttributes(context, attrs);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {

        totalLinesPoints = getLinesPoints(totalLinesCount);

        int[] colors = {Color.RED, ColorUtils.blendARGB(Color.RED, Color.YELLOW, 0.7f), Color.GREEN};
        gradient = new LinearGradient(dpToPixel(8), 0f, getWidth() - dpToPixel(8), 0f, colors, null,Shader.TileMode.CLAMP);

        setLinesPaintParams(linesPaint);
        setLinesBackgroundPaintParams(linesBackgroundPaint);

        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawLines(totalLinesPoints, linesBackgroundPaint);
        canvas.drawLines(totalLinesPoints, 0, progress * 4, linesPaint);

    }

    public void initAttributes(Context context, AttributeSet attributeSet) {



    }

    public void animateProgressTo(float percentage) {

        int progressLinesCount = (int) ((percentage / 100) * totalLinesCount);

        ValueAnimator progressAnimation = ValueAnimator.ofInt(1, progressLinesCount);
        progressAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                progress = (int) valueAnimator.getAnimatedValue();
                invalidate();
            }
        });
        progressAnimation.setDuration(2000);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(progressAnimation);
        animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
        animatorSet.start();
    }

    private float[] getLinesPoints(int linesCount){

        float[] points = new float[linesCount * 4];

        float yStart = getHeight() - dpToPixel(8);
        float minYEnd = yStart - ((getHeight() - (dpToPixel(8) * 2)) * (5f / 100f));
        float yDiff = ((getHeight() - (dpToPixel(8) * 2)) * (95f / 100f)) / linesCount;

        float lineX = dpToPixel(8);
        float xOffSet = (getWidth() - dpToPixel(8)) / linesCount;

        for (int i = 0, index = 0; index < linesCount * 4; i++, index += 4) {

            points[index] = lineX;
            points[index + 1] = yStart;
            points[index + 2] = lineX;
            points[index + 3] = minYEnd - yDiff * i;

            lineX += xOffSet;
        }

        return points;
    }

    private void setLinesBackgroundPaintParams(Paint paint) {
        paint.setStrokeWidth(dpToPixel(8));
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setColor(Color.LTGRAY);
        paint.setStyle(Paint.Style.STROKE);
    }

    private void setLinesPaintParams(Paint paint) {
        paint.setStrokeWidth(dpToPixel(8));
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStyle(Paint.Style.STROKE);
        paint.setShader(gradient);
    }

    private float dpToPixel(int dp) {
        return dp * getResources().getDisplayMetrics().density;
    }
}
