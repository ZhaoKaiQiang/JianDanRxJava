package com.socks.jiandan.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import com.socks.jiandan.R;
import com.socks.jiandan.base.BaseFragment;
import com.socks.jiandan.model.FreshNews;
import com.socks.jiandan.net.JDApi;
import com.socks.jiandan.utils.IntentHelper;
import com.socks.jiandan.utils.ShareUtil;
import com.socks.jiandan.utils.TextUtil;
import com.victor.loading.rotate.RotateLoading;

import butterknife.Bind;
import butterknife.ButterKnife;

public class FreshNewsDetailFragment extends BaseFragment {

    @Bind(R.id.webView)
    WebView webView;
    @Bind(R.id.loading)
    RotateLoading loading;

    private FreshNews freshNews;

    public FreshNewsDetailFragment() {
    }

    public static FreshNewsDetailFragment getInstance(FreshNews freshNews) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(DATA_FRESH_NEWS, freshNews);
        FreshNewsDetailFragment fragment = new FreshNewsDetailFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fresh_news_detail, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(true);
        freshNews = (FreshNews) getArguments().getSerializable(DATA_FRESH_NEWS);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                if (newProgress > 80) {
                    loading.stop();
                }
            }
        });
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        loading.start();
        dismissLoading();

        JDApi.getFreshDetail(freshNews.id).subscribe(s -> {
            webView.loadDataWithBaseURL("", TextUtil.getHtml(freshNews, s), "text/html", "utf-8", "");
        }, e -> {
            loading.stop();
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (webView != null) {
            webView.onResume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (webView != null) {
            webView.onPause();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_fresh_news_detail, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_comment:
                IntentHelper.toCommentListActivity(mContext, freshNews.id);
                return true;
            case R.id.action_share:
                ShareUtil.shareText(getActivity(), freshNews.title + " " + freshNews.url);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void dismissLoading() {
        loading.postDelayed(() -> {
            if (loading.isShown()) {
                loading.stop();
            }
        }, 10 * 1000);
    }
}
