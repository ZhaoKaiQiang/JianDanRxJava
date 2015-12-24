package com.socks.jiandan.adapter;

import android.app.Activity;
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
import com.socks.jiandan.base.ConstantString;
import com.socks.jiandan.cache.JokeCache;
import com.socks.jiandan.callback.LoadFinishCallBack;
import com.socks.jiandan.callback.LoadResultCallBack;
import com.socks.jiandan.model.Joke;
import com.socks.jiandan.net.JDApi;
import com.socks.jiandan.ui.activity.CommentListActivity;
import com.socks.jiandan.utils.GsonHelper;
import com.socks.jiandan.utils.NetWorkUtil;
import com.socks.jiandan.utils.ShareUtil;
import com.socks.jiandan.utils.String2TimeUtil;
import com.socks.jiandan.utils.TextUtil;
import com.socks.jiandan.utils.ToastHelper;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class JokeAdapter extends RecyclerView.Adapter<JokeAdapter.JokeViewHolder> {

    private int page;
    private int lastPosition = -1;
    private ArrayList<Joke> mJokes;
    private Activity mActivity;
    private LoadResultCallBack mLoadResultCallBack;
    private LoadFinishCallBack mLoadFinisCallBack;

    public JokeAdapter(Activity activity, LoadFinishCallBack loadFinisCallBack, LoadResultCallBack loadResultCallBack) {
        mActivity = activity;
        mLoadFinisCallBack = loadFinisCallBack;
        mLoadResultCallBack = loadResultCallBack;
        mJokes = new ArrayList<>();
    }

    protected void setAnimation(View viewToAnimate, int position) {
        if (position > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(viewToAnimate.getContext(), R
                    .anim.item_bottom_in);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }

    @Override
    public void onViewDetachedFromWindow(JokeViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        holder.card.clearAnimation();
    }

    @Override
    public JokeViewHolder onCreateViewHolder(ViewGroup parent,
                                             int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_joke, parent, false);
        return new JokeViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final JokeViewHolder holder, final int position) {

        final Joke joke = mJokes.get(position);
        holder.tv_content.setText(joke.getComment_content());
        holder.tv_author.setText(joke.getComment_author());
        holder.tv_time.setText(String2TimeUtil.dateString2GoodExperienceFormat(joke.getComment_date()));
        holder.tv_like.setText(joke.getVote_positive());
        holder.tv_comment_count.setText(joke.getComment_counts());
        holder.tv_unlike.setText(joke.getVote_negative());

        holder.img_share.setOnClickListener(v -> new MaterialDialog.Builder(mActivity)
                .items(R.array.joke_dialog)
                .backgroundColor(mActivity.getResources().getColor(JDApplication.COLOR_OF_DIALOG))
                .contentColor(JDApplication.COLOR_OF_DIALOG_CONTENT)
                .itemsCallback((dialog, view, which, text) -> {
                    switch (which) {
                        case 0:
                            ShareUtil.shareText(mActivity, joke.getComment_content().trim());
                            break;
                        case 1:
                            TextUtil.copy(mActivity, joke.getComment_content());
                            break;
                    }
                }).show());

        holder.ll_comment.setOnClickListener(v -> {
            Intent intent = new Intent(mActivity, CommentListActivity.class);
            intent.putExtra("thread_key", "comment-" + joke.getComment_ID());
            mActivity.startActivity(intent);
        });

        setAnimation(holder.card, position);

    }

    @Override
    public int getItemCount() {
        return mJokes.size();
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
        JDApi.getJokes(page).subscribe(this::getCommentCounts, e -> {
            mLoadFinisCallBack.loadFinish(null);
        });
    }

    private void loadCache() {
        mLoadFinisCallBack.loadFinish(null);
        mLoadResultCallBack.onSuccess(LoadResultCallBack.SUCCESS_OK, null);
        JokeCache jokeCacheUtil = JokeCache.getInstance(mActivity);
        if (page == 1) {
            mJokes.clear();
            ToastHelper.Short(ConstantString.LOAD_NO_NETWORK);
        }
        mJokes.addAll(jokeCacheUtil.getCacheByPage(page));
        notifyDataSetChanged();
    }

    private void getCommentCounts(final ArrayList<Joke> jokes) {

        StringBuilder sb = new StringBuilder();
        for (Joke joke : jokes) {
            sb.append("comment-" + joke.getComment_ID() + ",");
        }

        String url = sb.toString();
        if (url.endsWith(",")) {
            url = url.substring(0, url.length() - 1);
        }

        JDApi.getCommentNumber(url)
                .subscribe(commentNumbers -> {
                    for (int i = 0; i < jokes.size(); i++) {
                        jokes.get(i).setComment_counts(commentNumbers.get(i).getComments() + "");
                    }
                    if (page == 1) {
                        mJokes.clear();
                        JokeCache.getInstance(mActivity).clearAllCache();
                    }
                    mJokes.addAll(jokes);
                    notifyDataSetChanged();
                    JokeCache.getInstance(mActivity).addResultCache(GsonHelper.toString(jokes), page);
                    mLoadFinisCallBack.loadFinish(null);
                    mLoadResultCallBack.onSuccess(LoadResultCallBack.SUCCESS_OK, null);
                }, e -> {
                    mLoadResultCallBack.onError(LoadResultCallBack.ERROR_NET);
                    mLoadFinisCallBack.loadFinish(null);
                });
    }

    class JokeViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.tv_author)
        TextView tv_author;
        @Bind(R.id.tv_content)
        TextView tv_content;
        @Bind(R.id.tv_like)
        TextView tv_like;
        @Bind(R.id.tv_unlike)
        TextView tv_unlike;
        @Bind(R.id.tv_comment_count)
        TextView tv_comment_count;
        @Bind(R.id.tv_time)
        TextView tv_time;

        @Bind(R.id.img_share)
        ImageView img_share;
        @Bind(R.id.card)
        CardView card;
        @Bind(R.id.ll_comment)
        LinearLayout ll_comment;

        public JokeViewHolder(View contentView) {
            super(contentView);
            ButterKnife.bind(this, contentView);
        }
    }

}