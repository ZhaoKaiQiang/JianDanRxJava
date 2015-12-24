package com.socks.jiandan.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;

import com.socks.jiandan.base.BaseActivity;
import com.socks.jiandan.model.FreshNews;
import com.socks.jiandan.ui.activity.CommentListActivity;
import com.socks.jiandan.ui.activity.FreshNewsDetailActivity;
import com.socks.jiandan.ui.activity.PushCommentActivity;

import java.util.ArrayList;

/**
 * Created by zhaokaiqiang on 15/12/22.
 */
public class IntentHelper {

    public static void toSettingActivity(Context context) {
        Intent intent = new Intent(
                Settings.ACTION_WIRELESS_SETTINGS);
        context.startActivity(intent);
    }

    public static void toFreshDetailActivity(Context context, ArrayList<FreshNews> mFreshNews, int position) {
        Intent intent = new Intent(context, FreshNewsDetailActivity.class);
        intent.putExtra(FreshNewsDetailActivity.DATA_FRESH_NEWS, mFreshNews);
        intent.putExtra(FreshNewsDetailActivity.DATA_POSITION, position);
        context.startActivity(intent);
    }

    public static void toCommentListActivity(Context context, String id) {
        Intent intent = new Intent(context, CommentListActivity.class);
        intent.putExtra(BaseActivity.DATA_THREAD_ID, id);
        intent.putExtra(BaseActivity.DATA_IS_FROM_FRESH_NEWS, true);
        context.startActivity(intent);
    }

    public static void toPushComment4Result(Activity context, String postId, String threadId, String parentName){
        Intent intent = new Intent
                (context, PushCommentActivity.class);
        intent.putExtra("parent_id", postId);
        intent.putExtra("thread_id", threadId);
        intent.putExtra("parent_name", parentName);
        context.startActivityForResult(intent, 0);
    }

}