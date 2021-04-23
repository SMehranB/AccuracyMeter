package com.smb.accuracymetersample;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.smb.accuracymeter.AccuracyMeter;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        AccuracyMeter am = findViewById(R.id.am);
        Button button = findViewById(R.id.btnAnimate);
        EditText editText = findViewById(R.id.etPercentage);

        button.setOnClickListener(view -> am.animateProgressTo(Integer.parseInt(editText.getText().toString())));




    }
}