package com.socks.jiandan.net.parser;

import android.support.annotation.Nullable;

import com.google.gson.reflect.TypeToken;
import com.socks.jiandan.model.Picture;
import com.socks.jiandan.utils.GsonHelper;
import com.socks.okhttp.plus.parser.OkBaseParser;
import com.squareup.okhttp.Response;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by zhaokaiqiang on 15/11/22.
 */
public class PictureParser extends OkBaseParser<ArrayList<Picture>> {

    @Nullable
    public ArrayList<Picture> parse(Response response) {

        if (!response.isSuccessful())
            return null;

        try {
            String jsonStr = response.body().string();
            jsonStr = new JSONObject(jsonStr).getJSONArray("comments").toString();

            Type type = new TypeToken<ArrayList<Picture>>() {
            }.getType();

            return new GsonHelper<ArrayList<Picture>>().fromJson(jsonStr, type);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
