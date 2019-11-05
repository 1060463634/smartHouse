package com.qqcs.smartHouse.demo;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.annotation.SuppressLint;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;

@SuppressLint("NewApi")
public class AnimationDemo {

	private static Animation createExpandAnimation() {
		AnimationSet animationSet = new AnimationSet(true);
		animationSet.setFillAfter(true);
		animationSet.setDuration(2000);
		animationSet.setStartOffset(0);
		animationSet.setRepeatMode(Animation.REVERSE);
		animationSet.setInterpolator(new LinearInterpolator());

		//0表示的是完全透明，１表示的是完全不透明
		Animation alphaAnimation = new AlphaAnimation(0, 1);
		animationSet.addAnimation(alphaAnimation);
		
		Animation translateAnimation = new TranslateAnimation(0, 100, 0, 100);
		animationSet.addAnimation(translateAnimation);
		
		//０表示的就是看不见，１表示原始大小
		Animation scaleAnimation = new ScaleAnimation(0, 1, 0, 1, Animation.RELATIVE_TO_SELF, 0.5f,
				Animation.RELATIVE_TO_SELF, 0.5f);
		animationSet.addAnimation(scaleAnimation);

		Animation rotateAnimation = new RotateAnimation(0, 50, Animation.RELATIVE_TO_SELF, 0.5f, 
				Animation.RELATIVE_TO_SELF, 0.5f);
		animationSet.addAnimation(rotateAnimation);

		return animationSet;
	}
	
	
	private void PropertyAnimator(final View view){
		
		ValueAnimator animator = ValueAnimator.ofFloat(0, 100f);  //定义动画
		animator.setTarget(view);   //设置作用目标
		animator.setDuration(5000).start();
		animator.addUpdateListener(new AnimatorUpdateListener() {
		    @Override
		    public void onAnimationUpdate(ValueAnimator animation){
		        float value =  (Float) animation.getAnimatedValue();
		        view.setMinimumHeight((int) value);//必须通过这里设置属性值才有效
		    }
		});
		
		//ObjectAnimator：继承自ValueAnimator，允许你指定要进行动画的对象以及该对象的一个属性。
		ObjectAnimator.ofFloat(view, "rotationY", 0.0f, 360.0f).setDuration(1000).start();
	}
	
	
	

}
