package com.socks.jiandan.net.parser;

import android.support.annotation.Nullable;

import com.google.gson.reflect.TypeToken;
import com.socks.jiandan.callback.LoadFinishCallBack;
import com.socks.jiandan.model.Comment4FreshNews;
import com.socks.jiandan.utils.GsonHelper;
import com.socks.okhttp.plus.parser.OkBaseParser;
import com.squareup.okhttp.Response;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by zhaokaiqiang on 15/11/22.
 */
public class FreshNewsCommentParser extends OkBaseParser<ArrayList<Comment4FreshNews>> {

    private LoadFinishCallBack mCallBack;

    public FreshNewsCommentParser(LoadFinishCallBack mCallBack) {
        this.mCallBack = mCallBack;
    }

    @Nullable
    public ArrayList<Comment4FreshNews> parse(Response response) {

        if (!response.isSuccessful())
            return null;

        try {
            String resultStr = response.body().string();
            JSONObject resultObj = new JSONObject(resultStr);

            String status = resultObj.optString("status");

            if (status.equals("ok")) {
                String commentsStr = resultObj.optJSONObject("post").optJSONArray("comments")
                        .toString();
                int id = resultObj.optJSONObject("post").optInt("id");
                mCallBack.loadFinish(Integer.toString(id));

                ArrayList<Comment4FreshNews> comment4FreshNewses = (ArrayList<Comment4FreshNews>) GsonHelper.toObject(commentsStr,
                        new TypeToken<ArrayList<Comment4FreshNews>>() {
                        }.getType());

                Pattern pattern = Pattern.compile("\\d{7}");

                for (Comment4FreshNews comment4FreshNews : comment4FreshNewses) {
                    Matcher matcher = pattern.matcher(comment4FreshNews.getContent());
                    boolean isHas7Num = matcher.find();
                    boolean isHasCommentStr = comment4FreshNews.getContent().contains("#comment-");
                    //有回复
                    if (isHas7Num && isHasCommentStr || comment4FreshNews.getParentId() != 0) {
                        ArrayList<Comment4FreshNews> tempComments = new ArrayList<>();
                        int parentId = getParentId(comment4FreshNews.getContent());
                        comment4FreshNews.setParentId(parentId);
                        getParenFreshNews(tempComments, comment4FreshNewses, parentId);
                        Collections.reverse(tempComments);
                        comment4FreshNews.setParentComments(tempComments);
                        comment4FreshNews.setFloorNum(tempComments.size() + 1);
                        comment4FreshNews.setContent(getContentWithParent(comment4FreshNews.getContent()));
                    } else {
                        comment4FreshNews.setContent(getContentOnlySelf(comment4FreshNews.getContent()));
                    }
                }

                return comment4FreshNewses;
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void getParenFreshNews(ArrayList<Comment4FreshNews> tempComments, ArrayList<Comment4FreshNews> comment4FreshNewses, int parentId) {

        for (Comment4FreshNews comment4FreshNews : comment4FreshNewses) {
            if (comment4FreshNews.getId() != parentId) continue;
            //找到了父评论
            tempComments.add(comment4FreshNews);
            //父评论又有父评论
            if (comment4FreshNews.getParentId() != 0 && comment4FreshNews.getParentComments() != null) {
                comment4FreshNews.setContent(getContentWithParent(comment4FreshNews.getContent()));
                tempComments.addAll(comment4FreshNews.getParentComments());
                return;
            }
            //父评论没有父评论了
            comment4FreshNews.setContent(getContentOnlySelf(comment4FreshNews.getContent()));
        }
    }


    private int getParentId(String content) {
        try {
            int index = content.indexOf("comment-") + 8;
            int parentId = Integer.parseInt(content.substring(index, index + 7));
            return parentId;
        } catch (Exception ex) {
            return 0;
        }
    }


    private String getContentWithParent(String content) {
        if (content.contains("</a>:"))
            return getContentOnlySelf(content).split("</a>:")[1];
        return content;
    }

    private String getContentOnlySelf(String content) {
        content = content.replace("</p>", "");
        content = content.replace("<p>", "");
        content = content.replace("<br />", "");
        return content;
    }

}
