package com.starostinvlad.fan.activities;

import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.starostinvlad.fan.R;
import com.starostinvlad.fan.utils.RemoteConfig;

import java.util.Arrays;

import androidx.appcompat.app.AppCompatActivity;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        RemoteConfig.init();
        RemoteConfig.update(this);
        MobileAds.setRequestConfiguration(new RequestConfiguration
                .Builder()
                .setTestDeviceIds(Arrays.asList(
                        "DFDC84BFD22F6F12CEFBDB0880FA290B",
                        AdRequest.DEVICE_ID_EMULATOR
                )).build());
        MobileAds.initialize(this, initializationStatus -> Log.d("ADInitialize", initializationStatus.toString()));
//        Log.d("MainActivity", "ischeck: " + RemoteConfig.read(RemoteConfig.DOMAIN));

    }
}
