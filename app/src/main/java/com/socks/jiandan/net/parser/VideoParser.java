package com.socks.jiandan.net.parser;

import android.support.annotation.Nullable;

import com.socks.jiandan.model.Video;
import com.socks.okhttp.plus.parser.OkBaseParser;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by zhaokaiqiang on 15/11/22.
 */
public class VideoParser extends OkBaseParser<ArrayList<Video>> {

    @Nullable
    public ArrayList<Video> parse(Response response) {

        try {
            String jsonStr = response.body().string();
            JSONObject jsonObject = new JSONObject(jsonStr);

            if ("ok".equals(jsonObject.optString("status"))) {

                JSONArray commentsArray = jsonObject.optJSONArray("comments");
                ArrayList<Video> videos = new ArrayList<>();

                for (int i = 0; i < commentsArray.length(); i++) {

                    JSONObject commentObject = commentsArray.getJSONObject(i);
                    JSONObject videoObject = commentObject.optJSONArray("videos").optJSONObject(0);

                    if (videoObject != null) {
                        Video video = new Video();
                        video.setTitle(videoObject.optString("title"));
                        String videoSource = videoObject.optString("video_source");
                        video.setComment_ID(commentObject.optString("comment_ID"));
                        video.setVote_positive(commentObject.optString("vote_positive"));
                        video.setVote_negative(commentObject.optString("vote_negative"));
                        video.setVideo_source(videoSource);

                        if (videoSource.equals("youku")) {
                            video.setUrl(videoObject.optString("link"));
                            video.setDesc(videoObject.optString("description"));
                            video.setImgUrl(videoObject.optString("thumbnail"));
                            video.setImgUrl4Big(videoObject.optString("thumbnail_v2"));
                        } else if (videoSource.equals("56")) {
                            video.setUrl(videoObject.optString("url"));
                            video.setDesc(videoObject.optString("desc"));
                            video.setImgUrl4Big(videoObject.optString("img"));
                            video.setImgUrl(videoObject.optString("mimg"));
                        } else if (videoSource.equals("tudou")) {
                            video.setUrl(videoObject.optString("playUrl"));
                            video.setImgUrl(videoObject.optString("picUrl"));
                            video.setImgUrl4Big(videoObject.optString("picUrl"));
                            video.setDesc(videoObject.optString("description"));
                        }

                        videos.add(video);
                    }
                }

                return videos;
            } else {
                return new ArrayList<>();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
