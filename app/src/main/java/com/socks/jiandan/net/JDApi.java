package com.socks.jiandan.net;

import com.socks.jiandan.model.FreshNews;
import com.socks.jiandan.net.parser.FreshNewsDetailParser;
import com.socks.jiandan.net.parser.FreshNewsParser;
import com.socks.okhttp.plus.OkHttpProxy;

import java.io.IOException;
import java.util.ArrayList;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by zhaokaiqiang on 15/12/23.
 */
public class JDApi {

    public static Observable<ArrayList<FreshNews>> getFreshNews(final int page) {

        return Observable.create(new Observable.OnSubscribe<ArrayList<FreshNews>>() {

            @Override
            public void call(Subscriber<? super ArrayList<FreshNews>> subscriber) {
                String url = FreshNews.getUrlFreshNews(page);
                try {
                    subscriber.onNext(new FreshNewsParser().parse(OkHttpProxy.get()
                            .url(url)
                            .execute()));
                    subscriber.onCompleted();
                } catch (IOException e) {
                    subscriber.onError(e);
                }
            }
        }).compose(JDApi.<ArrayList<FreshNews>>applySchedulers());
    }


    public static Observable<String> getFreshDetail(String id) {
        return Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                String url = FreshNews.getUrlFreshNewsDetail(id);
                try {
                    subscriber.onNext(new FreshNewsDetailParser().parse(OkHttpProxy.get().url(url).execute()));
                    subscriber.onCompleted();
                } catch (IOException e) {
                    subscriber.onError(e);
                }
            }
        }).compose(applySchedulers());
    }

    private static <T> Observable.Transformer<T, T> applySchedulers() {
        return observable -> observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

}
