package com.qqcs.smartHouse.utils;


import android.content.Context;
import android.util.DisplayMetrics;

/**
 * 计算公式 pixels = dips * (density / 160)
 */
public class DensityUtil {

    private static final String TAG = DensityUtil.class.getSimpleName();

    // 当前屏幕的densityDpi
    private static float dmDensityDpi = 0.0f;
    private static DisplayMetrics dm;
    private static float scale = 0.0f;

    public static DensityUtil mInstance;

    /**
     * 根据构造函数获得当前手机的屏幕系数
     */
    public DensityUtil(Context context) {
        // 获取当前屏幕
        dm = new DisplayMetrics();
        dm = context.getApplicationContext().getResources().getDisplayMetrics();
        // 设置DensityDpi
        setDmDensityDpi(dm.densityDpi);
        // 密度因子
        scale = getDmDensityDpi() / 160;

    }

    public static DensityUtil getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new DensityUtil(context);
        }
        return mInstance;
    }

    /**
     * 当前屏幕的density因子
     */
    public float getDmDensityDpi() {
        return dmDensityDpi;
    }

    public float getDmDensithScale() {
        return scale;
    }

    /**
     * 当前屏幕的density因子
     */
    public void setDmDensityDpi(float dmDensityDpi) {
        DensityUtil.dmDensityDpi = dmDensityDpi;
    }

    /**
     * 密度转换像素
     */
    public int dip2px(float dipValue) {

        return (int) (dipValue * scale + 0.5f);

    }

    public int px2sp( float pxValue) {
        final float fontScale = dm.scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    public int sp2px( float spValue) {
        final float fontScale = dm.scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    /**
     * 像素转换密度
     */
    public int px2dip(float pxValue) {
        return (int) (pxValue / scale + 0.5f);
    }

    @Override
    public String toString() {
        return " dmDensityDpi:" + dmDensityDpi;
    }
}
