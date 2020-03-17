package com.hsw.slidelockview;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements OnLockListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SlideVerLockView verLockView = findViewById(R.id.slide_ver);
        SlideHorLockView horLockView = findViewById(R.id.slide_hor);
        verLockView.setOnLockListener(this);
        horLockView.setOnLockListener(this);
    }

    @Override
    public void locked(boolean result) {
        Toast.makeText(MainActivity.this, result ? "已解锁" : "未解锁", Toast.LENGTH_LONG).show();
    }
}
