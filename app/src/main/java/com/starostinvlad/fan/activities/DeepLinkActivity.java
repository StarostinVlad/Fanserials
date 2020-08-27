package com.starostinvlad.fan.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.starostinvlad.fan.R;
import com.starostinvlad.fan.utils.RemoteConfig;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

public class DeepLinkActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Uri data = this.getIntent().getData();
        if (data != null && data.isHierarchical()) {
            RemoteConfig.init();
            RemoteConfig.update(this, data);
            MobileAds.initialize(this, new OnInitializationCompleteListener() {
                @Override
                public void onInitializationComplete(InitializationStatus initializationStatus) {
                    Log.d("ADInitialize",initializationStatus.toString());
                }
            });
        }
    }
}
