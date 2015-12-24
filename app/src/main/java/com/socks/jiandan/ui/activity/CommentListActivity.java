package com.socks.jiandan.ui.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;

import com.socks.jiandan.R;
import com.socks.jiandan.base.BaseActivity;
import com.socks.jiandan.callback.LoadResultCallBack;
import com.socks.jiandan.ui.adapter.CommentAdapter;
import com.socks.jiandan.utils.ToastHelper;
import com.victor.loading.rotate.RotateLoading;

import butterknife.Bind;
import butterknife.ButterKnife;

public class CommentListActivity extends BaseActivity implements LoadResultCallBack {

    @Bind(R.id.swipe_refresh)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @Bind(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.loading)
    RotateLoading loading;

    private boolean isFromFreshNews;
    private CommentAdapter mAdapter;

    @Override
    protected void initView(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.activity_comment_list);
        ButterKnife.bind(this);

        mToolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(mToolbar);
        mToolbar.setTitle("评论");
        mToolbar.setNavigationIcon(R.drawable.ic_actionbar_back);

        mRecyclerView.setHasFixedSize(false);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        mSwipeRefreshLayout.setOnRefreshListener(() -> {
            if (isFromFreshNews) {
                mAdapter.loadData4FreshNews();
            } else {
                mAdapter.loadData();
            }
        });
    }

    @Override
    protected void loadData() {
        String thread_key = getIntent().getStringExtra(DATA_THREAD_KEY);
        String thread_id = getIntent().getStringExtra(DATA_THREAD_ID);
        isFromFreshNews = getIntent().getBooleanExtra(DATA_IS_FROM_FRESH_NEWS, false);

        if (isFromFreshNews) {
            mAdapter = new CommentAdapter(this, thread_id, true, this);
            if (TextUtils.isEmpty(thread_id) || thread_id.equals("0")) {
                ToastHelper.Short(FORBID_COMMENTS);
                finish();
            }
        } else {
            mAdapter = new CommentAdapter(this, thread_key, false, this);
            if (TextUtils.isEmpty(thread_key) || thread_key.equals("0")) {
                ToastHelper.Short(FORBID_COMMENTS);
                finish();
            }
        }
        mRecyclerView.setAdapter(mAdapter);
        if (isFromFreshNews) {
            mAdapter.loadData4FreshNews();
        } else {
            mAdapter.loadData();
        }
        loading.start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (isFromFreshNews) {
                mAdapter.loadData4FreshNews();
            } else {
                mAdapter.loadData();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_comment_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_edit:
                Intent intent = new Intent(this, PushCommentActivity.class);
                intent.putExtra(DATA_THREAD_ID, mAdapter.getThreadId());
                startActivityForResult(intent, 0);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSuccess(int result, Object object) {
        if (result == LoadResultCallBack.SUCCESS_NONE) {
            ToastHelper.Short(NO_COMMENTS);
        }
        loading.stop();
        mSwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onError(int code) {
        mSwipeRefreshLayout.setRefreshing(false);
        loading.stop();
        ToastHelper.Short(LOAD_FAILED);
    }

}