package com.qqcs.smartHouse.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.qqcs.smartHouse.R;
import com.zhy.http.okhttp.OkHttpUtils;


public abstract class BaseActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initSystemBar(this, R.color.system_bar_bg);
    }

    /**
     * 修改状态栏颜色，支持4.4以上版本
     * @param activity
     * @param res
     */
    public  void initSystemBar(Activity activity, int res) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            if(this instanceof LoginActivity || this instanceof WelcomeHomeActivity
                    || this instanceof MainActivity){
                View decorView = window.getDecorView();
                decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
                window.setStatusBarColor(Color.TRANSPARENT);

            }else {
                View decorView = window.getDecorView();
                decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                        View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                window.setStatusBarColor(activity.getResources().getColor(res));
            }

        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //使用SystemBarTint库使4.4版本状态栏变色，需要先将状态栏设置为透明
//            setTranslucentStatus(activity, true);
//            SystemBarTintManager tintManager = new SystemBarTintManager(activity);
//            tintManager.setStatusBarTintEnabled(true);
//            tintManager.setStatusBarTintResource(res);
        }

    }
    @TargetApi(19)
    private static void setTranslucentStatus(Activity activity, boolean on) {
        Window win = activity.getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();

        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }


    /**
     * 设置Activity标题
     */
    public void setTitle(int resId) {
        RelativeLayout layout = (RelativeLayout) findViewById(R.id.layout_top);
        TextView textView = (TextView) layout.findViewById(R.id.tv_activity_title);
        textView.setText(resId);
    }

    /**
     * 设置Activity标题
     */
    public void setTitleName(String titleName) {
        RelativeLayout layout = (RelativeLayout) findViewById(R.id.layout_top);
        TextView textView = (TextView) layout.findViewById(R.id.tv_activity_title);
        textView.setText(titleName);
    }

    /**
     * 设置点击监听器
     *
     * @param listener
     */
    public void setOptionsButtonListener(View.OnClickListener listener) {
        RelativeLayout layout = (RelativeLayout) findViewById(R.id.layout_top);
        ImageView optionsButton = (ImageView) layout.findViewById(R.id.more_img);
        optionsButton.setOnClickListener(listener);
    }

    /**
     * 不显示返回按钮
     */
    public void setBackButtonInVisible() {
        RelativeLayout layout = (RelativeLayout) findViewById(R.id.layout_top);
        ImageView optionsButton = (ImageView) layout.findViewById(R.id.back_img);
        optionsButton.setVisibility(View.INVISIBLE);
    }


    /**
     * 不显示设置按钮
     */
    public void setOptionsButtonVisible() {
        RelativeLayout layout = (RelativeLayout) findViewById(R.id.layout_top);
        ImageView optionsButton = (ImageView) layout.findViewById(R.id.more_img);
        optionsButton.setVisibility(View.VISIBLE);
    }

    /**
     * 回退事件
     *
     * @param v
     */
    public void onBack(View v) {
        super.onBackPressed();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        OkHttpUtils.getInstance().cancelTag(this);
    }

}
