package com.socks.jiandan.model;


import com.socks.jiandan.view.floorview.Commentable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class Comment4FreshNews extends Commentator implements Comparable, Commentable {

    //评论列表
    public static final String URL_COMMENTS = "http://jandan.net/?oxwlxojflwblxbsapi=get_post&include=comments&id=";
    //对新鲜事发表评论
    public static final String URL_PUSH_COMMENT = "http://jandan.net/?oxwlxojflwblxbsapi=respond.submit_comment";

    private int id;
    private String url;
    private String date;
    private String content;
    private String parent;
    private int parentId;
    private ArrayList<Comment4FreshNews> parentComments;
    private int vote_positive;

    public Comment4FreshNews() {
    }

    public static String getUrlComments(String id) {
        return URL_COMMENTS + id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public int getVote_positive() {
        return vote_positive;
    }

    public ArrayList<Comment4FreshNews> getParentComments() {
        return parentComments;
    }

    public void setParentComments(ArrayList<Comment4FreshNews> parentComments) {
        this.parentComments = parentComments;
    }

    public int getParentId() {
        return parentId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    @Override
    public int compareTo(Object another) {
        String anotherTimeString = ((Comment4FreshNews) another).getDate();
        String thisTimeString = getDate();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT+08"));

        try {
            Date anotherDate = simpleDateFormat.parse(anotherTimeString);
            Date thisDate = simpleDateFormat.parse(thisTimeString);
            return -thisDate.compareTo(anotherDate);
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static void generateCommentator(ArrayList<Comment4FreshNews> commentFrom,ArrayList<Comment4FreshNews> commentTo) {
        commentTo.clear();

        //如果评论条数大于6，就选择positive前6作为热门评论
        if (commentFrom.size() > 6) {
            Comment4FreshNews comment4FreshNews = new Comment4FreshNews();
            comment4FreshNews.setType(Comment4FreshNews.TYPE_HOT);
            commentTo.add(comment4FreshNews);

            Collections.sort(commentFrom, (lhs, rhs) -> lhs.getVote_positive() <= rhs.getVote_positive() ? 1 :
                    -1);
            List<Comment4FreshNews> subComments = commentFrom.subList(0, 6);

            for (Comment4FreshNews subComment : subComments) {
                subComment.setTag(Comment4FreshNews.TAG_HOT);
            }
            commentTo.addAll(subComments);
        }

        Comment4FreshNews comment4FreshNews = new Comment4FreshNews();
        comment4FreshNews.setType(Comment4FreshNews.TYPE_NEW);
        commentTo.add(comment4FreshNews);

        Collections.sort(commentFrom);

        for (Comment4FreshNews comment4Normal : commentFrom) {
            if (comment4Normal.getTag().equals(Comment4FreshNews.TAG_NORMAL)) {
                commentTo.add(comment4Normal);
            }
        }
    }

    @Override
    public int getCommentFloorNum() {
        return getFloorNum();
    }

    @Override
    public String getCommentContent() {
        return getContent();
    }

    @Override
    public String getAuthorName() {
        return getName();
    }
}
