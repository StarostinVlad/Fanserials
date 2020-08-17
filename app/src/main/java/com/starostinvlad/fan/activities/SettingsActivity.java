package com.starostinvlad.fan.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.starostinvlad.fan.R;
import com.starostinvlad.fan.utils.SharedPref;
import com.starostinvlad.fan.utils.Utils;
import com.google.firebase.messaging.FirebaseMessaging;

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
//                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
//                    builder.setTitle("Не официальное приложение FanSerials!")
//                            .setIcon(R.mipmap.ic_launcher)
//                            .setMessage("copyright 2019")
//                            .setCancelable(false)
//                            .setNegativeButton("ОК",
//                                    new DialogInterface.OnClickListener() {
//                                        public void onClick(DialogInterface dialog, int id) {
//                                            dialog.cancel();
//                                        }
//                                    });
//                    AlertDialog alert = builder.create();
//                    alert.show();

                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    View dialogView = ((Activity) getContext()).getLayoutInflater().inflate(R.layout.copyright_dialog, null);
                    builder.setView(dialogView);
                    AlertDialog alert = builder.create();
                    alert.show();

                    return false;
                }
            });

            if (SharedPref.readSubscribes().size() == 0) {
                findPreference("switchNotification").callChangeListener(true);
                findPreference("switchNotification").setEnabled(false);
            } else {
                findPreference("switchNotification").setEnabled(true);
            }

            findPreference("switchNotification").setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    if (newValue.equals(true)) {
                        for (String topic : SharedPref.readSubscribes())
                            FirebaseMessaging.getInstance().subscribeToTopic(topic);
                    } else {
                        for (String topic : SharedPref.readSubscribes())
                            FirebaseMessaging.getInstance().unsubscribeFromTopic(topic);
                    }
                    return true;
                }
            });
            findPreference("logout").setEnabled(Utils.AUTH);


            findPreference("logout").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {


                    if (Utils.AUTH) {
                        Utils.logout();
                        preference.setEnabled(false);
                    }
                    return false;
                }
            });

        }
    }
}