package com.socks.jiandan;

import android.app.Application;
import android.content.Context;
import android.graphics.Color;

import com.socks.greendao.DaoMaster;
import com.socks.greendao.DaoSession;
import com.socks.jiandan.cache.BaseCache;
import com.socks.jiandan.utils.SPHelper;
import com.socks.jiandan.utils.StrictModeUtil;
import com.socks.jiandan.view.imageloader.ImageLoadProxy;
import com.socks.library.KLog;
import com.socks.okhttp.plus.OkHttpProxy;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;
import com.squareup.okhttp.OkHttpClient;

import java.util.concurrent.TimeUnit;

/**
 * Created by zhaokaiqiang on 15/12/22.
 */
public class JDApplication extends Application {

    public static int COLOR_OF_DIALOG = R.color.primary;
    public static int COLOR_OF_DIALOG_CONTENT = Color.WHITE;

    private static Context mContext;
    private static DaoMaster daoMaster;
    private static DaoSession daoSession;

    private RefWatcher refWatcher;

    @Override
    public void onCreate() {
        StrictModeUtil.init();
        super.onCreate();
        mContext = this;

        refWatcher = LeakCanary.install(this);
        ImageLoadProxy.initImageLoader(this);
        initHttpClient();
        SPHelper.init(mContext);
        KLog.init(BuildConfig.DEBUG);
    }

    private void initHttpClient() {
        OkHttpClient client = OkHttpProxy.getInstance();
        client.setConnectTimeout(30, TimeUnit.SECONDS);
        client.setWriteTimeout(30, TimeUnit.SECONDS);
        client.setReadTimeout(30, TimeUnit.SECONDS);
        client.setRetryOnConnectionFailure(true);
    }

    public static Context getContext() {
        return mContext;
    }

    public static RefWatcher getRefWatcher(Context context) {
        JDApplication application = (JDApplication) context.getApplicationContext();
        return application.refWatcher;
    }


    public static DaoMaster getDaoMaster(Context context) {
        if (daoMaster == null) {
            DaoMaster.OpenHelper helper = new DaoMaster.DevOpenHelper(context, BaseCache.DB_NAME, null);
            daoMaster = new DaoMaster(helper.getWritableDatabase());
        }
        return daoMaster;
    }

    public static DaoSession getDaoSession(Context context) {
        if (daoSession == null) {
            if (daoMaster == null) {
                daoMaster = getDaoMaster(context);
            }
            daoSession = daoMaster.newSession();
        }
        return daoSession;
    }

}