package com.my.okhttpdemo;

import android.util.Log;

/**
 * Created by YJH on 2017/3/14 13:06.
 */

public class L {
    private static final String TAG = "OKHttpDemo";
    private static boolean debug = true;

    public static void e(String msg) {
        if (debug) {
            Log.e(TAG, msg);
        }
    }

}
