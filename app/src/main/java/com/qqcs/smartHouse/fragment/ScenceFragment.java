package com.qqcs.smartHouse.fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.qqcs.smartHouse.R;
import com.qqcs.smartHouse.activity.CreateHomeActivity;
import com.qqcs.smartHouse.activity.LoginActivity;
import com.qqcs.smartHouse.activity.MainActivity;
import com.qqcs.smartHouse.activity.WelcomeHomeActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


/**
 * Created by ameng on 2016/6/15.
 */
public class ScenceFragment extends BaseFragment {

    private View mRootView;
    private Context mContext;
    private Unbinder bind;

    @BindView(R.id.morning_scene_img)
    ImageView mMoringImg;

    @BindView(R.id.leave_scene_img)
    ImageView mLeaveImg;

    @BindView(R.id.back_scene_img)
    ImageView mBackImg;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setSystemBarWhite();
        if (mRootView == null) {
            mRootView = inflater.inflate(R.layout.fragment_scence, null);

            bind = ButterKnife.bind(this, mRootView);

            initView();
        }
        ViewGroup parent = (ViewGroup) mRootView.getParent();
        if (parent != null) {
            parent.removeView(mRootView);
        }

        return mRootView;
    }



    private void initView(){
        mMoringImg.setOnClickListener(this);
        mLeaveImg.setOnClickListener(this);
        mBackImg.setOnClickListener(this);

    }

    @Override
    public void onMultiClick(View v) {
        Toast toast2;
        View view;
        switch (v.getId()) {
            case R.id.morning_scene_img:

                toast2 = new Toast(mContext);
                view = LayoutInflater.from(mContext).inflate(R.layout.toast_commom, null);
                ((TextView)view.findViewById(R.id.text)).setText("开启早安模式");
                toast2.setView(view);
                toast2.setGravity(Gravity.CENTER, 0, 0);
                toast2.show();
                break;

            case R.id.leave_scene_img:
                toast2 = new Toast(mContext);
                view = LayoutInflater.from(mContext).inflate(R.layout.toast_commom, null);
                ((TextView)view.findViewById(R.id.text)).setText("开启外出模式");
                toast2.setView(view);
                toast2.setGravity(Gravity.CENTER, 0, 0);
                toast2.show();
                break;

            case R.id.back_scene_img:
                toast2 = new Toast(mContext);
                view = LayoutInflater.from(mContext).inflate(R.layout.toast_commom, null);
                ((TextView)view.findViewById(R.id.text)).setText("开启回家模式");
                toast2.setView(view);
                toast2.setGravity(Gravity.CENTER, 0, 0);
                toast2.show();
                break;
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        //解除绑定
        bind.unbind();
    }


}
