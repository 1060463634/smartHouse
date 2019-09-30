package com.qqcs.smartHouse.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.qqcs.smartHouse.R;
import com.qqcs.smartHouse.adapter.PhotoGridAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class DemoHomePictureActivity extends BaseActivity {



    @BindView(R.id.photo_grid)
    GridView mGridView;

    private PhotoGridAdapter mAdapter;
    private List<Integer> mDataLists = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo_home_picture);
        ButterKnife.bind(this);
        setTitleName("摄影师作品");
        initView();

    }


    private void initView(){
        mDataLists.add(R.drawable.ic_home_1);
        mDataLists.add(R.drawable.ic_home_2);
        mDataLists.add(R.drawable.ic_home_3);
        mDataLists.add(R.drawable.ic_home_4);
        mDataLists.add(R.drawable.ic_home_5);
        mDataLists.add(R.drawable.ic_home_6);
        mDataLists.add(R.drawable.ic_home_7);
        mDataLists.add(R.drawable.ic_home_8);
        mDataLists.add(R.drawable.ic_home_9);
        mDataLists.add(R.drawable.ic_home_10);

        setMyAdapter();
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                intent.putExtra("picture_id",mDataLists.get(position));
                setResult(RESULT_OK,intent);
                finish();
            }
        });

    }

    public void setMyAdapter() {
        if (mAdapter == null) {
            mAdapter = new PhotoGridAdapter(this, mDataLists);
            mGridView.setAdapter(mAdapter);
        } else {
            mAdapter.refreshData(mDataLists);
        }

    }

}
