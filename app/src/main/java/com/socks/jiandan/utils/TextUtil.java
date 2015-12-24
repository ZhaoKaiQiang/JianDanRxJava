package com.socks.jiandan.utils;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;

import com.socks.jiandan.base.ConstantString;
import com.socks.jiandan.model.FreshNews;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextUtil {

    public static final String REG_EMAIL = "\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*";


    public static boolean isEmail(String email) {
        Pattern p = Pattern.compile(REG_EMAIL);
        Matcher m = p.matcher(email);
        return m.matches();
    }


    /**
     * 判断是否为null、空字符串或者是"null"
     *
     * @param str
     * @return
     */
    public static boolean isNull(CharSequence... str) {

        for (CharSequence cha : str) {
            if (cha == null || cha.length() == 0 || cha.equals("null")) {
                return true;
            } else {
                return false;
            }
        }

        return true;
    }

    public static void copy(Activity activity, String copyText) {
        ClipboardManager clip = (ClipboardManager)
                activity.getSystemService(Context
                        .CLIPBOARD_SERVICE);
        clip.setPrimaryClip(ClipData.newPlainText
                (null, copyText));
        ToastHelper.Short(ConstantString.COPY_SUCCESS);
    }

    public static String getHtml(FreshNews freshNews, String content) {
        final StringBuilder sb = new StringBuilder();
        sb.append("<!DOCTYPE html>");
        sb.append("<html dir=\"ltr\" lang=\"zh\">");
        sb.append("<head>");
        sb.append("<meta name=\"viewport\" content=\"width=100%; initial-scale=1.0; maximum-scale=1.0; user-scalable=0;\" />");
        sb.append("<link rel=\"stylesheet\" href='file:///android_asset/style.css' type=\"text/css\" media=\"screen\" />");
        sb.append("</head>");
        sb.append("<body style=\"padding:0px 8px 8px 8px;\">");
        sb.append("<div id=\"pagewrapper\">");
        sb.append("<div id=\"mainwrapper\" class=\"clearfix\">");
        sb.append("<div id=\"maincontent\">");
        sb.append("<div class=\"post\">");
        sb.append("<div class=\"posthit\">");
        sb.append("<div class=\"postinfo\">");
        sb.append("<h2 class=\"thetitle\">");
        sb.append("<a>");
        sb.append(freshNews.title);
        sb.append("</a>");
        sb.append("</h2>");
        sb.append(freshNews.author.getName() + " @ " + String2TimeUtil
                .dateString2GoodExperienceFormat(freshNews.date));
        sb.append("</div>");
        sb.append("<div class=\"entry\">");
        sb.append(content);
        sb.append("</div>");
        sb.append("</div>");
        sb.append("</div>");
        sb.append("</div>");
        sb.append("</div>");
        sb.append("</div>");
        sb.append("</body>");
        sb.append("</html>");
        return sb.toString();
    }

}