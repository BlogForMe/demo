package com.android.application;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.tim.annotation.DIActivity;
import com.tim.annotation.DIView;


@DIActivity
public class MainActivity extends AppCompatActivity {

    @DIView(value = R.id.text)
    TextView textView;

    @DIView(value = R.id.text1)
    TextView textView1;

    @DIView(value = R.id.text2)
    TextView textView2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DIMainActivity.bindView(this);
        textView.setText("Hello, JavaPoet!");

        textView2.setText("Tim");
    }
}