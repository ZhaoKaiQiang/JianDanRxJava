package com.socks.jiandan.adapter;

import android.content.Intent;
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

import com.afollestad.materialdialogs.MaterialDialog;
import com.socks.jiandan.JDApplication;
import com.socks.jiandan.R;
import com.socks.jiandan.base.BaseActivity;
import com.socks.jiandan.base.ConstantString;
import com.socks.jiandan.cache.VideoCache;
import com.socks.jiandan.callback.LoadFinishCallBack;
import com.socks.jiandan.callback.LoadResultCallBack;
import com.socks.jiandan.model.Video;
import com.socks.jiandan.net.JDApi;
import com.socks.jiandan.ui.activity.CommentListActivity;
import com.socks.jiandan.ui.activity.VideoDetailActivity;
import com.socks.jiandan.utils.GsonHelper;
import com.socks.jiandan.utils.NetWorkUtil;
import com.socks.jiandan.utils.ShareUtil;
import com.socks.jiandan.utils.TextUtil;
import com.socks.jiandan.utils.ToastHelper;
import com.socks.jiandan.view.imageloader.ImageLoadProxy;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoViewHolder> {

    private int page;
    private ArrayList<Video> mVideos;
    private int lastPosition = -1;
    private BaseActivity mActivity;
    private LoadResultCallBack mLoadResultCallBack;
    private LoadFinishCallBack<Object> mLoadFinisCallBack;

    public VideoAdapter(BaseActivity activity, LoadResultCallBack loadResultCallBack, LoadFinishCallBack<Object> loadFinisCallBack) {
        mActivity = activity;
        mLoadFinisCallBack = loadFinisCallBack;
        mLoadResultCallBack = loadResultCallBack;
        mVideos = new ArrayList<>();
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
    public void onViewDetachedFromWindow(VideoViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        holder.card.clearAnimation();
    }

    @Override
    public VideoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_video, parent, false);
        return new VideoViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final VideoViewHolder holder, final int position) {

        final Video video = mVideos.get(position);
        holder.tv_title.setText(video.getTitle());
        holder.tv_comment_count.setText(video.getComment_count());

        ImageLoadProxy.displayImageWithLoadingPicture(video.getImgUrl(), holder.img, R.drawable.ic_loading_small);

        holder.tv_like.setText(video.getVote_positive());
        holder.tv_unlike.setText(video.getVote_negative());
        holder.ll_comment.setOnClickListener(v -> {
            Intent intent = new Intent(mActivity, CommentListActivity.class);
            intent.putExtra(BaseActivity.DATA_THREAD_KEY, "comment-" + video.getComment_ID());
            mActivity.startActivity(intent);
        });
        holder.img_share.setOnClickListener(v -> new MaterialDialog.Builder(mActivity)
                .items(R.array.joke_dialog)
                .backgroundColor(mActivity.getResources().getColor(JDApplication.COLOR_OF_DIALOG))
                .contentColor(JDApplication.COLOR_OF_DIALOG_CONTENT)
                .itemsCallback((dialog, view, which, text) -> {

                    switch (which) {
                        case 0:
                            ShareUtil.shareText(mActivity, video
                                    .getTitle().trim() + " " + video.getUrl());
                            break;
                        case 1:
                            TextUtil.copy(mActivity, video.getUrl());
                            break;
                    }
                }).show());

        holder.card.setOnClickListener(v -> {
            Intent intent = new Intent(mActivity, VideoDetailActivity.class);
            intent.putExtra("url", video.getUrl());
            mActivity.startActivity(intent);
        });

        setAnimation(holder.card, position);

    }

    @Override
    public int getItemCount() {
        return mVideos.size();
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
            loadData();
        } else {
            loadCache();
        }
    }

    private void loadData() {
        Subscription subscription = JDApi.getVideos(page).subscribe(this::getCommentCounts, e -> {
            mLoadFinisCallBack.loadFinish(e);
        });
        mActivity.addSubscription(subscription);

    }

    private void loadCache() {
        Subscription subscription = Observable.create((Observable.OnSubscribe<ArrayList<Video>>) subscriber -> {
            subscriber.onNext(VideoCache.getInstance(mActivity).getCacheByPage(page));
            subscriber.onCompleted();
        }).compose(JDApi.applySchedulers())
                .doOnNext(videos -> {
                    if (page == 1) {
                        mVideos.clear();
                        ToastHelper.Short(ConstantString.LOAD_NO_NETWORK);
                    }
                })
                .subscribe(videos -> {
                    mVideos.addAll(videos);
                    notifyDataSetChanged();
                    mLoadResultCallBack.onSuccess(LoadResultCallBack.SUCCESS_OK, null);
                    mLoadFinisCallBack.loadFinish(null);
                });
        mActivity.addSubscription(subscription);
    }

    private void getCommentCounts(final ArrayList<Video> videos) {

        StringBuilder sb = new StringBuilder();
        for (Video video : videos) {
            sb.append("comment-" + video.getComment_ID() + ",");
        }

        Subscription subscription = JDApi.getCommentNumber(sb.toString())
                .observeOn(Schedulers.io())
                .doOnNext(commentNumbers -> {
                    if (page == 1) {
                        mVideos.clear();
                        VideoCache.getInstance(mActivity).clearAllCache();
                    }
                    VideoCache.getInstance(mActivity).addResultCache(GsonHelper.toString
                            (videos), page);
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(commentNumbers -> {

                    for (int i = 0; i < videos.size(); i++) {
                        videos.get(i).setComment_count(commentNumbers.get(i).comments + "");
                    }

                    mVideos.addAll(videos);
                    notifyDataSetChanged();

                    if (mVideos.size() < 10) {
                        loadNextPage();
                    }
                    mLoadResultCallBack.onSuccess(LoadResultCallBack.SUCCESS_OK, null);
                    mLoadFinisCallBack.loadFinish(null);
                }, e -> {
                    mLoadFinisCallBack.loadFinish(e);
                    mLoadResultCallBack.onError(LoadResultCallBack.ERROR_NET);
                });
        mActivity.addSubscription(subscription);
    }

    static class VideoViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.tv_title)
        TextView tv_title;
        @Bind(R.id.tv_like)
        TextView tv_like;
        @Bind(R.id.tv_unlike)
        TextView tv_unlike;
        @Bind(R.id.tv_comment_count)
        TextView tv_comment_count;

        @Bind(R.id.img_share)
        ImageView img_share;
        @Bind(R.id.ll_comment)
        LinearLayout ll_comment;
        @Bind(R.id.img)
        ImageView img;
        @Bind(R.id.card)
        CardView card;

        public VideoViewHolder(View contentView) {
            super(contentView);
            ButterKnife.bind(this, contentView);
        }
    }
}

