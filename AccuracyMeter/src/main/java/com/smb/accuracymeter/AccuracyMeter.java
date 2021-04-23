package com.smb.accuracymeter;

import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.ColorUtils;

public class AccuracyMeter extends View {

    private final Paint linesPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint linesBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final TextPaint textPaint = new TextPaint(TextPaint.ANTI_ALIAS_FLAG | TextPaint.SUBPIXEL_TEXT_FLAG);
    private LinearGradient mainGradient = null;
    private LinearGradient backgroundGradient = null;
    private float[] totalLinesPoints;

    public int totalLinesCount = 30;
    public int progress = 0;
    long animationDuration = 1000;

    public float lineWidth = dpToPixel(8);

    public float innerPadding = dpToPixel(8);

    private float textX = 0f;
    private float textY = 0f;
    private float textHeight = 0f;
    private String text = "0%";
    public boolean percentageEnabled = true;
    public float textSize = dpToPixel(16);
    public int textStyle = 0;
    private int fontFamily = 0;
    public int textColor = Color.BLACK;
    public int textPosition = 0; //bottom_left

    public void initAttributes(Context context, AttributeSet attributeSet) {

        TypedArray attrs = context.getTheme().obtainStyledAttributes(attributeSet, R.styleable.AccuracyMeter, 0, 0);

        totalLinesCount = attrs.getInt(R.styleable.AccuracyMeter_am_linesCount, 30);
        lineWidth = attrs.getDimension(R.styleable.AccuracyMeter_am_linesWidth, dpToPixel(8));
        animationDuration = (long) attrs.getInt(R.styleable.AccuracyMeter_am_animationDuration, 1000);

        percentageEnabled = attrs.getBoolean(R.styleable.AccuracyMeter_am_percentageEnabled, true);
        textSize = attrs.getDimension(R.styleable.AccuracyMeter_am_textSize, dpToPixel(16));
        textStyle = attrs.getInt(R.styleable.AccuracyMeter_am_textStyle, Typeface.NORMAL);
        fontFamily = attrs.getResourceId(R.styleable.AccuracyMeter_am_textFont, 0);
        textColor = attrs.getInteger(R.styleable.AccuracyMeter_am_textColor, Color.BLACK);
        textPosition = attrs.getInt(R.styleable.AccuracyMeter_am_textPosition, 0);

        attrs.recycle();

    }

    public AccuracyMeter(Context context) {
        super(context);
        initAttributes(context, null);
    }

    public AccuracyMeter(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttributes(context, attrs);
    }

/*
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int w = MeasureSpec.getSize(widthMeasureSpec);
        int h = MeasureSpec.getSize(heightMeasureSpec);

        int hMode = MeasureSpec.getMode(heightMeasureSpec);

        int textHeight = (int) (textPaint.descent() - textPaint.ascent());
        int highlightHeight = (int) (highlightPaint.descent() - highlightPaint.ascent());

        int minHeight = (int) (Math.max(textHeight, highlightHeight) + verticalPadding * 2 + shadowOffSetY);

        switch (hMode) {
            case MeasureSpec.EXACTLY:
                setMeasuredDimension(w, h);
                break;
            case MeasureSpec.AT_MOST:
                setMeasuredDimension(w, Math.min(minHeight, h));
                break;
            case MeasureSpec.UNSPECIFIED:
                setMeasuredDimension(w, minHeight);
                break;
        }
    }
*/

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {

        int[] mainColors = {Color.RED, ColorUtils.blendARGB(Color.RED, Color.YELLOW, 0.7f), Color.GREEN};
        int[] backgroundColors = {ColorUtils.blendARGB(Color.RED, Color.WHITE, 0.8f),
                ColorUtils.blendARGB(Color.GREEN, Color.WHITE, 0.8f)};

        mainGradient = new LinearGradient(innerPadding, 0f, getWidth() - innerPadding, 0f, mainColors, null,Shader.TileMode.CLAMP);
        backgroundGradient = new LinearGradient(innerPadding, 0f, getWidth() - innerPadding, 0f, backgroundColors, null,Shader.TileMode.CLAMP);

        setLinesPaintParams(linesPaint);
        setLinesBackgroundPaintParams(linesBackgroundPaint);

        setTextParams();
        setTextPosition();

        totalLinesPoints = getLinesPoints(totalLinesCount);

        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawLines(totalLinesPoints, linesBackgroundPaint);
        canvas.drawLines(totalLinesPoints, 0, progress * 4, linesPaint);
        if (percentageEnabled) {
            canvas.drawText(text, textX, textY, textPaint);
        }
    }


    public void animateProgressTo(float percentage) {

        percentage = Math.min(percentage, 100);

        ValueAnimator progressAnimation = ValueAnimator.ofFloat(0, percentage);
        progressAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float val = (float) valueAnimator.getAnimatedValue();
                progress = Math.min(totalLinesCount, (int) (val * totalLinesCount / 100));
                text = (int) val + "%";
                invalidate();
            }
        });
        progressAnimation.setDuration(animationDuration);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(progressAnimation);
        animatorSet.setInterpolator(new DecelerateInterpolator());
        animatorSet.start();
    }

    private float[] getLinesPoints(int linesCount) {

        float[] points = new float[linesCount * 4];

        float yStart = getHeight() - textHeight - dpToPixel(8);
        float minYEnd = yStart - ((getHeight() - (dpToPixel(8) * 2)) * (5f / 100f));
        float yDiff = ((getHeight() - (dpToPixel(8) * 2) - textHeight) * (95f / 100f)) / linesCount;

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

    private void setTextPosition() {

        switch (textPosition) {
            case 0:{ //BOTTOM_LEFT
                textX = innerPadding;
                textY = getHeight() - innerPadding;
                break;
            }
            case 1:{ //BOTTOM_RIGHT
                textX = getWidth() - innerPadding - textPaint.measureText("100%");
                textY = getHeight() - innerPadding;
                break;
            }
        }
    }

    private void setTextParams() {

        textPaint.setTextSize(textSize);
        textPaint.setColor(textColor);
        textPaint.setTypeface(Typeface.defaultFromStyle(textStyle));

        if (fontFamily != 0) {
            Typeface font = ResourcesCompat.getFont(getContext(), fontFamily);
            textPaint.setTypeface(Typeface.create(font, textStyle));
        }

        if (percentageEnabled) {
            textHeight = innerPadding + textPaint.descent() - textPaint.ascent();
        }
    }

    private void setLinesBackgroundPaintParams(Paint paint) {
        paint.setStrokeWidth(lineWidth);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setColor(Color.LTGRAY);
        paint.setStyle(Paint.Style.STROKE);
        paint.setShader(backgroundGradient);
    }

    private void setLinesPaintParams(Paint paint) {
        paint.setStrokeWidth(lineWidth);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStyle(Paint.Style.STROKE);
        paint.setShader(mainGradient);
    }

    private float dpToPixel(int dp) {
        return dp * getResources().getDisplayMetrics().density;
    }
}
