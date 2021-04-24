package com.smb.accuracymetersample;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.smb.accuracymeter.AccuracyMeter;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    AccuracyMeter am;
    AccuracyMeter am2;
    AccuracyMeter am3;
    Button button1;
    Button button2;
    Button button3;
    Button button4;
    Button buttonReset;
    Button buttonIncreaseThreshold;
    Button buttonDecreaseThreshold;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        am = findViewById(R.id.am);
        am2 = findViewById(R.id.am2);
        am3 = findViewById(R.id.am3);
        button1 = findViewById(R.id.btnAnimate25);
        button2 = findViewById(R.id.btnAnimate50);
        button3 = findViewById(R.id.btnAnimate75);
        button4 = findViewById(R.id.btnAnimate100);
        buttonReset = findViewById(R.id.btnReset);
        buttonIncreaseThreshold = findViewById(R.id.btnIncreaseThreshold);
        buttonDecreaseThreshold = findViewById(R.id.btnDecreaseThreshold);

        AccuracyMeter accuracyMeter = new AccuracyMeter(this);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        accuracyMeter.setLayoutParams(params);
        accuracyMeter.setProgressTextEnabled(true);
        accuracyMeter.setThresholdRangeEnabled(true);
        accuracyMeter.setThresholdIndicatorColor(Color.YELLOW);
        accuracyMeter.setTextColor(Color.YELLOW);
        accuracyMeter.setTextPosition(AccuracyMeter.TextPosition.BOTTOM_LEFT);
        accuracyMeter.setTextSizeDp(24);
        accuracyMeter.setLineCount(100);
        accuracyMeter.setLineWidthDp(2);
        accuracyMeter.setAnimationDuration(500);
        accuracyMeter.setThreshold(95);
        accuracyMeter.setTextStyle(Typeface.ITALIC);
        accuracyMeter.setBackgroundAlpha(0.5f);

        LinearLayout vh = findViewById(R.id.viewHolder);
        vh.addView(accuracyMeter);

        List<AccuracyMeter> meters = new ArrayList<AccuracyMeter>();
        meters.add(am);
        meters.add(am2);
        meters.add(am3);
        meters.add(accuracyMeter);

        buttonIncreaseThreshold.setOnClickListener(view -> {
            for (int i = 0; i < meters.size(); i++) {
                meters.get(i).animateThresholdIndicatorTo(meters.get(i).getThreshold() - 5);
            }
        });

        buttonDecreaseThreshold.setOnClickListener(view -> {
            for (int i = 0; i < meters.size(); i++) {
                meters.get(i).animateThresholdIndicatorTo(meters.get(i).getThreshold() + 5);
            }
        });

        button1.setOnClickListener(view ->{
            for (int i = 0; i < meters.size(); i++) {
                meters.get(i).animateProgressTo(14);
            }
        });
        button2.setOnClickListener(view ->{
            for (int i = 0; i < meters.size(); i++) {
                meters.get(i).animateProgressTo(38);
            }
        });
        button3.setOnClickListener(view ->{
            for (int i = 0; i < meters.size(); i++) {
                meters.get(i).animateProgressTo(83);
            }
        });
        button4.setOnClickListener(view ->{
            for (int i = 0; i < meters.size(); i++) {
                meters.get(i).animateProgressTo(97);
            }
        });

        buttonReset.setOnClickListener(view -> {
            for (int i = 0; i < meters.size(); i++) {
                meters.get(i).reset();
            }
        });
    }
}