package com.socks.jiandan.ui.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.socks.jiandan.model.FreshNews;
import com.socks.jiandan.ui.fragment.FreshNewsDetailFragment;

import java.util.ArrayList;

/**
 * Created by zhaokaiqiang on 15/12/24.
 */
public class FreshNewsDetailAdapter extends FragmentPagerAdapter {

    private ArrayList<FreshNews> freshNewses;

    public FreshNewsDetailAdapter(FragmentManager fm, ArrayList<FreshNews> freshNewses) {
        super(fm);
        this.freshNewses = freshNewses;
    }

    @Override
    public Fragment getItem(int position) {
        return FreshNewsDetailFragment.getInstance(freshNewses.get(position));
    }

    @Override
    public int getCount() {
        return freshNewses.size();
    }
}