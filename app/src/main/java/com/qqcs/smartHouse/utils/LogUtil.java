package com.qqcs.smartHouse.utils;

import android.util.Log;

/**
 * Created by hello on 2016/12/8.
 */
public class LogUtil {

    private static boolean flag = true;
    private static String TAG_ROOT = "wang";

    public static void d(String msg)
    {
        if (flag)
            Log.v(TAG_ROOT, msg);
    }

    public static void d(String tag, String msg)
    {
        if (flag)
            Log.v(tag, msg);
    }

}
