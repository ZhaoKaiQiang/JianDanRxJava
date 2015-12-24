package com.socks.jiandan;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void textRxJava() {

        Observable.just("1", "2").toList().subscribe(strings -> {
            KLog.d(strings.toString());
        });
    }
}