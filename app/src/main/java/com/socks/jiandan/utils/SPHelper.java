package com.socks.jiandan.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SPHelper {

    private static Context mContext;

    private static final String SHARED_PREFERANCE_NAME = "jiandan_pref";

    public static void init(Context context) {
        mContext = context;
    }

    public static void setInteger(String key, int value) {
        SharedPreferences sp = mContext.getSharedPreferences(
                SHARED_PREFERANCE_NAME, Context.MODE_PRIVATE);
        Editor editor = sp.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public static int getInteger(String key, int defaultValue) {
        SharedPreferences sp = mContext.getSharedPreferences(
                SHARED_PREFERANCE_NAME, Context.MODE_PRIVATE);
        return sp.getInt(key, defaultValue);
    }

    public static void setString(String key, String value) {
        SharedPreferences sp = mContext.getSharedPreferences(
                SHARED_PREFERANCE_NAME, Context.MODE_PRIVATE);
        Editor editor = sp.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static String getString(String key) {
        SharedPreferences sp = mContext.getSharedPreferences(
                SHARED_PREFERANCE_NAME, Context.MODE_PRIVATE);
        return sp.getString(key, "");
    }

    public static void setBoolean(String key, boolean value) {
        SharedPreferences sp = mContext.getSharedPreferences(
                SHARED_PREFERANCE_NAME, Context.MODE_PRIVATE);
        Editor editor = sp.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public static boolean getBoolean(String key,
                                     boolean defaultValue) {
        SharedPreferences sp = mContext.getSharedPreferences(
                SHARED_PREFERANCE_NAME, Context.MODE_PRIVATE);
        return sp.getBoolean(key, defaultValue);
    }

}
