package com.socks.jiandan.base;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;

import com.socks.jiandan.JDApplication;
import com.socks.jiandan.R;

/**
 * Created by zhaokaiqiang on 15/12/22.
 */
public abstract class BaseActivity extends AppCompatActivity implements ConstantString{

    protected Context mContext;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        initView(savedInstanceState);
        loadData();
    }

    protected abstract void initView(@Nullable Bundle savedInstanceState);

    protected abstract void loadData();

    @Override
    protected void onDestroy() {
        super.onDestroy();
        JDApplication.getRefWatcher(this).watch(this);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.anim_none, R.anim.trans_center_2_right);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Fragment Method
    ///////////////////////////////////////////////////////////////////////////

    protected Fragment findFragmentByTag(String tag) {
        FragmentManager manager = getSupportFragmentManager();
        return manager.findFragmentByTag(tag);
    }

    protected void addFragment(@IdRes int containerId, Fragment fragment, String tag) {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.add(containerId, fragment, tag);
        transaction.commit();
    }

    protected void showFragment(Fragment fragment) {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.show(fragment);
        transaction.commit();
    }

    protected void hideFragment(Fragment fragment) {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.hide(fragment);
        transaction.commit();
    }

    protected void removeFragment(Fragment fragment) {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.remove(fragment);
        transaction.commit();
    }

    protected void hideFragmentByTag(String... tags) {

        if (tags.length <= 0) {
            return;
        }

        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        for (String tag : tags) {
            if (TextUtils.isEmpty(tag)) {
                continue;
            }
            Fragment fragment = manager.findFragmentByTag(tag);
            if (fragment != null) {
                transaction.hide(fragment);
            }
        }
        transaction.commit();
    }

    public void replaceFragment(@IdRes int id_content, Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(id_content, fragment);
        transaction.commit();
    }
}
