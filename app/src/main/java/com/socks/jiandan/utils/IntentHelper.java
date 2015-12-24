package com.socks.jiandan.utils;

import android.content.Context;
import android.content.Intent;
import android.provider.Settings;

import com.socks.jiandan.model.FreshNews;
import com.socks.jiandan.ui.activity.FreshNewsDetailActivity;

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

}