package com.socks.jiandan.model;

import com.google.gson.annotations.SerializedName;

public class CommentNumber {

    public static final String URL_COMMENT_COUNTS = "http://jandan.duoshuo.com/api/threads/counts.json?threads=";

    @SerializedName("comments")
    public int comments;
    @SerializedName("thread_id")
    public String thread_id;
    @SerializedName("thread_key")
    public String thread_key;

    public CommentNumber() {
    }

    public CommentNumber(String thread_id, String thread_key, int comments) {
        this.thread_id = thread_id;
        this.thread_key = thread_key;
        this.comments = comments;
    }

    public static String getCommentCountsURL(String params) {
        return URL_COMMENT_COUNTS + params;
    }

}
