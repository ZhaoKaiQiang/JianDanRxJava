package com.socks.jiandan.net.parser;

import android.support.annotation.Nullable;

import com.google.gson.reflect.TypeToken;
import com.socks.jiandan.model.Joke;
import com.socks.jiandan.utils.GsonHelper;
import com.socks.okhttp.plus.parser.OkBaseParser;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;

import okhttp3.Response;

/**
 * Created by zhaokaiqiang on 15/11/22.
 */
public class JokeParser extends OkBaseParser<ArrayList<Joke>> {

    @Nullable
    public ArrayList<Joke> parse(Response response) {

        if (!response.isSuccessful())
            return null;

        try {
            String jsonStr = response.body().string();
            jsonStr = new JSONObject(jsonStr).getJSONArray("comments").toString();

            Type type = new TypeToken<ArrayList<Joke>>() {
            }.getType();

            return new GsonHelper<ArrayList<Joke>>().fromJson(jsonStr, type);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
