package com.socks.jiandan.adapter;

import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.socks.jiandan.R;
import com.socks.jiandan.base.BaseActivity;
import com.socks.jiandan.base.ConstantString;
import com.socks.jiandan.cache.FreshNewsCache;
import com.socks.jiandan.callback.LoadFinishCallBack;
import com.socks.jiandan.callback.LoadResultCallBack;
import com.socks.jiandan.model.FreshNews;
import com.socks.jiandan.net.JDApi;
import com.socks.jiandan.ui.fragment.SettingFragment;
import com.socks.jiandan.utils.GsonHelper;
import com.socks.jiandan.utils.IntentHelper;
import com.socks.jiandan.utils.NetWorkUtil;
import com.socks.jiandan.utils.SPHelper;
import com.socks.jiandan.utils.ShareUtil;
import com.socks.jiandan.utils.ToastHelper;
import com.socks.jiandan.view.imageloader.ImageLoadProxy;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class FreshNewsAdapter extends RecyclerView.Adapter<FreshNewsAdapter.ViewHolder> {

    private LoadFinishCallBack<Object> mLoadFinisCallBack;
    private LoadResultCallBack mLoadResultCallBack;
    private ArrayList<FreshNews> mFreshNews;
    private DisplayImageOptions options;
    private BaseActivity mActivity;

    private int page;
    private int lastPosition = -1;
    private boolean isLargeMode;

    public FreshNewsAdapter(BaseActivity activity, LoadFinishCallBack<Object> loadFinisCallBack, LoadResultCallBack loadResultCallBack) {
        this.mActivity = activity;
        this.isLargeMode = SPHelper.getBoolean(SettingFragment.ENABLE_FRESH_BIG, true);
        this.mLoadFinisCallBack = loadFinisCallBack;
        this.mLoadResultCallBack = loadResultCallBack;
        mFreshNews = new ArrayList<>();

        int loadingResource = isLargeMode ? R.drawable.ic_loading_large : R.drawable.ic_loading_small;
        options = ImageLoadProxy.getOptions4PictureList(loadingResource);
    }

    private void setAnimation(View viewToAnimate, int position) {
        if (position > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(viewToAnimate.getContext(), R
                    .anim.item_bottom_in);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }

    @Override
    public void onViewDetachedFromWindow(ViewHolder holder) {
        if (isLargeMode) {
            assert holder.card != null;
            holder.card.clearAnimation();
        } else {
            assert holder.ll_content != null;
            holder.ll_content.clearAnimation();
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layoutId = isLargeMode ? R.layout.item_fresh_news : R.layout.item_fresh_news_small;
        View v = LayoutInflater.from(parent.getContext())
                .inflate(layoutId, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        final FreshNews freshNews = mFreshNews.get(position);

        ImageLoadProxy.displayImage(freshNews.custom_fields.thumb_m, holder.img, options);
        holder.tv_title.setText(freshNews.title);
        holder.tv_info.setText(freshNews.getInfo());
        holder.tv_views.setText(freshNews.getViews());

        if (isLargeMode) {
            assert holder.tv_share != null;
            holder.tv_share.setOnClickListener(v -> ShareUtil.shareText(mActivity, freshNews.title + " " + freshNews.url));
            assert holder.card != null;
            holder.card.setOnClickListener(v ->
                    IntentHelper.toFreshDetailActivity(mActivity, mFreshNews, position));
            setAnimation(holder.card, position);
        } else {
            assert holder.ll_content != null;
            holder.ll_content.setOnClickListener(v -> IntentHelper.toFreshDetailActivity(mActivity, mFreshNews, position));
            setAnimation(holder.ll_content, position);
        }

    }

    @Override
    public int getItemCount() {
        return mFreshNews.size();
    }

    public void loadFirst() {
        page = 1;
        loadDataByNetworkType();
    }

    public void loadNextPage() {
        page++;
        loadDataByNetworkType();
    }

    private void loadDataByNetworkType() {

        if (NetWorkUtil.isNetWorkConnected(mActivity)) {
            loadDate();
        } else {
            loadFromCache();
        }
    }

    private void loadDate() {
        Subscription subscription = JDApi.getFreshNews(page)
                .observeOn(Schedulers.io())
                .doOnNext(freshNewses -> {
                    if (page == 1) {
                        mFreshNews.clear();
                        FreshNewsCache.getInstance(mActivity).clearAllCache();
                    }
                    FreshNewsCache.getInstance(mActivity).addResultCache(GsonHelper.toString(freshNewses),
                            page);
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(freshNewses -> {
                    mFreshNews.addAll(freshNewses);
                    notifyDataSetChanged();
                    mLoadResultCallBack.onSuccess(LoadResultCallBack.SUCCESS_OK, null);
                    mLoadFinisCallBack.loadFinish(null);
                }, e -> {
                    mLoadResultCallBack.onError(LoadResultCallBack.ERROR_NET);
                    mLoadFinisCallBack.loadFinish(null);
                });
        mActivity.addSubscription(subscription);
    }

    private void loadFromCache() {
        Observable.create(new Observable.OnSubscribe<ArrayList<FreshNews>>() {
            @Override
            public void call(Subscriber<? super ArrayList<FreshNews>> subscriber) {
                subscriber.onNext(FreshNewsCache.getInstance(mActivity).getCacheByPage(page));
                subscriber.onCompleted();
            }
        }).compose(JDApi.applySchedulers())
                .doOnNext(freshNewses -> {
                    if (page == 1) {
                        mFreshNews.clear();
                        ToastHelper.Short(ConstantString.LOAD_NO_NETWORK);
                    }
                })
                .subscribe(freshNewses -> {
                    mFreshNews.addAll(freshNewses);
                    notifyDataSetChanged();
                    mLoadResultCallBack.onSuccess(LoadResultCallBack.SUCCESS_OK, null);
                    mLoadFinisCallBack.loadFinish(null);
                });
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.tv_title)
        TextView tv_title;
        @Bind(R.id.tv_info)
        TextView tv_info;
        @Bind(R.id.tv_views)
        TextView tv_views;
        @Nullable
        @Bind(R.id.tv_share)
        TextView tv_share;
        @Bind(R.id.img)
        ImageView img;
        @Nullable
        @Bind(R.id.card)
        CardView card;
        @Nullable
        @Bind(R.id.ll_content)
        LinearLayout ll_content;

        public ViewHolder(View contentView) {
            super(contentView);
            ButterKnife.bind(this, contentView);
        }
    }

}