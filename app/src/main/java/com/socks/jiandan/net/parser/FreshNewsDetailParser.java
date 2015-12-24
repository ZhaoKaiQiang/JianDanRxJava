package com.socks.jiandan.net.parser;

import android.support.annotation.Nullable;

import com.socks.okhttp.plus.parser.OkBaseParser;
import com.squareup.okhttp.Response;

import org.json.JSONObject;

/**
 * Created by zhaokaiqiang on 15/11/22.
 */
public class FreshNewsDetailParser extends OkBaseParser<String> {

    @Nullable
    public String parse(Response response) {

        if (!response.isSuccessful())
            return null;

        try {
            JSONObject jsonObject = new JSONObject(response.body().string());
            if (jsonObject.opt("status").equals("ok")) {
                JSONObject contentObject = jsonObject.optJSONObject("post");
                return contentObject.optString("content");
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
