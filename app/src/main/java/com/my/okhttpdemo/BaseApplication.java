package com.my.okhttpdemo;

import android.app.Application;
import android.content.Context;

/**
 * Created by YJH on 2017/3/15 20:13.
 */

public class BaseApplication extends Application {

    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }

    public static Context getAppContext() {
        return context;
    }
}
