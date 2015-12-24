package com.socks.jiandan.ui.viewInterface;

/**
 * Created by zhaokaiqiang on 15/12/23.
 */
public interface IFreshNewsList {

    void loadSuccess(int code, Object object);

    void loadError(int code);

    void loadFirstPage();

}
