package com.socks.jiandan.utils;

import android.os.Build;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.lang.reflect.Modifier;
import java.lang.reflect.Type;

/**
 * Created by zhaokaiqiang on 15/12/23.
 */
public class GsonHelper<T> {

    public static Gson mGson;

    static {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            GsonBuilder gsonBuilder = new GsonBuilder()
                    .excludeFieldsWithModifiers(
                            Modifier.FINAL,
                            Modifier.TRANSIENT,
                            Modifier.STATIC);
            mGson = gsonBuilder.create();
        } else {
            mGson = new Gson();
        }
    }


    public static String toString(Object obj) {
        return mGson.toJson(obj);
    }

    public T fromJson(String json, Class<T> classOfT) {
        return mGson.fromJson(json, classOfT);
    }

    public T fromJson(String json, Type typeOfT) {
        return mGson.fromJson(json, typeOfT);
    }

}
