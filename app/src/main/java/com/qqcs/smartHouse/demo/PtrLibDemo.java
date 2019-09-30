package com.qqcs.smartHouse.demo;

import android.os.Bundle;


import com.qqcs.smartHouse.R;
import com.qqcs.smartHouse.activity.BaseActivity;
import com.qqcs.smartHouse.adapter.MyListViewAdapter;
import com.qqcs.smartHouse.utils.CommonUtil;
import com.qqcs.smartHouse.utils.ToastUtil;
import com.qqcs.smartHouse.widgets.XListView;

import java.util.ArrayList;

import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.header.MaterialHeader;


/**
 * Created by hello on 2017/6/23.
 */
public class PtrLibDemo extends BaseActivity {
    PtrFrameLayout mPtrFrame;
    XListView mListView;
    ArrayList<String> dataSource = new ArrayList<String>();
    MyListViewAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ptr_lib_demo);
        setTitle("消息列表");

        mPtrFrame = findViewById(R.id.ptr_frame);
        mListView = findViewById(R.id.list_view);

        // header
        final MaterialHeader header = new MaterialHeader(this);
        int[] colors = getResources().getIntArray(R.array.google_colors);
        header.setColorSchemeColors(colors);
        header.setLayoutParams(new PtrFrameLayout.LayoutParams(-1, -2));
        header.setPadding(0, CommonUtil.dip2px(this,15), 0, CommonUtil.dip2px(this,10));
        header.setPtrFrameLayout(mPtrFrame);
        mPtrFrame.setHeaderView(header);
        mPtrFrame.addPtrUIHandler(header);

        initView();
    }


    private void initView(){
        mPtrFrame.setPtrHandler(new PtrDefaultHandler() {
            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                mListView.setPullLoadEnable(false);
                frame.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        getDatas(0,10);
                    }
                }, 1000);
            }
        });
        mListView.setPullLoadEnable(true);
        mListView.setXListViewListener(new XListView.IXListViewListener() {
            @Override
            public void onLoadMore() {
                mPtrFrame.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        getDatas(dataSource.size(),10);
                    }
                }, 1000);
            }
        });


        mPtrFrame.postDelayed(new Runnable() {
            @Override
            public void run() {
                mPtrFrame.autoRefresh(true);
            }
        }, 100);

    }

    /**
     * 初始化数据
     */
    private void getDatas(final int startIndex,int count) {
        // 初始化ListView中展示的数据
        ArrayList<String> temp = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            temp.add("Existed Old List Item " + i);
        }

        //来自下拉刷新
        if(startIndex == 0){
            mPtrFrame.refreshComplete();
            mListView.setPullLoadEnable(true);
            if(temp.size() != 0){
                //还有数据
                dataSource.clear();
                dataSource.addAll(temp);
                setMyAdapter();
            }else{
                //没有数据
                //multiStateView.setViewState(MultiStateView.VIEW_STATE_ERROR);
            }

        }else{
            //来自上拉加载
            mListView.stopLoadMore();
            if(temp.size() != 0){
                //还有数据
                dataSource.addAll(temp);
                setMyAdapter();

            }else{
                //没有数据
                ToastUtil.showToast(this,"没有更多了");
                mListView.setPullLoadEnable(false);
            }

        }

    }

    public void setMyAdapter() {
        if (mAdapter == null) {
            mAdapter = new MyListViewAdapter(this,dataSource);
            mListView.setAdapter(mAdapter);
        }else{
            mAdapter.refreshData(dataSource);
        }

    }

}
