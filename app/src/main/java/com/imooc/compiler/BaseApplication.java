package com.imooc.compiler;

import android.app.Application;

import com.imooc.annotations.WXPayEntry;
import com.imooc.joke.BaseWXPayActivity;

/**
 *
 */
@WXPayEntry(packageName = "com.imooc.joke",entryClass = BaseWXPayActivity.class)
public class BaseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
    }
}
