package com.qqcs.smartHouse.widgets;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.qqcs.smartHouse.R;
import com.qqcs.smartHouse.activity.AirConditionControlActivity;
import com.qqcs.smartHouse.adapter.HomesPopAdapter;
import com.qqcs.smartHouse.application.Constants;
import com.qqcs.smartHouse.application.SP_Constants;
import com.qqcs.smartHouse.fragment.HomeFragment;
import com.qqcs.smartHouse.models.EventBusBean;
import com.qqcs.smartHouse.models.FamilyInfoBean;
import com.qqcs.smartHouse.network.CommonJsonList;
import com.qqcs.smartHouse.network.MyStringCallback;
import com.qqcs.smartHouse.utils.CommonUtil;
import com.qqcs.smartHouse.utils.MD5Utils;
import com.qqcs.smartHouse.utils.SharePreferenceUtil;
import com.qqcs.smartHouse.utils.ToastUtil;
import com.zhy.http.okhttp.OkHttpUtils;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;

public class SpeedPopupWindow {


    private PopupWindow popupWindow;
    private Context mContext;
    private Activity mActivity;
    private View view;

    private LinearLayout mQiangLayout;
    private LinearLayout mRuoLayout;
    private LinearLayout mWeiLayout;
    private LinearLayout mAutoLayout;
    private LinearLayout mCancelLayout;

    /**
     *
     * 构造函数
     * @param context
     * @param view 在这个控件下面
     */
    public SpeedPopupWindow(Context context, Activity activity, View view) {
        this.mContext = context;
        this.mActivity = activity;
        this.view = view;
        initPopupWindow();
    }

    public void show() {
        if (popupWindow == null) {
            return;
        }
        if (popupWindow.isShowing()) {
            popupWindow.dismiss();
        } else {
            popupWindow.showAtLocation(view, Gravity.BOTTOM,0, 0);
            backgroundAlpha(0.5f);
        }

    }

    /**
     * 初始化
     */
    public void initPopupWindow() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_speed_pop, null);
        mQiangLayout = view.findViewById(R.id.qiangfeng_layout);
        mRuoLayout = view.findViewById(R.id.ruofeng_layout);
        mWeiLayout = view.findViewById(R.id.weifeng_layout);
        mAutoLayout = view.findViewById(R.id.auto_layout);
        mCancelLayout = view.findViewById(R.id.cancel_layout);

        mCancelLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (popupWindow.isShowing()) {
                    popupWindow.dismiss();
                }
            }
        });

        mQiangLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((AirConditionControlActivity)mActivity).setSpeed("1",true);
                popupWindow.dismiss();
            }
        });

        mRuoLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((AirConditionControlActivity)mActivity).setSpeed("2",true);
                popupWindow.dismiss();
            }
        });

        mWeiLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((AirConditionControlActivity)mActivity).setSpeed("3",true);
                popupWindow.dismiss();
            }
        });

        mAutoLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((AirConditionControlActivity)mActivity).setSpeed("4",true);
                popupWindow.dismiss();
            }
        });


        popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupWindow.setOutsideTouchable(true);
        popupWindow.setTouchable(true);
        popupWindow.setAnimationStyle(R.style.mypopwindow_anim_style);
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                backgroundAlpha(1f);
            }
        });

    }

    /**
     * 设置添加屏幕的背景透明度
     * @param bgAlpha
     */
    public void backgroundAlpha(float bgAlpha)
    {
        WindowManager.LayoutParams lp = mActivity.getWindow().getAttributes();
        lp.alpha = bgAlpha; //0.0-1.0
        mActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        mActivity.getWindow().setAttributes(lp);
    }


}
