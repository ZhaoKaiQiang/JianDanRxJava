package com.socks.jiandan.utils;

import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.widget.Toast;

import com.socks.jiandan.JDApplication;

public class ToastHelper {

    public static void Short(@NonNull CharSequence sequence) {
        Toast.makeText(JDApplication.getContext(), sequence, Toast.LENGTH_SHORT).show();
    }

    public static void Short(@StringRes int id) {
        Toast.makeText(JDApplication.getContext(), id, Toast.LENGTH_SHORT).show();
    }

    public static void Long(@StringRes int id) {
        Toast.makeText(JDApplication.getContext(), id, Toast.LENGTH_LONG).show();
    }

    public static void Long(@NonNull CharSequence sequence) {
        Toast.makeText(JDApplication.getContext(), sequence, Toast.LENGTH_LONG).show();
    }

}
