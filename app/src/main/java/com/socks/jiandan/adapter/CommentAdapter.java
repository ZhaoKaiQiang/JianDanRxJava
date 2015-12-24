package com.socks.jiandan.adapter;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.socks.jiandan.JDApplication;
import com.socks.jiandan.R;
import com.socks.jiandan.base.ConstantString;
import com.socks.jiandan.callback.LoadResultCallBack;
import com.socks.jiandan.model.Comment4FreshNews;
import com.socks.jiandan.model.Commentator;
import com.socks.jiandan.net.JDApi;
import com.socks.jiandan.utils.IntentHelper;
import com.socks.jiandan.utils.String2TimeUtil;
import com.socks.jiandan.utils.ToastHelper;
import com.socks.jiandan.view.floorview.FloorView;
import com.socks.jiandan.view.floorview.SubComments;
import com.socks.jiandan.view.floorview.SubFloorFactory;
import com.socks.jiandan.view.imageloader.ImageLoadProxy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {

    private ArrayList<Commentator> mCommentators;
    private ArrayList<Comment4FreshNews> commentators4FreshNews;

    private Activity mActivity;
    private String thread_key;
    private String thread_id;
    private LoadResultCallBack mLoadResultCallBack;
    private boolean isFromFreshNews;

    public CommentAdapter(Activity activity, String thread_key, boolean isFromFreshNews, LoadResultCallBack loadResultCallBack) {
        mActivity = activity;
        this.thread_key = thread_key;
        this.isFromFreshNews = isFromFreshNews;
        mLoadResultCallBack = loadResultCallBack;
        if (isFromFreshNews) {
            commentators4FreshNews = new ArrayList<>();
        } else {
            mCommentators = new ArrayList<>();
        }
    }

    @Override
    public CommentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        switch (viewType) {
            case Commentator.TYPE_HOT:
            case Commentator.TYPE_NEW:
                return new CommentViewHolder(mActivity.getLayoutInflater().inflate(R.layout
                        .item_comment_flag, parent, false));
            case Commentator.TYPE_NORMAL:
                return new CommentViewHolder(mActivity.getLayoutInflater().inflate(R.layout.item_comment, parent,
                        false));
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(CommentViewHolder holder, int position) {

        Commentator commentator;
        if (isFromFreshNews) {
            commentator = commentators4FreshNews.get(position);
        } else {
            commentator = mCommentators.get(position);
        }

        switch (commentator.getType()) {
            case Commentator.TYPE_HOT:
                assert holder.tv_flag != null;
                holder.tv_flag.setText("热门评论");
                break;
            case Commentator.TYPE_NEW:
                assert holder.tv_flag != null;
                holder.tv_flag.setText("最新评论");
                break;
            case Commentator.TYPE_NORMAL:
                final Commentator comment = commentator;
                assert holder.tv_name != null;
                holder.tv_name.setText(commentator.getName());
                assert holder.tv_content != null;
                holder.tv_content.setOnClickListener(v -> new MaterialDialog.Builder(mActivity)
                        .title(comment.getName())
                        .items(R.array.comment_dialog)
                        .backgroundColor(mActivity.getResources().getColor(JDApplication.COLOR_OF_DIALOG))
                        .contentColor(JDApplication.COLOR_OF_DIALOG_CONTENT)
                        .itemsCallback((dialog, view, which, text) -> {
                            switch (which) {
                                case 0:
                                    IntentHelper.toPushComment4Result(mActivity, comment.getPost_id(), thread_id, comment
                                            .getName());
                                    break;
                                case 1:
                                    ClipboardManager clip = (ClipboardManager) mActivity.getSystemService(Context.CLIPBOARD_SERVICE);
                                    clip.setPrimaryClip(ClipData.newPlainText
                                            (null, comment.getMessage()));
                                    ToastHelper.Short(ConstantString.COPY_SUCCESS);
                                    break;
                            }
                        }).show());

                if (isFromFreshNews) {
                    Comment4FreshNews commentators4FreshNews = (Comment4FreshNews) commentator;
                    holder.tv_content.setText(commentators4FreshNews.getCommentContent());
                    ImageLoadProxy.displayHeadIcon(commentators4FreshNews.getAvatar_url(), holder.img_header);
                } else {
                    String timeString = commentator.getCreated_at().replace("T", " ");
                    timeString = timeString.substring(0, timeString.indexOf("+"));
                    assert holder.tv_time != null;
                    holder.tv_time.setText(String2TimeUtil.dateString2GoodExperienceFormat(timeString));
                    holder.tv_content.setText(commentator.getMessage());
                    ImageLoadProxy.displayHeadIcon(commentator.getAvatar_url(), holder.img_header);
                }
                assert holder.floors_parent != null;
                if (commentator.getFloorNum() > 1) {
                    SubComments subComments;
                    if (isFromFreshNews) {
                        subComments = new SubComments(addFloors4FreshNews((Comment4FreshNews) commentator));
                    } else {
                        subComments = new SubComments(addFloors(commentator));
                    }
                    holder.floors_parent.setComments(subComments);
                    holder.floors_parent.setFactory(new SubFloorFactory());
                    holder.floors_parent.setBoundDrawer(mActivity.getResources().getDrawable(
                            R.drawable.bg_comment));
                    holder.floors_parent.init();
                } else {
                    holder.floors_parent.setVisibility(View.GONE);
                }
                break;
        }

    }

    private List<Comment4FreshNews> addFloors4FreshNews(Comment4FreshNews commentator) {
        return commentator.getParentComments();
    }

    private List<Commentator> addFloors(Commentator commentator) {
        if (commentator.getFloorNum() == 1) {
            return null;
        }
        List<String> parentIds = Arrays.asList(commentator.getParents());
        ArrayList<Commentator> commentators = new ArrayList<>();
        for (Commentator comm : this.mCommentators) {
            if (parentIds.contains(comm.getPost_id())) {
                commentators.add(comm);
            }
        }
        Collections.reverse(commentators);
        return commentators;
    }

    @Override
    public int getItemCount() {
        if (isFromFreshNews) {
            return commentators4FreshNews.size();
        } else {
            return mCommentators.size();
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (isFromFreshNews) {
            return commentators4FreshNews.get(position).getType();
        } else {
            return mCommentators.get(position).getType();
        }
    }

    public void loadData() {
        JDApi.getCommentator(thread_key, obj -> thread_id = (String) obj).subscribe(commentators1 -> {
            if (commentators1.size() == 0) {
                mLoadResultCallBack.onSuccess(LoadResultCallBack.SUCCESS_NONE, null);
            } else {
                mCommentators.clear();
                Commentator.generateCommentator(commentators1,mCommentators);
                notifyDataSetChanged();
                mLoadResultCallBack.onSuccess(LoadResultCallBack.SUCCESS_OK, null);
            }
        }, e -> {
            mLoadResultCallBack.onError(LoadResultCallBack.ERROR_NET);
        });
    }

    public void loadData4FreshNews() {
        JDApi.getCommentator4FreshNews(thread_key, obj -> thread_id = (String) obj).subscribe(comment4FreshNewses -> {
            if (comment4FreshNewses.size() == 0) {
                mLoadResultCallBack.onSuccess(LoadResultCallBack.SUCCESS_NONE, null);
            } else {
                Comment4FreshNews.generateComment(comment4FreshNewses, commentators4FreshNews);
                notifyDataSetChanged();
                mLoadResultCallBack.onSuccess(LoadResultCallBack.SUCCESS_OK, null);
            }
        }, e -> {
            mLoadResultCallBack.onError(LoadResultCallBack.ERROR_NET);
        });
    }

    public String getThreadId() {
        return thread_id;
    }

    class CommentViewHolder extends RecyclerView.ViewHolder {

        @Nullable
        @Bind(R.id.tv_name)
        TextView tv_name;
        @Nullable
        @Bind(R.id.tv_content)
        TextView tv_content;
        @Nullable
        @Bind(R.id.tv_flag)
        TextView tv_flag;
        @Nullable
        @Bind(R.id.tv_time)
        TextView tv_time;
        @Nullable
        @Bind(R.id.img_header)
        ImageView img_header;
        @Nullable
        @Bind(R.id.floors_parent)
        FloorView floors_parent;

        public CommentViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            setIsRecyclable(false);
        }
    }
}