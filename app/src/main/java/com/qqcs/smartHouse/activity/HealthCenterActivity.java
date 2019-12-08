package com.qqcs.smartHouse.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.qqcs.smartHouse.R;
import com.qqcs.smartHouse.application.Constants;
import com.qqcs.smartHouse.utils.AESUtils;
import com.qqcs.smartHouse.utils.ZXingUtils;

import butterknife.BindView;
import butterknife.ButterKnife;


public class HealthCenterActivity extends BaseActivity{



    @BindView(R.id.foot_layout)
    RelativeLayout mFootLayout;

    private String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_health_center);
        ButterKnife.bind(this);
        name = getIntent().getStringExtra("name");
        setTitleName(name);
        initView();

    }


    private void initView(){
        mFootLayout.setOnClickListener(this);

    }




    @Override
    public void onMultiClick(View v) {
        switch (v.getId()){
            case R.id.foot_layout:
                Intent intent = new Intent(this, FootActivity.class);
                intent.putExtra("name",name);
                startActivity(intent);
                break;

        }
    }

}
