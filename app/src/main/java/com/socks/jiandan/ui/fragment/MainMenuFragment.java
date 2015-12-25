package com.socks.jiandan.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.socks.jiandan.R;
import com.socks.jiandan.adapter.MenuAdapter;
import com.socks.jiandan.base.BaseFragment;
import com.socks.jiandan.model.MenuItem;
import com.socks.jiandan.ui.activity.SettingActivity;
import com.socks.jiandan.viewInterface.IMainView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainMenuFragment extends BaseFragment {

    @Bind(R.id.recycler_view)
    RecyclerView mRecyclerView;

    private LinearLayoutManager mLayoutManager;
    private IMainView mMainView;
    private MenuAdapter mAdapter;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof IMainView) {
            mMainView = (IMainView) context;
        } else {
            throw new IllegalArgumentException("The activity must be a IMainView !");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_drawer, container, false);
        ButterKnife.bind(this, view);

        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mAdapter = new MenuAdapter(mContext);
        addAllMenuItems(mAdapter);
        mRecyclerView.setAdapter(mAdapter);
    }

    @OnClick(R.id.rl_setting)
    void showSetting() {
        startActivity(new Intent(mContext, SettingActivity.class));
        mMainView.closeDrawer();
    }

    @Override
    public void onResume() {
        super.onResume();
        boolean showSister = PreferenceManager.getDefaultSharedPreferences(mContext).getBoolean(SettingFragment.ENABLE_SISTER, false);

        if (showSister && mAdapter.menuItems.size() == 4) {
            addAllMenuItems(mAdapter);
            mAdapter.notifyDataSetChanged();
        } else if (!showSister && mAdapter.menuItems.size()
                == 5) {
            addMenuItemsNoSister(mAdapter);
            mAdapter.notifyDataSetChanged();
        }

    }

    private void addAllMenuItems(MenuAdapter mAdapter) {
        mAdapter.menuItems.clear();
        mAdapter.menuItems.add(new MenuItem(getString(R.string.title_activity_fresh_news_detail), R.drawable.ic_explore_white_24dp, MenuItem.FragmentType.FreshNews,
                FreshNewsFragment.class));
        mAdapter.menuItems.add(new MenuItem(getString(R.string.boring_picture), R.drawable.ic_mood_white_24dp, MenuItem.FragmentType.BoringPicture,
                PictureFragment.class));
        mAdapter.menuItems.add(new MenuItem(getString(R.string.sister_picture), R.drawable.ic_local_florist_white_24dp, MenuItem.FragmentType.Sister,
                SisterFragment.class));
        mAdapter.menuItems.add(new MenuItem(getString(R.string.title_activity_joke), R.drawable.ic_chat_white_24dp, MenuItem.FragmentType.Joke, JokeFragment
                .class));
        mAdapter.menuItems.add(new MenuItem(getString(R.string.small_video), R.drawable.ic_movie_white_24dp, MenuItem.FragmentType.Video,
                VideoFragment.class));
    }

    private void addMenuItemsNoSister(MenuAdapter mAdapter) {
        mAdapter.menuItems.clear();
        mAdapter.menuItems.add(new MenuItem(getString(R.string.title_activity_fresh_news_detail), R.drawable.ic_explore_white_24dp, MenuItem.FragmentType.FreshNews,
                FreshNewsFragment.class));
        mAdapter.menuItems.add(new MenuItem(getString(R.string.boring_picture), R.drawable.ic_mood_white_24dp, MenuItem.FragmentType.BoringPicture,
                PictureFragment.class));
        mAdapter.menuItems.add(new MenuItem(getString(R.string.title_activity_joke), R.drawable.ic_chat_white_24dp, MenuItem.FragmentType.Joke, JokeFragment
                .class));
        mAdapter.menuItems.add(new MenuItem(getString(R.string.small_video), R.drawable.ic_movie_white_24dp, MenuItem.FragmentType.Video,
                VideoFragment.class));
    }

}