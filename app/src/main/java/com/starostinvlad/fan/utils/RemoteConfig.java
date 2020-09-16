package com.starostinvlad.fan.utils;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.starostinvlad.fan.R;
import com.starostinvlad.fan.activities.ExoPlayerActivity;
import com.starostinvlad.fan.activities.MainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

public class RemoteConfig {
    public static final String DOMAIN = "DOMAIN";
    public static final String IS_REVIEW = "REVIEW";
    public static final String ALL_SERIAL_JSON = "ALL_SERIAL_JSON";
    public static final String IP = "PROXY_IP";
    public static final String PORT = "PROXY_PORT";

    private static FirebaseRemoteConfig mFirebaseRemoteConfig;

    private RemoteConfig() {

    }

    public static void init() {
        Log.d("RemoteConfig","RemoteConfig init");
        if (mFirebaseRemoteConfig == null) {
            mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
            mFirebaseRemoteConfig.setDefaultsAsync(R.xml.remote_config_defaults);
            FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                    .setMinimumFetchIntervalInSeconds(0)
                    .build();
            mFirebaseRemoteConfig.setConfigSettingsAsync(configSettings);
        }
    }

    public static String read(String key) {
        return mFirebaseRemoteConfig.getString(key);
    }

    public static void update(final Context context) {
        // cacheExpirationSeconds is set to cacheExpiration here, indicating the next fetch request
        // will use fetch data from the Remote Config service, rather than cached parameter values,
        // if cached parameter values are more than cacheExpiration seconds old.
        // See Best Practices in the README for more information.
        mFirebaseRemoteConfig.fetchAndActivate()
                .addOnCompleteListener(new OnCompleteListener<Boolean>() {
                    @Override
                    public void onComplete(@NonNull Task<Boolean> task) {
                        if (task.isSuccessful()) {
                            SharedPref.init(context);
                            Utils.init(context);
                            context.startActivity(new Intent(context, MainActivity.class));
                            ((Activity) context).finish();
                        } else {
                            if (context != null) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                builder.setTitle("Ошибка!")
                                        .setMessage("Не удалось получить актуальный домен")
                                        .setCancelable(false)
                                        .setNegativeButton("ОК",
                                                new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int id) {
                                                        dialog.cancel();
                                                        android.os.Process.killProcess(android.os.Process.myPid());
                                                        System.exit(1);
                                                    }
                                                });
                                AlertDialog alert = builder.create();
                                alert.show();
                            }

                        }
                    }
                });
    }

    public static void update(final Context context, final Uri data) {
        mFirebaseRemoteConfig.fetchAndActivate()
                .addOnCompleteListener(new OnCompleteListener<Boolean>() {
                    @Override
                    public void onComplete(@NonNull Task<Boolean> task) {
                        if (task.isSuccessful()) {
                            SharedPref.init(context);
                            Utils.init(context);

                            String uri = data.toString();
                            Log.i("MyApp", "Deep link clicked " + uri);
                            if (uri.substring(uri.length() - 4).equals("html")) {
                                Intent intent = new Intent(context, ExoPlayerActivity.class);
                                intent.putExtra("Title", "");
                                intent.putExtra("SubTitle", "");
                                intent.putExtra("URL", uri);
                                context.startActivity(intent);
                            } else {
                                context.startActivity(new Intent(context, MainActivity.class));
                            }
                            ((Activity) context).finish();
                        } else {
                            if (context != null) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                builder.setTitle("Ошибка!")
                                        .setMessage("Не удалось получить актуальный домен")
                                        .setCancelable(false)
                                        .setNegativeButton("ОК",
                                                new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int id) {
                                                        dialog.cancel();
                                                        android.os.Process.killProcess(android.os.Process.myPid());
                                                        System.exit(1);
                                                    }
                                                });
                                AlertDialog alert = builder.create();
                                alert.show();
                            }

                        }
                    }
                });
    }
}