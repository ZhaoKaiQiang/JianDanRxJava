package com.socks.jiandan;

import android.app.Application;
import android.test.ApplicationTestCase;

import com.socks.library.KLog;

import rx.Observable;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {
    public ApplicationTest() {
        super(Application.class);
    }

    public void textRxJava() {
        Observable.just("1", "2").toList().subscribe(strings -> {
            KLog.d(strings.toString());
            assertEquals("[1,2]", strings.toString());
        });
    }

}