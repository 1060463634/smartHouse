package com.qqcs.smartHouse.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.qqcs.smartHouse.R;

import butterknife.BindView;
import butterknife.ButterKnife;


public class UserInfoActivity extends BaseActivity implements View.OnClickListener{

    @BindView(R.id.nick_name_layout)
    RelativeLayout mNickNameLayout;

    @BindView(R.id.phone_layout)
    RelativeLayout mPhoneLayout;

    @BindView(R.id.exit_login_btn)
    Button mExitLoginLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        ButterKnife.bind(this);
        setTitleName("个人信息");
        initView();

    }


    private void initView(){
        mNickNameLayout.setOnClickListener(this);
        mPhoneLayout.setOnClickListener(this);
        mExitLoginLayout.setOnClickListener(this);

    }





    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()){
            case R.id.nick_name_layout:

                break;
            case R.id.phone_layout:
                intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.exit_login_btn:

                break;

        }
    }

}
