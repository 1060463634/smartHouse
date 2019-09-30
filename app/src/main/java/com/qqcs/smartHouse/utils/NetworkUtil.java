package com.qqcs.smartHouse.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkUtil {

    public static final int NO_NETWORK = 0x01;
    public static final int NETTYPE_WIFI = 0x02;
    public static final int NETTYPE_MOBILE = 0x03;

    /**
     * 检测当的网络（WLAN、3G/2G）状态
     * @param context Context
     * @return true 表示网络可用
     */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo info = connectivity.getActiveNetworkInfo();
            if (info != null && info.isConnected())
            {
                // 当前网络是连接的
                if (info.getState() == NetworkInfo.State.CONNECTED)
                {
                    // 当前所连接的网络可用
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Get current connected network type.
     *
     * @return {@code int}. 0:NETWORK NULL 1:WIFI 2:Mobile
     */
    public static String getNetworkType(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo == null) {
            return "noNetwork";
        } else {
            if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                return "WAN";
            } else if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                return "wifi";
            } else {
                return "Unknown";
            }
        }
    }
}
