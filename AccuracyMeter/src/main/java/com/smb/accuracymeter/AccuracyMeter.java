package com.smb.accuracymeter;

import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.ColorUtils;

public class AccuracyMeter extends View {

    public enum TextPosition { BOTTOM_LEFT, BOTTOM_RIGHT;}

    private final Paint linesPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint linesBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint limitIndicatorPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final TextPaint textPaint = new TextPaint(TextPaint.ANTI_ALIAS_FLAG | TextPaint.SUBPIXEL_TEXT_FLAG);
    private LinearGradient mainGradient = null;
    private LinearGradient backgroundGradient = null;

    private float[] linesPoints;
    private final float innerPadding = dpToPixel(8);
    public int totalLinesCount = 50;
    public float lineWidth = dpToPixel(5);
    public int progress = 0;

    public float backgroundAlpha = 0.5f;

    public long animationDuration = 1000;

    public boolean thresholdEnabled = true;
    public float threshold = 70;
    public int thresholdIndicatorColor = Color.BLACK;

    private float textX = 0f;
    private float textY = 0f;
    private float textHeight = 0f;
    private String text = "0%";
    public boolean ProgressTextEnabled = true;
    public float textSize = dpToPixel(16);
    public int textStyle = 0;
    public int fontFamily = 0;
    public int textColor = Color.BLACK;
    public TextPosition textPosition = TextPosition.BOTTOM_LEFT;

    public AccuracyMeter(Context context) {
        super(context);
        initAttributes(context, null);
    }

    public AccuracyMeter(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttributes(context, attrs);
    }

    private void initAttributes(Context context, AttributeSet attributeSet) {

        TypedArray attrs = context.getTheme().obtainStyledAttributes(attributeSet, R.styleable.AccuracyMeter, 0, 0);

        totalLinesCount = attrs.getInt(R.styleable.AccuracyMeter_am_linesCount, 50);
        lineWidth = attrs.getDimension(R.styleable.AccuracyMeter_am_linesWidth, dpToPixel(5));
        animationDuration = attrs.getInt(R.styleable.AccuracyMeter_am_animationDuration, 1000);

        ProgressTextEnabled = attrs.getBoolean(R.styleable.AccuracyMeter_am_ProgressTextEnabled, true);
        textSize = attrs.getDimension(R.styleable.AccuracyMeter_am_textSize, dpToPixel(16));
        textStyle = attrs.getInt(R.styleable.AccuracyMeter_am_textStyle, Typeface.NORMAL);
        fontFamily = attrs.getResourceId(R.styleable.AccuracyMeter_am_textFont, 0);
        textColor = attrs.getInteger(R.styleable.AccuracyMeter_am_textColor, Color.BLACK);
        textPosition = getTextPosition(attrs.getInt(R.styleable.AccuracyMeter_am_textPosition, 0));

        backgroundAlpha = Math.min(attrs.getFloat(R.styleable.AccuracyMeter_am_backgroundAlpha, 0.5f), 1f);

        thresholdEnabled = attrs.getBoolean(R.styleable.AccuracyMeter_am_ThresholdIndicatorEnabled, true);
        thresholdIndicatorColor = attrs.getInteger(R.styleable.AccuracyMeter_am_ThresholdIndicatorColor, Color.BLACK);
        threshold = attrs.getFloat(R.styleable.AccuracyMeter_am_Threshold, 70f);

        attrs.recycle();
    }

