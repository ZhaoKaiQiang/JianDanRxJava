package com.socks.jiandan.ui.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import com.afollestad.materialdialogs.MaterialDialog;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.socks.jiandan.JDApplication;
import com.socks.jiandan.R;
import com.socks.jiandan.utils.AppInfoUtil;
import com.socks.jiandan.utils.FileUtil;
import com.socks.jiandan.utils.ToastHelper;

import java.io.File;
import java.text.DecimalFormat;

public class SettingFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener {

    public static final String CLEAR_CACHE = "clear_cache";
    public static final String ABOUT_APP = "about_app";
    public static final String APP_VERSION = "app_version";
    public static final String ENABLE_SISTER = "enable_sister";
    public static final String ENABLE_FRESH_BIG = "enable_fresh_big";

    private Preference clearCache;
    private Preference aboutApp;
    private Preference appVersion;
    private CheckBoxPreference enableSister;
    private CheckBoxPreference enableBig;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        clearCache = findPreference(CLEAR_CACHE);
        aboutApp = findPreference(ABOUT_APP);
        appVersion = findPreference(APP_VERSION);
        enableSister = (CheckBoxPreference) findPreference(ENABLE_SISTER);
        enableBig = (CheckBoxPreference) findPreference(ENABLE_FRESH_BIG);

        appVersion.setTitle(AppInfoUtil.getVersionName(getActivity()));

        File cacheFile = ImageLoader.getInstance().getDiskCache().getDirectory();
        DecimalFormat decimalFormat = new DecimalFormat("#0.00");
        clearCache.setSummary(getString(R.string.cache_size) + decimalFormat.format(FileUtil.getDirSize(cacheFile)) + "M");

        enableSister.setOnPreferenceChangeListener((preference, newValue) -> {
            ToastHelper.Short(((Boolean) newValue) ? getString(R.string.open_sister_mode) : getString(R.string.close_sister_mode));
            return true;
        });

        enableBig.setOnPreferenceChangeListener((preference, newValue) -> {
            ToastHelper.Short(((Boolean) newValue) ? getString(R.string.picture_mode_big) : getString(R.string.picture_mode_small));
            return true;
        });

        clearCache.setOnPreferenceClickListener(this);
        aboutApp.setOnPreferenceClickListener(this);
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {

        String key = preference.getKey();

        if (key.equals(CLEAR_CACHE)) {
            ImageLoader.getInstance().clearDiskCache();
            ToastHelper.Short(R.string.clear_cache);
            clearCache.setSummary("缓存大小：0.00M");
        } else if (key.equals(ABOUT_APP)) {

            MaterialDialog dialog = new MaterialDialog.Builder(getActivity())
                    .title(getString(R.string.app_name))
                    .backgroundColor(getResources().getColor(JDApplication.COLOR_OF_DIALOG))
                    .contentColor(JDApplication.COLOR_OF_DIALOG_CONTENT)
                    .positiveColor(JDApplication.COLOR_OF_DIALOG_CONTENT)
                    .negativeColor(JDApplication.COLOR_OF_DIALOG_CONTENT)
                    .neutralColor(JDApplication.COLOR_OF_DIALOG_CONTENT)
                    .titleColor(JDApplication.COLOR_OF_DIALOG_CONTENT)
                    .content(R.string.person_info)
                    .positiveText("GitHub")
                    .negativeText("WeiBo")
                    .neutralText("CSDN")
                    .onPositive((dialog1, which) -> {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/ZhaoKaiQiang/JianDan")));
                        dialog1.dismiss();
                    })
                    .onNegative((dialog1, which) -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://weibo.com/zhaokaiqiang1992"))))
                    .onNeutral((dialog1, which) -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://weibo.com/zhaokaiqiang1992"))))
                    .build();
            dialog.show();
        }
        return true;
    }

}
