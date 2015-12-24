package com.socks.jiandan.adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.socks.jiandan.JDApplication;
import com.socks.jiandan.R;
import com.socks.jiandan.base.BaseActivity;
import com.socks.jiandan.base.ConstantString;
import com.socks.jiandan.cache.PictureCache;
import com.socks.jiandan.callback.LoadFinishCallBack;
import com.socks.jiandan.callback.LoadResultCallBack;
import com.socks.jiandan.model.Picture;
import com.socks.jiandan.net.JDApi;
import com.socks.jiandan.ui.activity.CommentListActivity;
import com.socks.jiandan.utils.FileUtil;
import com.socks.jiandan.utils.GsonHelper;
import com.socks.jiandan.utils.IntentHelper;
import com.socks.jiandan.utils.NetWorkUtil;
import com.socks.jiandan.utils.ShareUtil;
import com.socks.jiandan.utils.String2TimeUtil;
import com.socks.jiandan.utils.TextUtil;
import com.socks.jiandan.utils.ToastHelper;
import com.socks.jiandan.view.ShowMaxImageView;
import com.socks.jiandan.view.imageloader.ImageLoadProxy;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.socks.jiandan.R.color.secondary_text_default_material_light;

public class PictureAdapter extends RecyclerView.Adapter<PictureAdapter.PictureViewHolder> {

    private int page;
    private int lastPosition = -1;
    private int mType;
    private boolean isWifiConnected;

    private ArrayList<Picture> mPictures;
    private LoadFinishCallBack mLoadFinisCallBack;
    private LoadResultCallBack mLoadResultCallBack;
    private Activity mActivity;
    private LoadFinishCallBack mSaveFileCallBack;

