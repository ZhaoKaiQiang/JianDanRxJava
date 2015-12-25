package com.socks.jiandan.receiver;

import com.socks.jiandan.model.NetWorkEvent;

import rx.Observable;
import rx.subjects.PublishSubject;
import rx.subjects.SerializedSubject;
import rx.subjects.Subject;

/**
 * Created by zhaokaiqiang on 15/12/25.
 */
public class RxNetWorkEvent {

    private static final Subject<NetWorkEvent, NetWorkEvent> bus = new SerializedSubject<>(PublishSubject.create());

    public static void send(NetWorkEvent o) {
        bus.onNext(o);
    }

    public static Observable<NetWorkEvent> toObserverable() {
        return bus;
    }

}
