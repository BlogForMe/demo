package com.rhythm7.annotationprocessordemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.rhythm7.lib_annotations.RandomInt;
import com.rhythm7.lib_annotations.RandomString;

public class MainActivity extends AppCompatActivity {
    @RandomInt(minValue = 10, maxValue = 1000)
    int mRandomInt;

    @RandomString
    String mRandomString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        RandomUtil.inject(this);
//
//        TextView tvRandom = findViewById(R.id.tv_random);
//        tvRandom.setText("RandomInt:    " + mRandomInt + "\n"
//                + "RandomString:    " + mRandomString);
    }
}
