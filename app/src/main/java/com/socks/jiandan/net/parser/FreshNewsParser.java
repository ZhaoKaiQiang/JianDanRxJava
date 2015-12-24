package com.socks.jiandan.net.parser;

import android.support.annotation.Nullable;

import com.socks.jiandan.model.FreshNews;
import com.socks.okhttp.plus.parser.OkBaseParser;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by zhaokaiqiang on 15/11/22.
 */
public class FreshNewsParser extends OkBaseParser<ArrayList<FreshNews>> {

    @Nullable
    public ArrayList<FreshNews> parse(Response response) {

        if (!response.isSuccessful())
            return null;

        try {
            String body = response.body().string();
            JSONObject resultObj = new JSONObject(body);
            JSONArray postsArray = resultObj.optJSONArray("posts");
            return FreshNews.parse(postsArray);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
