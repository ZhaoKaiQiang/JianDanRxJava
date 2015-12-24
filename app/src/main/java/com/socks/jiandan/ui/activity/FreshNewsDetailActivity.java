package com.socks.jiandan.ui.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.socks.jiandan.R;
import com.socks.jiandan.base.BaseActivity;
import com.socks.jiandan.model.FreshNews;
import com.socks.jiandan.ui.adapter.FreshNewsDetailAdapter;

import java.io.Serializable;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class FreshNewsDetailActivity extends BaseActivity {

    @Bind(R.id.vp)
    ViewPager viewPager;
    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    @Override
    protected void initView(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.activity_fresh_news_detail);
        ButterKnife.bind(this);
        mToolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationIcon(R.drawable.ic_actionbar_back);
    }

    @Override
    protected void loadData() {
        Intent intent = getIntent();
        int position = intent.getIntExtra(DATA_POSITION, 0);
        Serializable serializable = intent.getSerializableExtra(DATA_FRESH_NEWS);
        ArrayList<FreshNews> FreshNews = (ArrayList<FreshNews>) serializable;

        viewPager.setAdapter(new FreshNewsDetailAdapter(getSupportFragmentManager(), FreshNews));
        viewPager.setCurrentItem(position);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
