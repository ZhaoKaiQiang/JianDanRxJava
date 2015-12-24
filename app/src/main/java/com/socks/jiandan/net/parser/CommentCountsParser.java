package com.socks.jiandan.net.parser;

import com.socks.jiandan.model.CommentNumber;
import com.socks.okhttp.plus.parser.OkBaseParser;
import com.squareup.okhttp.Response;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by zhaokaiqiang on 15/11/22.
 */
public class CommentCountsParser extends OkBaseParser<ArrayList<CommentNumber>> {
    @Override
    public ArrayList<CommentNumber> parse(Response response) {

        if (!response.isSuccessful())
            return null;

        try {
            String body = response.body().string();
            JSONObject jsonObject = new JSONObject(body).getJSONObject("response");
            String[] comment_IDs = response.request().urlString().split("\\=")[1].split("\\,");
            ArrayList<CommentNumber> commentNumbers = new ArrayList<>();

            for (String comment_ID : comment_IDs) {

                if (!jsonObject.isNull(comment_ID)) {
                    CommentNumber commentNumber = new CommentNumber();
                    commentNumber.setComments(jsonObject.getJSONObject(comment_ID).getInt(CommentNumber.COMMENTS));
                    commentNumber.setThread_id(jsonObject.getJSONObject(comment_ID).getString(CommentNumber.THREAD_ID));
                    commentNumber.setThread_key(jsonObject.getJSONObject(comment_ID).getString(CommentNumber.THREAD_KEY));
                    commentNumbers.add(commentNumber);
                } else {
                    //可能会出现没有对应id的数据的情况，为了保证条数一致，添加默认数据
                    commentNumbers.add(new CommentNumber("0", "0", 0));
                }
            }
            return commentNumbers;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
