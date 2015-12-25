package com.socks.jiandan.ui.fragment;

import android.media.MediaScannerConnection;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.socks.jiandan.R;
import com.socks.jiandan.adapter.PictureAdapter;
import com.socks.jiandan.base.BaseFragment;
import com.socks.jiandan.callback.LoadFinishCallBack;
import com.socks.jiandan.callback.LoadResultCallBack;
import com.socks.jiandan.model.NetWorkEvent;
import com.socks.jiandan.model.Picture;
import com.socks.jiandan.receiver.RxNetWorkEvent;
import com.socks.jiandan.utils.JDMediaScannerConnectionClient;
import com.socks.jiandan.utils.NetWorkUtil;
import com.socks.jiandan.utils.ToastHelper;
import com.socks.jiandan.view.AutoLoadRecyclerView;
import com.victor.loading.rotate.RotateLoading;

import java.io.File;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

public class PictureFragment extends BaseFragment implements LoadResultCallBack, LoadFinishCallBack {

    @Bind(R.id.recycler_view)
    AutoLoadRecyclerView mRecyclerView;
    @Bind(R.id.swipeRefreshLayout)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @Bind(R.id.loading)
    RotateLoading loading;

    protected int mType = Picture.BoringPicture;
    private boolean isFirstChange = true;
    private CompositeSubscription mRxBusComposite;
    private long lastShowTime;
    private MediaScannerConnection connection;
    private PictureAdapter mAdapter;

    public PictureFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mRxBusComposite = new CompositeSubscription();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_auto_load, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mRecyclerView.setHasFixedSize(false);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setLoadMoreListener(() -> mAdapter.loadNextPage());
        mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        mSwipeRefreshLayout.setOnRefreshListener(() -> mAdapter.loadFirst());

        mRecyclerView.setOnPauseListenerParams(false, true);
        mAdapter = new PictureAdapter(getActivity(), this, mRecyclerView, mType);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setSaveFileCallBack(this);
        mAdapter.loadFirst();
        loading.start();
    }

    @Override
    public void onStart() {
        super.onStart();

        Subscription subscribe = RxNetWorkEvent.toObserverable().subscribe(netWorkEvent -> {
            if (netWorkEvent.getType() == NetWorkEvent.AVAILABLE) {
                if (NetWorkUtil.isWifiConnected(getActivity())) {
                    mAdapter.setIsWifi(true);
                    if (!isFirstChange && (System.currentTimeMillis() - lastShowTime) > 3000) {
                        ToastHelper.Short(R.string.load_mode_wifi);
                        lastShowTime = System.currentTimeMillis();
                    }
                } else {
                    mAdapter.setIsWifi(false);
                    if (!isFirstChange && (System.currentTimeMillis() - lastShowTime) > 3000) {
                        ToastHelper.Short(R.string.load_mode_3g);
                        lastShowTime = System.currentTimeMillis();
                    }
                }
                isFirstChange = false;
            }
        });

        mRxBusComposite.add(subscribe);
    }

    @Override
    public void onStop() {
        super.onStop();
        mRxBusComposite.unsubscribe();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_refresh, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_refresh) {
            mSwipeRefreshLayout.setRefreshing(true);
            mAdapter.loadFirst();
            return true;
        }
        return false;
    }

    @Override
    public void onSuccess(int result, Object object) {
        loading.stop();
        if (mSwipeRefreshLayout.isRefreshing()) {
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    @Override
    public void onError(int code) {
        loading.stop();
        if (mSwipeRefreshLayout.isRefreshing()) {
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    public void setType(int mType) {
        this.mType = mType;
    }

    @Override
    public void loadFinish(Object obj) {
        Bundle bundle = (Bundle) obj;
        boolean isSmallPic = bundle.getBoolean(DATA_IS_SMALL_PIC);
        String filePath = bundle.getString(DATA_FILE_PATH);
        assert filePath != null;
        File newFile = new File(filePath);
        JDMediaScannerConnectionClient connectionClient = new JDMediaScannerConnectionClient(isSmallPic,
                newFile);
        connection = new MediaScannerConnection(getActivity(), connectionClient);
        connectionClient.setMediaScannerConnection(connection);
        connection.connect();
    }

}