package com.example.fan.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashSet;
import java.util.Set;

public class SharedPref {
    public static final String DOMAIN = "DOMAIN";
    public static final String COOKIE = "COOKIE";
    public static final String TOKEN = "TOKEN";
    public static final String AUTH = "AUTH";
    public static final String SUBSCRIBES = "SUBSCRIBES";
    private static SharedPreferences mSharedPref;

    private SharedPref() {

    }

    public static void init(Context context) {
        if (mSharedPref == null)
            mSharedPref = context.getSharedPreferences(context.getPackageName(), Activity.MODE_PRIVATE);
    }

    public static String read(String key, String defValue) {
        return mSharedPref.getString(key, defValue);
    }

    public static Set<String> readSubscribes() {
        return mSharedPref.getStringSet(SUBSCRIBES, new HashSet<String>());
    }

    public static void removeSubscribe(String topic) {
        Set<String> set = mSharedPref.getStringSet(SUBSCRIBES, new HashSet<String>());
        set.remove(topic);
        mSharedPref.edit().putStringSet(SUBSCRIBES, set).apply();
    }

    public static boolean containsSubscribe(String topic) {
        Set<String> subscribes = mSharedPref.getStringSet(SUBSCRIBES, new HashSet<String>());
        if (subscribes != null) {
            return subscribes.contains(topic);
        }
        return false;
    }

    public static boolean addSubscribes(String topic) {
        Set<String> subscribes = mSharedPref.getStringSet(SUBSCRIBES, new HashSet<String>());
        if (subscribes != null) {
            subscribes.add(topic);
            mSharedPref.edit().putStringSet(SUBSCRIBES, subscribes).apply();
            return true;
        }
        return false;
    }

    public static String read(String key) {
        return mSharedPref.getString(key, "");
    }

    public static void write(String key, String value) {
        SharedPreferences.Editor prefsEditor = mSharedPref.edit();
        prefsEditor.putString(key, value).apply();
    }

    public static boolean read(String key, boolean defValue) {
        return mSharedPref.getBoolean(key, defValue);
    }


    public static void write(String key, boolean value) {
        SharedPreferences.Editor prefsEditor = mSharedPref.edit();
        prefsEditor.putBoolean(key, value).apply();
    }

    public static Integer read(String key, int defValue) {
        return mSharedPref.getInt(key, defValue);
    }

    public static void write(String key, Integer value) {
        SharedPreferences.Editor prefsEditor = mSharedPref.edit();
        prefsEditor.putInt(key, value).apply();
    }

    public static boolean contains(String key) {
        return mSharedPref.contains(key);
    }

    public static void remove(String key) {
        SharedPreferences.Editor prefsEditor = mSharedPref.edit();
        prefsEditor.remove(key).apply();
    }

}