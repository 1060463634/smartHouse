package com.qqcs.smartHouse.application;

import android.view.View;

import com.qqcs.smartHouse.utils.ToastUtil;

public abstract class OnMultiClickListener implements View.OnClickListener{

    // 两次点击按钮之间的点击间隔不能少于1000毫秒
    private static final int MIN_CLICK_DELAY_TIME = 1000;
    private static long lastClickTime;

    public abstract void onMultiClick(View v);

    @Override
    public void onClick(View v) {
        long curClickTime = System.currentTimeMillis();
        if((curClickTime - lastClickTime) >= MIN_CLICK_DELAY_TIME) {
            // 超过点击间隔后再将lastClickTime重置为当前点击时间
            lastClickTime = curClickTime;
            onMultiClick(v);
        } else {
            ToastUtil.showToast(MyApplication.getApplication()
                    .getApplicationContext(),"您手速太快了吧～");
        }
    }

}
