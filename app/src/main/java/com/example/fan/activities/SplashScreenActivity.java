package com.example.fan.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.example.fan.R;
import com.example.fan.utils.RemoteConfig;
import com.example.fan.utils.Utils;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import androidx.appcompat.app.AppCompatActivity;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        RemoteConfig.init();
        RemoteConfig.update(this);
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
                Log.d("ADInitialize",initializationStatus.toString());
            }
        });
//        Log.d("MainActivity", "ischeck: " + RemoteConfig.read(RemoteConfig.DOMAIN));

    }
}
