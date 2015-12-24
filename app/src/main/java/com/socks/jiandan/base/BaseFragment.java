package com.socks.jiandan.base;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;

import com.socks.jiandan.JDApplication;
import com.socks.jiandan.view.imageloader.ImageLoadProxy;


public class BaseFragment extends Fragment implements ConstantString {

    protected Activity mContext;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = (Activity) context;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        JDApplication.getRefWatcher(getActivity()).watch(this);
        ImageLoadProxy.getImageLoader().clearMemoryCache();
    }

}
