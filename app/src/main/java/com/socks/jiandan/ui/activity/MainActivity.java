package com.socks.jiandan.ui.activity;

import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.socks.jiandan.JDApplication;
import com.socks.jiandan.R;
import com.socks.jiandan.base.BaseActivity;
import com.socks.jiandan.model.NetWorkEvent;
import com.socks.jiandan.receiver.NetStateReceiver;
import com.socks.jiandan.receiver.RxNetWorkEvent;
import com.socks.jiandan.ui.fragment.FreshNewsFragment;
import com.socks.jiandan.ui.fragment.MainMenuFragment;
import com.socks.jiandan.utils.IntentHelper;
import com.socks.jiandan.utils.ToastHelper;
import com.socks.jiandan.view.IMainView;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.CompositeSubscription;

public class MainActivity extends BaseActivity implements IMainView {

    private static final String TAG_MENU = "menu";
    private static final String TAG_FRESH_NEWS = "fresh_news";

    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;

    private ActionBarDrawerToggle mActionBarDrawerToggle;
    private CompositeSubscription mRxBusComposite;
    private NetStateReceiver netStateReceiver;
    private MaterialDialog noNetWorkDialog;
    private long exitTime;

    @Override
    protected void initView(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mToolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mActionBarDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.app_name,
                R.string.app_name) {
            @Override
            public void onDrawerClosed(View drawerView) {
                invalidateOptionsMenu();
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                invalidateOptionsMenu();
            }
        };

        mActionBarDrawerToggle.syncState();
        mDrawerLayout.setDrawerListener(mActionBarDrawerToggle);

        if (savedInstanceState == null) {
            addFragment(R.id.frame_container, new FreshNewsFragment(), TAG_MENU);
            addFragment(R.id.drawer_container, new MainMenuFragment(), TAG_FRESH_NEWS);
        }
    }

    @Override
    protected void loadData() {
        netStateReceiver = new NetStateReceiver();
        registerReceiver(netStateReceiver, new IntentFilter(
                ConnectivityManager.CONNECTIVITY_ACTION));

    }

    @Override
    protected void onStart() {
        super.onStart();
        mRxBusComposite = new CompositeSubscription();
        Subscription sub = RxNetWorkEvent.toObserverable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(netWorkEvent -> {
                    if (netWorkEvent.getType() == NetWorkEvent.UNAVAILABLE) {
                        if (noNetWorkDialog == null) {
                            noNetWorkDialog = new MaterialDialog.Builder(MainActivity.this)
                                    .title(R.string.no_network)
                                    .content(R.string.open_network)
                                    .backgroundColor(getResources().getColor(JDApplication.COLOR_OF_DIALOG))
                                    .contentColor(JDApplication.COLOR_OF_DIALOG_CONTENT)
                                    .positiveColor(JDApplication.COLOR_OF_DIALOG_CONTENT)
                                    .negativeColor(JDApplication.COLOR_OF_DIALOG_CONTENT)
                                    .titleColor(JDApplication.COLOR_OF_DIALOG_CONTENT)
                                    .negativeText(R.string.no).positiveText(R.string.yes)
                                    .onPositive((dialog, which) -> IntentHelper.toSettingActivity(mContext))
                                    .cancelable(false)
                                    .build();
                        }
                        if (!noNetWorkDialog.isShowing()) {
                            noNetWorkDialog.show();
                        }
                    }
                });
        mRxBusComposite.add(sub);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mRxBusComposite.unsubscribe();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(netStateReceiver);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - exitTime) > 1500) {
                ToastHelper.Short(R.string.one_more_exit);
                exitTime = System.currentTimeMillis();
            } else {
                finish();
            }
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void closeDrawer() {
        mDrawerLayout.closeDrawers();
    }

    ///////////////////////////////////////////////////////////////////////////
    // Drawer Method
    ///////////////////////////////////////////////////////////////////////////

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mActionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mActionBarDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mActionBarDrawerToggle.onConfigurationChanged(newConfig);
    }
}
