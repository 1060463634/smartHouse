package com.qqcs.smartHouse.utils;


import android.content.Context;
import android.widget.Toast;

public class ToastUtil {

    public static void showToast(Context context, String info) {
        Toast.makeText(context, info, Toast.LENGTH_SHORT).show();
    }

    public static void showToast(Context context, int id) {
        Toast.makeText(context, context.getString(id), Toast.LENGTH_SHORT).show();
    }




   
}
