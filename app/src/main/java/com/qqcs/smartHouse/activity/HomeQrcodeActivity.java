package com.qqcs.smartHouse.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.qqcs.smartHouse.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.MediaType;


public class HomeQrcodeActivity extends BaseActivity{





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_qrcode);
        ButterKnife.bind(this);
        setTitleName("家庭二维码");
        initView();

    }


    private void initView(){

    }




    @Override
    public void onMultiClick(View v) {
        switch (v.getId()){
            case R.id.save_btn:
                break;

        }
    }

}
