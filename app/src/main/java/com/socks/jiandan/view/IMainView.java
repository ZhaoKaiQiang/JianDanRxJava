package com.socks.jiandan.view;

import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;

/**
 * Created by zhaokaiqiang on 15/12/22.
 */
public interface IMainView {

    void closeDrawer();

    void replaceFragment(@IdRes int id, Fragment fragment);

}
