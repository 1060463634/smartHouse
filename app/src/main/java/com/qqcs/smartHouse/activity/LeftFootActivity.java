package com.qqcs.smartHouse.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.qqcs.smartHouse.R;
import com.qqcs.smartHouse.utils.LogUtil;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;


public class LeftFootActivity extends BaseActivity {

    //脚后跟
    @BindView(R.id.heel_img1)
    ImageView mHeelImg1;
    @BindView(R.id.heel_img2)
    ImageView mHeelImg2;
    @BindView(R.id.heel_img3)
    ImageView mHeelImg3;

    //脚心
    @BindView(R.id.arch_img1)
    ImageView mArchImg1;
    @BindView(R.id.arch_img2)
    ImageView mArchImg2;
    @BindView(R.id.arch_img3)
    ImageView mArchImg3;

    //脚心
    @BindView(R.id.sole_img1)
    ImageView mSoleImg1;
    @BindView(R.id.sole_img2)
    ImageView mSoleImg2;
    @BindView(R.id.sole_img3)
    ImageView mSoleImg3;

    //大拇指
    @BindView(R.id.thumb_img1)
    ImageView mThumbImg1;
    @BindView(R.id.thumb_img2)
    ImageView mThumbImg2;
    @BindView(R.id.thumb_img3)
    ImageView mThumbImg3;

    //食指
    @BindView(R.id.indextoe_img1)
    ImageView mIndexImg1;
    @BindView(R.id.indextoe_img2)
    ImageView mIndexImg2;

    //中指
    @BindView(R.id.middletoe_img1)
    ImageView mMiddleImg1;
    @BindView(R.id.middletoe_img2)
    ImageView mMiddleImg2;

    //无名指
    @BindView(R.id.ringtoe_img1)
    ImageView mRingImg1;

    //小指
    @BindView(R.id.littletoe_img1)
    ImageView mLittleImg1;


    Handler mhandler = new Handler();
    Timer timer ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_left_foot);
        ButterKnife.bind(this);
        initView();

    }

    boolean reverse = true;

    private void initView() {

        timer = new Timer();
        timer.schedule(new TimerTask() {
            public void run() {
                reverse = !reverse;
                if (reverse) {
                    doAni(0, 1);

                } else {
                    doAni(1, 0);
                }
            }
        }, 200, 3100);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        timer.cancel();
    }

    private void doAni(float from, float to) {
        doAnimation(mHeelImg1, mHeelImg2, mHeelImg3, from, to);
        doAnimation(mArchImg1, mArchImg2, mArchImg3, from, to);
        doAnimation(mSoleImg1, mSoleImg2, mSoleImg3, from, to);
        doAnimation(mThumbImg1, mThumbImg2, mThumbImg3, from, to);

        doAnimation(mIndexImg1, mIndexImg2, null, from, to);
        doAnimation(mMiddleImg1, mMiddleImg2, null, from, to);
        doAnimation(mRingImg1, null, null, from, to);
        doAnimation(mLittleImg1, null, null, from, to);

    }


    private void doAnimation(final View v1, final View v2, final View v3, float from, float to) {

        if(v1 != null && v2 != null && v3 != null){
            final Animation animation1 = createAnimation(v1, v3, from, to, 3000);
            final Animation animation2 = createAnimation(v2, v3, from, to, 2000);
            final Animation animation3 = createAnimation(v3, v3, from, to, 1000);

            if (from > to) {
                v1.startAnimation(animation1);
                v2.startAnimation(animation2);
                v3.startAnimation(animation3);
            } else {
                v1.startAnimation(animation1);
                mhandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        v2.startAnimation(animation2);
                    }
                }, 1000);
                mhandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        v3.startAnimation(animation3);
                    }
                }, 2000);
            }


        } else if(v1 != null && v2 != null && v3 == null){
            final Animation animation1 = createAnimation(v1, v2, from, to, 2000);
            final Animation animation2 = createAnimation(v2, v2, from, to, 1000);

            if (from > to) {
                v1.startAnimation(animation1);
                v2.startAnimation(animation2);
            } else {
                mhandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        v1.startAnimation(animation1);;
                    }
                }, 1000);
                mhandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        v2.startAnimation(animation2);
                    }
                }, 2000);
            }

        } else if(v1 != null && v2 == null && v3 == null){
            final Animation animation1 = createAnimation(v1, v1, from, to, 1000);

            if (from > to) {
                v1.startAnimation(animation1);
            } else {
                mhandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        v1.startAnimation(animation1);
                    }
                }, 2000);

            }
        }

    }

    private Animation createAnimation(View self, View target, float from, float to, int time) {
        int y = target.getTop() - self.getTop() + target.getHeight() / 2;
        int x = target.getLeft() - self.getLeft() + target.getWidth() / 2;

        //０表示的就是看不见，１表示原始大小
        Animation scaleAnimation = new ScaleAnimation(from, to, from, to,
                Animation.ABSOLUTE, x, Animation.ABSOLUTE, y);
        scaleAnimation.setRepeatMode(Animation.REVERSE);
        scaleAnimation.setDuration(time);
        scaleAnimation.setFillAfter(true);
        scaleAnimation.setInterpolator(new LinearInterpolator());

        return scaleAnimation;
    }


    @Override
    public void onMultiClick(View v) {
        Intent intent;
        switch (v.getId()) {

            case R.id.gait_layout:

                break;

        }
    }


}
