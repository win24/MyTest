package com.huabao.mytest.provider;


import android.database.ContentObserver;
import android.os.Handler;

public class TestObserver extends ContentObserver {

    public TestObserver(Handler handler) {
        super(handler);
    }

    @Override
    public void onChange(boolean selfChange) {
        super.onChange(selfChange);
    }

}