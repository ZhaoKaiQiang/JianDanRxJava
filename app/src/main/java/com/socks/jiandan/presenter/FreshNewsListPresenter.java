package com.socks.jiandan.presenter;

import com.socks.jiandan.callback.LoadResultCallBack;
import com.socks.jiandan.ui.viewInterface.IFreshNewsList;

/**
 * Created by zhaokaiqiang on 15/12/23.
 */
public class FreshNewsListPresenter implements LoadResultCallBack {

    private IFreshNewsList mFreshNewsList;

    public FreshNewsListPresenter(IFreshNewsList freshNewsList) {
        mFreshNewsList = freshNewsList;
    }

    @Override
    public void onSuccess(int result, Object object) {
        mFreshNewsList.loadSuccess(result, object);
    }

    @Override
    public void onError(int code) {
        mFreshNewsList.loadError(code);
    }

    public void loadFirstPage() {
        mFreshNewsList.loadFirstPage();
    }
}
