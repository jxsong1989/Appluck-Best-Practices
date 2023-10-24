package com.lightwebview;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;

import com.lightwebviewsdk.LigthWebview;

public class MainActivity extends AppCompatActivity {

    private Button close;

    private Button back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        close = findViewById(R.id.close);
        close.setOnClickListener((v) -> {
            LigthWebview.open(this, "https://aios.soinluck.com/scene?sk=q842c2e079a1b32c8");
        });

        back = findViewById(R.id.back);
        back.setOnClickListener((v) -> {
            LigthWebview.open(this, "https://aios.soinluck.com/scene?sk=q842c2e079a1b32c8", 1);
        });
    }
}