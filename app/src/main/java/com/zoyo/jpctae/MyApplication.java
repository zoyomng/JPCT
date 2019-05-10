package com.zoyo.jpctae;

import android.app.Application;


public class MyApplication extends Application {
    MyApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    private MyApplication getInstance() {
        return instance;
    }


}