    public PictureAdapter(Activity activity, LoadResultCallBack loadResultCallBack, LoadFinishCallBack loadFinisCallBack, int type) {
        mActivity = activity;
        mType = type;
        mLoadFinisCallBack = loadFinisCallBack;
        mLoadResultCallBack = loadResultCallBack;
        mPictures = new ArrayList<>();
        isWifiConnected = NetWorkUtil.isWifiConnected(mActivity);
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
    public void onViewDetachedFromWindow(PictureViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        holder.card.clearAnimation();
    }

    @Override
    public PictureViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_pic, parent, false);
        return new PictureViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final PictureViewHolder holder, final int position) {

        final Picture picture = mPictures.get(position);

        String picUrl = picture.getPics()[0];

        if (picUrl.endsWith(".gif")) {
            holder.img_gif.setVisibility(View.VISIBLE);
            //非WIFI网络情况下，GIF图只加载缩略图，详情页才加载真实图片
            if (!isWifiConnected) {
                picUrl = picUrl.replace("mw600", "small").replace("mw1200", "small").replace
                        ("large", "small");
            }
        } else {
            holder.img_gif.setVisibility(View.GONE);
        }

        holder.progress.setProgress(0);
        holder.progress.setVisibility(View.VISIBLE);

        ImageLoadProxy.displayImageList(picUrl, holder.img, R.drawable.ic_loading_large, new
                        SimpleImageLoadingListener() {
                            @Override
                            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                                super.onLoadingComplete(imageUri, view, loadedImage);
                                holder.progress.setVisibility(View.GONE);
                            }
                        },
                (imageUri, view, current, total) -> holder.progress.setProgress((int) (current * 100f / total)));

        if (TextUtil.isNull(picture.getText_content().trim())) {
            holder.tv_content.setVisibility(View.GONE);
        } else {
            holder.tv_content.setVisibility(View.VISIBLE);
            holder.tv_content.setText(picture.getText_content().trim());
        }

        holder.img.setOnClickListener(v -> IntentHelper.toImageDetailActivity(mActivity, picture));

        holder.tv_author.setText(picture.getComment_author());
        holder.tv_time.setText(String2TimeUtil.dateString2GoodExperienceFormat(picture.getComment_date()));
        holder.tv_like.setText(picture.getVote_positive());
        holder.tv_comment_count.setText(picture.getComment_counts());

        //用于恢复默认的文字
        holder.tv_like.setTypeface(Typeface.DEFAULT);
        holder.tv_like.setTextColor(mActivity.getResources().getColor(
                secondary_text_default_material_light));
        holder.tv_support_des.setTextColor(mActivity.getResources().getColor(
                secondary_text_default_material_light));

        holder.tv_unlike.setText(picture.getVote_negative());
        holder.tv_unlike.setTypeface(Typeface.DEFAULT);
        holder.tv_unlike.setTextColor(mActivity.getResources().getColor(
                secondary_text_default_material_light));
        holder.tv_un_support_des.setTextColor(mActivity.getResources().getColor(
                secondary_text_default_material_light));

        holder.img_share.setOnClickListener(v -> new MaterialDialog.Builder(mActivity)
                .items(R.array.picture_dialog)
                .backgroundColor(mActivity.getResources().getColor(JDApplication.COLOR_OF_DIALOG))
                .contentColor(JDApplication.COLOR_OF_DIALOG_CONTENT)
                .itemsCallback((dialog, view, which, text) -> {

                    switch (which) {
                        case 0:
                            ShareUtil.sharePicture(mActivity, picture
                                    .getPics()[0]);
                            break;
                        case 1:
                            FileUtil.savePicture(mActivity, picture
                                    .getPics()[0], mSaveFileCallBack);
                            break;
                    }
                })
                .show());

        holder.ll_comment.setOnClickListener(v -> {
            Intent intent = new Intent(mActivity, CommentListActivity.class);
            intent.putExtra(BaseActivity.DATA_THREAD_KEY, "comment-" + picture.getComment_ID());
            mActivity.startActivity(intent);
        });

        setAnimation(holder.card, position);

    }

    @Override
    public int getItemCount() {
        return mPictures.size();
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
        JDApi.getPictures(mType, page)
                .doOnNext(pictures -> {
                    if (page == 1) {
                        PictureAdapter.this.mPictures.clear();
                        PictureCache.getInstance(mActivity).clearAllCache();
                    }
                })
                .subscribe(this::getCommentCounts, e -> {
                    mLoadResultCallBack.onError(LoadResultCallBack.ERROR_NET);
                    mLoadFinisCallBack.loadFinish(null);
                });
    }

    private void loadCache() {
        mLoadResultCallBack.onSuccess(LoadResultCallBack.SUCCESS_OK, null);
        mLoadFinisCallBack.loadFinish(null);
        PictureCache pictureCacheUtil = PictureCache.getInstance(mActivity);
        if (page == 1) {
            mPictures.clear();
            ToastHelper.Short(ConstantString.LOAD_NO_NETWORK);
        }
        mPictures.addAll(pictureCacheUtil.getCacheByPage(page));
        notifyDataSetChanged();
    }

    private void getCommentCounts(final ArrayList<Picture> pictures) {

        StringBuilder sb = new StringBuilder();
        for (Picture joke : pictures) {
            sb.append("comment-").append(joke.getComment_ID()).append(",");
        }

        JDApi.getCommentNumber(sb.toString())
                .doOnCompleted(() -> PictureCache.getInstance(mActivity).addResultCache(GsonHelper.toString(mPictures), page))
                .subscribe(commentNumbers -> {
                    mLoadResultCallBack.onSuccess(LoadResultCallBack.SUCCESS_OK, null);
                    mLoadFinisCallBack.loadFinish(null);

                    for (int i = 0; i < pictures.size(); i++) {
                        pictures.get(i).setComment_counts(commentNumbers.get(i).getComments() + "");
                    }

                    PictureAdapter.this.mPictures.addAll(pictures);
                    notifyDataSetChanged();
                }, e -> {
                    ToastHelper.Short(ConstantString.LOAD_FAILED);
                    mLoadFinisCallBack.loadFinish(null);
                    mLoadResultCallBack.onError(LoadResultCallBack.ERROR_NET);
                });
    }

    public void setIsWifi(boolean isWifiConnected) {
        this.isWifiConnected = isWifiConnected;
    }

    public void setSaveFileCallBack(LoadFinishCallBack mSaveFileCallBack) {
        this.mSaveFileCallBack = mSaveFileCallBack;
    }

    class PictureViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.tv_author)
        TextView tv_author;
        @Bind(R.id.tv_time)
        TextView tv_time;
        @Bind(R.id.tv_content)
        TextView tv_content;
        @Bind(R.id.tv_like)
        TextView tv_like;
        @Bind(R.id.tv_unlike)
        TextView tv_unlike;
        @Bind(R.id.tv_comment_count)
        TextView tv_comment_count;
        @Bind(R.id.tv_unsupport_des)
        TextView tv_un_support_des;
        @Bind(R.id.tv_support_des)
        TextView tv_support_des;

        @Bind(R.id.img_share)
        ImageView img_share;
        @Bind(R.id.img_gif)
        ImageView img_gif;
        @Bind(R.id.img)
        ShowMaxImageView img;

        @Bind(R.id.ll_comment)
        LinearLayout ll_comment;
        @Bind(R.id.progress)
        ProgressBar progress;
        @Bind(R.id.card)
        CardView card;

        public PictureViewHolder(View contentView) {
            super(contentView);
            ButterKnife.bind(this, contentView);
        }
    }
}