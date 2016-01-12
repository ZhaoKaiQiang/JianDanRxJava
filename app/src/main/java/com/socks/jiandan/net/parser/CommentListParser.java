package com.socks.jiandan.net.parser;

import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.socks.jiandan.callback.LoadFinishCallBack;
import com.socks.jiandan.model.Commentator;
import com.socks.jiandan.utils.TextUtil;
import com.socks.okhttp.plus.parser.OkBaseParser;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.Response;

/**
 * Created by zhaokaiqiang on 15/11/22.
 */
public class CommentListParser extends OkBaseParser<ArrayList<Commentator>> {

    private LoadFinishCallBack<String> mCallBack;

    public CommentListParser(LoadFinishCallBack<String> callBack) {
        this.mCallBack = callBack;
    }

    @Nullable
    @Override
    public ArrayList<Commentator> parse(Response response) {

        if (!response.isSuccessful())
            return null;


        try {
            //获取到所有的数据
            String jsonStr = response.body().string();
            JSONObject resultJson = new JSONObject(jsonStr);
            String allThreadId = resultJson.getString("response").replace("[", "").replace
                    ("]", "").replace("\"", "");
            String[] threadIds = allThreadId.split("\\,");

            mCallBack.loadFinish(resultJson.optJSONObject("thread").optString("thread_id"));

            if (TextUtils.isEmpty(threadIds[0])) {
                return new ArrayList<>();
            } else {

                //然后根据thread_id再去获得对应的评论和作者信息
                JSONObject parentPostsJson = resultJson.getJSONObject("parentPosts");
                //找出热门评论
                String hotPosts = resultJson.getString("hotPosts").replace("[", "").replace
                        ("]", "").replace("\"", "");
                String[] allHotPosts = hotPosts.split("\\,");

                ArrayList<Commentator> commentators = new ArrayList<>();
                List<String> allHotPostsArray = Arrays.asList(allHotPosts);

                for (String threadId : threadIds) {
                    Commentator commentator = new Commentator();
                    JSONObject threadObject = parentPostsJson.getJSONObject(threadId);

                    //解析评论，打上TAG
                    if (allHotPostsArray.contains(threadId)) {
                        commentator.setTag(Commentator.TAG_HOT);
                    } else {
                        commentator.setTag(Commentator.TAG_NORMAL);
                    }

                    commentator.setPost_id(threadObject.optString("post_id"));
                    commentator.setParent_id(threadObject.optString("parent_id"));

                    String parentsString = threadObject.optString("parents").replace("[", "").replace
                            ("]", "").replace("\"", "");

                    String[] parents = parentsString.split("\\,");
                    commentator.setParents(parents);

                    //如果第一个数据为空，则只有一层
                    if (TextUtil.isNull(parents[0])) {
                        commentator.setFloorNum(1);
                    } else {
                        commentator.setFloorNum(parents.length + 1);
                    }

                    commentator.setMessage(threadObject.optString("message"));
                    commentator.setCreated_at(threadObject.optString("created_at"));
                    JSONObject authorObject = threadObject.optJSONObject("author");
                    commentator.setName(authorObject.optString("name"));
                    commentator.setAvatar_url(authorObject.optString("avatar_url"));
                    commentator.setType(Commentator.TYPE_NORMAL);
                    commentators.add(commentator);
                }

                return commentators;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
