package com.socks.jiandan.net.parser;

import android.support.annotation.Nullable;

import com.google.gson.reflect.TypeToken;
import com.socks.jiandan.model.Joke;
import com.socks.jiandan.utils.JSONParser;
import com.socks.okhttp.plus.parser.OkBaseParser;
import com.squareup.okhttp.Response;

import org.json.JSONObject;

import java.util.ArrayList;

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
            return (ArrayList<Joke>) JSONParser.toObject(jsonStr,
                    new TypeToken<ArrayList<Joke>>() {
                    }.getType());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
