package com.vaiyee.shangji.xiechanghong;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;

import org.litepal.LitePalApplication;

/**
 * Created by Administrator on 2018/6/8.
 */

public class MyApp extends Application {
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        LitePalApplication.initialize(context);  //初始化数据库
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    public static Context getQuanjuContext()
    {
        return context;  //获取全局context
    }
}
