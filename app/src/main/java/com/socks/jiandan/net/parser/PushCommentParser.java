package com.socks.jiandan.net.parser;

import android.support.annotation.Nullable;

import com.socks.okhttp.plus.parser.OkBaseParser;

import org.json.JSONObject;

import java.util.HashMap;

import okhttp3.Response;

/**
 * Created by zhaokaiqiang on 15/11/22.
 */
public class PushCommentParser extends OkBaseParser<Boolean> {

    @Nullable
    public Boolean parse(Response response) {

        if (!response.isSuccessful())
            return false;

        try {
            JSONObject resultObj = new JSONObject(response.body().string());
            int code = resultObj.optInt("code");
            if (code == 0) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 包装请求参数
     *
     * @param thread_id
     * @return
     */
    public static HashMap<String, String> getRequestParams(String thread_id, String parent_id,
                                                           String author_name, String author_email,
                                                           String message) {
        HashMap<String, String> params = new HashMap<>();
        params.put("thread_id", thread_id);
        params.put("parent_id", parent_id);
        params.put("author_name", author_name);
        params.put("author_email", author_email);
        params.put("message", message);

        return params;
    }

    /**
     * 包装无父评论的请求参数
     *
     * @param thread_id
     * @param author_name
     * @param author_email
     * @param message
     * @return
     */
    public static HashMap<String, String> getRequestParamsNoParent(String thread_id,
                                                                   String author_name, String author_email,
                                                                   String message) {
        HashMap<String, String> params = new HashMap<>();
        params.put("thread_id", thread_id);
        params.put("author_name", author_name);
        params.put("author_email", author_email);
        params.put("message", message);

        return params;
    }

}
