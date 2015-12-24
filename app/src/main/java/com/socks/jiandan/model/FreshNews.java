package com.socks.jiandan.model;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 新鲜事
 * Created by zhaokaiqiang on 15/4/24.
 */
public class FreshNews implements Serializable {

    public static final String URL_FRESH_NEWS = "http://jandan.net/?oxwlxojflwblxbsapi=get_recent_posts&include=url,date,tags,author,title,comment_count,custom_fields&custom_fields=thumb_c,views&dev=1&page=";
    public static final String URL_FRESH_NEWS_DETAIL = "http://i.jandan.net/?oxwlxojflwblxbsapi=get_post&include=content&id=";

    //文章id
    public String id;
    //文章标题
    public String title;
    //文章地址
    public String url;
    //发布日期
    public String date;
    //评论数
    public String comment_count;
    //作者
    public Author author;
    //自定义字段
    public CustomFields custom_fields;
    //标签
    public Tags tags;

    public FreshNews() {
    }

    public static String getUrlFreshNews(int page) {
        return URL_FRESH_NEWS + page;
    }

    public static String getUrlFreshNewsDetail(String id) {
        return URL_FRESH_NEWS_DETAIL + id;
    }

    public String getInfo() {
        return author.name + "@" + tags.getTitle();
    }

    public String getViews() {
        return "浏览" + custom_fields.getViews() + "次";
    }

    public static ArrayList<FreshNews> parse(JSONArray postsArray) {

        ArrayList<FreshNews> freshNewses = new ArrayList<>();

        for (int i = 0; i < postsArray.length(); i++) {

            FreshNews freshNews = new FreshNews();
            JSONObject jsonObject = postsArray.optJSONObject(i);

            freshNews.id = jsonObject.optString("id");
            freshNews.url = jsonObject.optString("url");
            freshNews.title = jsonObject.optString("title");
            freshNews.date = jsonObject.optString("date");
            freshNews.comment_count = jsonObject.optString("comment_count");
            freshNews.author = Author.parse(jsonObject.optJSONObject("author"));
            freshNews.custom_fields = CustomFields.parse(jsonObject.optJSONObject("custom_fields"));
            freshNews.tags = Tags.parse(jsonObject.optJSONArray("tags"));

            freshNewses.add(freshNews);
        }
        return freshNewses;
    }

    public static ArrayList<FreshNews> parseCache(JSONArray postsArray) {

        ArrayList<FreshNews> freshNewses = new ArrayList<>();

        for (int i = 0; i < postsArray.length(); i++) {

            FreshNews freshNews = new FreshNews();
            JSONObject jsonObject = postsArray.optJSONObject(i);

            freshNews.id = jsonObject.optString("id");
            freshNews.url = jsonObject.optString("url");
            freshNews.title = jsonObject.optString("title");
            freshNews.date = jsonObject.optString("date");
            freshNews.comment_count = jsonObject.optString("comment_count");
            freshNews.author = Author.parse(jsonObject.optJSONObject("author"));
            freshNews.custom_fields = CustomFields.parseCache(jsonObject.optJSONObject("custom_fields"));
            freshNews.tags = Tags.parseCache(jsonObject.optJSONObject("tags"));

            freshNewses.add(freshNews);
        }
        return freshNewses;
    }

}
