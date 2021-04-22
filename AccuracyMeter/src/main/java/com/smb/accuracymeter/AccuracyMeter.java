package com.smb.accuracymeter;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

import androidx.core.graphics.ColorUtils;

public class AccuracyMeter extends View {

    private final Paint linesPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private LinearGradient gradient = null;
    Path linesPath = new Path();

    public int linesCount = 20;
    private float[] points;


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

        points = getLinesPoints();

        int[] colors = {Color.RED, ColorUtils.blendARGB(Color.RED, Color.YELLOW, 0.7f), Color.GREEN};
        float[] colorPos = {dpToPixel(8), getWidth() / 2f, getWidth() - dpToPixel(8)};
        gradient = new LinearGradient(dpToPixel(8), 0f, getWidth() - dpToPixel(8), 0f, colors, null,Shader.TileMode.CLAMP);

        linesPaint.setStrokeWidth(dpToPixel(8));
        linesPaint.setStrokeCap(Paint.Cap.ROUND);
        linesPaint.setStyle(Paint.Style.STROKE);
        linesPaint.setColor(Color.RED);
        linesPaint.setShader(gradient);

        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    protected void onDraw(Canvas canvas) {


        canvas.drawLines(points, linesPaint);

    }

    public void initAttributes(Context context, AttributeSet attributeSet) {


    }

    private float[] getLinesPoints(){

        float[] points = new float[linesCount * 4];
        float yStart = getHeight() - dpToPixel(8);
        float minYEnd = yStart - ((getHeight() - (dpToPixel(8) * 2)) * (20f / 100f));
        float yDiff = ((getHeight() - (dpToPixel(8) * 2)) * (80f / 100f)) / linesCount;

        float lineX = dpToPixel(8);
        float xOffSet = (getWidth() - dpToPixel(8)) / linesCount + 1;

        for (int i = 0, index = 0; index < linesCount * 4; i++, index += 4) {

            points[index] = lineX;
            points[index + 1] = yStart;
            points[index + 2] = lineX;
            points[index + 3] = minYEnd - yDiff * i;

            lineX += xOffSet;
        }

        return points;
    }

    private float dpToPixel(int dp) {
        return dp * getResources().getDisplayMetrics().density;
    }
}
