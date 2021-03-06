package com.qqcs.smartHouse.utils;


import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;


public class ScreenUtil {
    public static ScreenUtil mInstance = new ScreenUtil();

    static public ScreenUtil getInstance() {
        return mInstance;
    }

    /*屏幕高度*/
    public int getScreenWidth(final Context context) {
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.widthPixels;
    }

    /*屏幕宽度*/
    public int getScreenHeight(final Context context) {
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.heightPixels;
    }

    /*状态栏高度*/
    public int getStatusHeight(final Context context) {

        int statusHeight = -1;
        try {
            Class<?> clazz = Class.forName("com.android.internal.R$dimen");
            Object object = clazz.newInstance();
            int height = Integer.parseInt(clazz.getField("status_bar_height")
                    .get(object).toString());
            statusHeight = context.getResources().getDimensionPixelSize(height);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return statusHeight;
    }

    //获取当前屏幕截图，包含状态栏
    public Bitmap snapShotWithStatusBar(final Activity activity) {
        View view = activity.getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap bmp = view.getDrawingCache();
        int width = getScreenWidth(activity);
        int height = getScreenHeight(activity);
        Bitmap bp = null;
        bp = Bitmap.createBitmap(bmp, 0, 0, width, height);
        view.destroyDrawingCache();
        return bp;

    }

    //获取当前屏幕截图，不包含状态栏
    public Bitmap snapShotWithoutStatusBar(final Activity activity) {
        View view = activity.getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap bmp = view.getDrawingCache();
        Rect frame = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        int statusBarHeight = frame.top;

        int width = getScreenWidth(activity);
        int height = getScreenHeight(activity);
        Bitmap bp = null;
        bp = Bitmap.createBitmap(bmp, 0, statusBarHeight, width, height
                - statusBarHeight);
        view.destroyDrawingCache();
        return bp;
    }

    public void printDevInfo(Context context) {

        /*LogUtil.getInstance().log(LogUtil.LOG_LVL_DEBUG, ClassUtil.getCls(), "-------device info start-------------");
        LogUtil.getInstance().log(LogUtil.LOG_LVL_DEBUG, ClassUtil.getCls(), "the device dpi:" + DensityUtil.getInstance(context).getDmDensityDpi());
        LogUtil.getInstance().log(LogUtil.LOG_LVL_DEBUG, ClassUtil.getCls(), "the device density scale:"
                + DensityUtil.getInstance(context).getDmDensithScale());
        LogUtil.getInstance().log(LogUtil.LOG_LVL_DEBUG, ClassUtil.getCls(), "screen width:" + ScreenUtil.getInstance().getScreenWidth(context));
        LogUtil.getInstance().log(LogUtil.LOG_LVL_DEBUG, ClassUtil.getCls(), "screen height:" + ScreenUtil.getInstance().getScreenHeight(context));
        LogUtil.getInstance().log(LogUtil.LOG_LVL_DEBUG, ClassUtil.getCls(), "screen status bar height:"
                + ScreenUtil.getInstance().getStatusHeight(context));
        LogUtil.getInstance().log(LogUtil.LOG_LVL_DEBUG, ClassUtil.getCls(), "-------device info end-------------");*/
    }
}