    @Override
    protected void onDraw(Canvas canvas) {

        canvas.drawLines(linesPoints, linesBackgroundPaint);
        canvas.drawLines(linesPoints, 0, progress * 4, linesPaint);

        if (thresholdEnabled) {
            Path limiterPath = getThresholdPath(threshold);
            canvas.drawPath(limiterPath, limitIndicatorPaint);
        }

        if (ProgressTextEnabled) {
            canvas.drawText(text, textX, textY, textPaint);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int w = MeasureSpec.getSize(widthMeasureSpec);
        int h = MeasureSpec.getSize(heightMeasureSpec);

        int hMode = MeasureSpec.getMode(heightMeasureSpec);

        int minHeight = (int) (textHeight + innerPadding * 3 + dpToPixel(100));

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

    @SuppressLint("DrawAllocation")
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {

        int[] mainColors = {Color.RED, ColorUtils.blendARGB(Color.RED, Color.YELLOW, 0.7f), Color.GREEN};
        int[] backgroundColors = {ColorUtils.blendARGB( Color.TRANSPARENT, Color.RED, backgroundAlpha),
                ColorUtils.blendARGB(Color.TRANSPARENT, Color.GREEN, backgroundAlpha)};

        mainGradient = new LinearGradient(innerPadding, 0f, getWidth() - innerPadding, 0f,
                mainColors, null,Shader.TileMode.CLAMP);
        backgroundGradient = new LinearGradient(innerPadding, 0f, getWidth() - innerPadding, 0f,
                backgroundColors, null,Shader.TileMode.CLAMP);

        setLinesPaintParams();
        setLinesBackgroundPaintParams();

        setTextParams();
        setTextPosition();

        linesPoints = getLinesPoints(totalLinesCount);

        super.onLayout(changed, left, top, right, bottom);
    }

    private Path getThresholdPath(float limit) {

        limitIndicatorPaint.setStyle(Paint.Style.STROKE);
        limitIndicatorPaint.setColor(thresholdIndicatorColor);
        limitIndicatorPaint.setStrokeCap(Paint.Cap.ROUND);
        limitIndicatorPaint.setStrokeJoin(Paint.Join.ROUND);
        limitIndicatorPaint.setStrokeWidth(dpToPixel(2));

        float xStart = linesPoints[linesPoints.length - 4] + lineWidth / 2;
        float yStart = textY - textHeight + dpToPixel(5);
        float indicatorDepth = dpToPixel(5);

        int indicatorLineXIndex = Math.max((int) (Math.ceil((100 - limit) * totalLinesCount / 100) - 1) * 4, 0);
        float indicatorLength = linesPoints[indicatorLineXIndex] + lineWidth;

        Path limitIndicatorPath = new Path();

        limitIndicatorPath.moveTo(xStart, yStart);
        limitIndicatorPath.lineTo(xStart, yStart + indicatorDepth);
        limitIndicatorPath.lineTo(xStart - indicatorLength, yStart + indicatorDepth);
        limitIndicatorPath.lineTo(xStart - indicatorLength, yStart);

        return limitIndicatorPath;
    }

    public void animateProgressTo(float percentage) {

        percentage = Math.min(percentage, 100);

        ValueAnimator progressAnimation = ValueAnimator.ofFloat( progress * 100f / totalLinesCount, percentage);
        progressAnimation.addUpdateListener(valueAnimator -> {
            float val = (float) valueAnimator.getAnimatedValue();
            progress = Math.min(totalLinesCount, (int) (val * totalLinesCount / 100));
            text = (int) val + "%";
            invalidate();
        });
        progressAnimation.setDuration(animationDuration);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(progressAnimation);
        animatorSet.setInterpolator(new DecelerateInterpolator());
        animatorSet.start();
    }

    public void animateThresholdIndicatorTo(float newThreshold){

        newThreshold = Math.min(newThreshold, 100);
        newThreshold = Math.max(newThreshold, 1);

        ValueAnimator thresholdAnimation = ValueAnimator.ofFloat(threshold, newThreshold);
        thresholdAnimation.addUpdateListener(valueAnimator -> {
            threshold = (float) valueAnimator.getAnimatedValue();
            invalidate();
        });

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setInterpolator(new DecelerateInterpolator());
        animatorSet.setDuration(500);
        animatorSet.play(thresholdAnimation);
        animatorSet.start();
    }

    public void reset() {
        text = "0%";
        progress = 0;

        invalidate();
    }

    private TextPosition getTextPosition(int pos) {

        switch (pos) {
            case 0: {
                return TextPosition.BOTTOM_LEFT;
            }
            case 1: {
                return TextPosition.BOTTOM_RIGHT;
            }
        }

        return TextPosition.BOTTOM_LEFT;
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
            case BOTTOM_LEFT:{ //BOTTOM_LEFT
                textX = innerPadding;
                textY = getHeight() - innerPadding;
                break;
            }
            case BOTTOM_RIGHT:{ //BOTTOM_RIGHT
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

        if (ProgressTextEnabled) {
            textHeight = innerPadding + textPaint.descent() - textPaint.ascent();
        }
    }

    private void setLinesBackgroundPaintParams() {
        linesBackgroundPaint.setStrokeWidth(lineWidth);
        linesBackgroundPaint.setStrokeCap(Paint.Cap.ROUND);
        linesBackgroundPaint.setColor(Color.LTGRAY);
        linesBackgroundPaint.setStyle(Paint.Style.STROKE);
        linesBackgroundPaint.setShader(backgroundGradient);
    }

    private void setLinesPaintParams() {
        linesPaint.setStrokeWidth(lineWidth);
        linesPaint.setStrokeCap(Paint.Cap.ROUND);
        linesPaint.setStyle(Paint.Style.STROKE);
        linesPaint.setShader(mainGradient);

        linesPaint.setShadowLayer(10f, 15f, 15f, ColorUtils.blendARGB(Color.GRAY, Color.TRANSPARENT, 0.5f));
    }

    private float dpToPixel(int dp) {
        return dp * getResources().getDisplayMetrics().density;
    }

    public int getLineCount() {
        return totalLinesCount;
    }

    public void setLineCount(int totalLinesCount) {
        this.totalLinesCount = totalLinesCount;
        requestLayout();
    }

    public float getLineWidthDp() {
        return lineWidth / getResources().getDisplayMetrics().density;
    }

    public void setLineWidthDp(int lineWidth) {
        this.lineWidth = dpToPixel(lineWidth);
        invalidate();
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
        invalidate();
    }

    public float getBackgroundAlpha() {
        return backgroundAlpha;
    }

    public void setBackgroundAlpha(float backgroundAlpha) {
        this.backgroundAlpha = backgroundAlpha;
        invalidate();
    }

    public long getAnimationDuration() {
        return animationDuration;
    }

    public void setAnimationDuration(long animationDuration) {
        this.animationDuration = animationDuration;
    }

    public boolean isThresholdRangeEnabled() {
        return thresholdEnabled;
    }

    public void setThresholdRangeEnabled(boolean limitIndicatorEnabled) {
        this.thresholdEnabled = limitIndicatorEnabled;
        invalidate();
    }

    public float getThreshold() {
        return threshold;
    }

    public void setThreshold(float limitIndicatorPercentage) {
        this.threshold = limitIndicatorPercentage;
        invalidate();
    }

    public int getThresholdIndicatorColor() {
        return thresholdIndicatorColor;
    }

    public void setThresholdIndicatorColor(int limiterColor) {
        this.thresholdIndicatorColor = limiterColor;
        invalidate();
    }

    public boolean isProgressTextEnabled() {
        return ProgressTextEnabled;
    }

    public void setProgressTextEnabled(boolean percentageEnabled) {
        this.ProgressTextEnabled = percentageEnabled;
        invalidate();
    }

    public float getTextSizeDp() {
        return textSize / getResources().getDisplayMetrics().density;
    }

    public void setTextSizeDp(int textSize) {
        this.textSize = dpToPixel(textSize);
        requestLayout();
    }

    public int getTextStyle() {
        return textStyle;
    }

    public void setTextStyle(int textStyle) {
        this.textStyle = textStyle;
        requestLayout();
    }

    public int getFontFamily() {
        return fontFamily;
    }

    public void setFontFamily(int fontFamily) {
        this.fontFamily = fontFamily;
        requestLayout();
    }

    public int getTextColor() {
        return textColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
        invalidate();
    }

    public TextPosition getTextPosition() {
        return textPosition;
    }

    public void setTextPosition(TextPosition textPosition) {
        this.textPosition = textPosition;
        invalidate();
    }
}
