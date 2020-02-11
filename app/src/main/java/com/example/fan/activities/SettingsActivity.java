package com.example.fan.activities;

import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Toast;

import com.example.fan.R;
import com.example.fan.utils.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

public class SettingsActivity extends AppCompatActivity {

    static boolean auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings, new SettingsFragment())
                .commit();
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);
            findPreference("about_us").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle("Не официальное приложение FanSerials!")
                            .setIcon(R.drawable.logo1)
                            .setMessage("copyright 2019")
                            .setCancelable(false)
                            .setNegativeButton("ОК",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                        }
                                    });
                    AlertDialog alert = builder.create();
                    alert.show();
                    return false;
                }
            });

            findPreference("switchNotification").setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    if (newValue.equals(true)) {
                        FirebaseMessaging.getInstance().subscribeToTopic("news")
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        String msg = getString(R.string.msg_subscribed);
                                        if (!task.isSuccessful()) {
                                            msg = getString(R.string.msg_subscribe_failed);
                                        }
                                        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
                                    }
                                });
                    } else {
                        FirebaseMessaging.getInstance().unsubscribeFromTopic("news")
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        String msg = getString(R.string.msg_subscribed);
                                        if (!task.isSuccessful()) {
                                            msg = getString(R.string.msg_subscribe_failed);
                                        }
                                        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                    return true;
                }
            });
            auth = getContext().getSharedPreferences("URL", MODE_PRIVATE)
                    .getBoolean("Auth", false);
            findPreference("logout").setEnabled(auth);


            findPreference("logout").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {

                    Utils utils = new Utils();
                    if (auth) {
                        utils.logout(getContext());
                        preference.setEnabled(false);
                    }
                    return false;
                }
            });

        }
    }
}