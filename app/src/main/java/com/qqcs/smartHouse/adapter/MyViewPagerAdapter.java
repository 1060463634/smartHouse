package com.qqcs.smartHouse.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;


import com.qqcs.smartHouse.widgets.MyWebView;

import java.util.List;


/**
 * Created by Administrator on 2016/7/29.
 */
public class MyViewPagerAdapter extends PagerAdapter {

    private List<String> mDatas;
    private MyWebView[] mViews ;
    private Context mContext;

    public MyViewPagerAdapter(Context context, List<String> datas) {
        mContext = context;
        mDatas = datas;
        mViews = new MyWebView[datas.size()];
        for (int i = 0; i < datas.size(); i++) {
            mViews[i] = new MyWebView(mContext,null);
            mViews[i].loadUrl(datas.get(i));
        }
    }

    private class PagerItemClickListener implements View.OnClickListener{

        private int index;

        public PagerItemClickListener(int index) {
            this.index = index;
        }

        @Override
        public void onClick(View view) {


        }
    }

    @Override
    public int getCount() {
        return mViews.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView(mViews[position]);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        ((ViewPager) container).addView(mViews[position]);
        return mViews[position];
    }
}
