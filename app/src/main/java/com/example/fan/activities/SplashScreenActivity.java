package com.example.fan.activities;

import android.os.Bundle;
import android.util.Log;

import com.example.fan.R;
import com.example.fan.utils.RemoteConfig;
import com.example.fan.utils.Utils;

import androidx.appcompat.app.AppCompatActivity;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        RemoteConfig.init();
        RemoteConfig.update(this);
        Log.d("MainActivity", "ischeck: " + RemoteConfig.read(RemoteConfig.DOMAIN));

    }
}
