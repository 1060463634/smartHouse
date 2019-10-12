package com.qqcs.smartHouse.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qqcs.smartHouse.R;
import com.qqcs.smartHouse.application.Constants;
import com.qqcs.smartHouse.application.SP_Constants;
import com.qqcs.smartHouse.models.LoginBean;
import com.qqcs.smartHouse.network.MyStringCallback;
import com.qqcs.smartHouse.utils.AESUtils;
import com.qqcs.smartHouse.utils.CommonUtil;
import com.qqcs.smartHouse.utils.ImageLoaderUtil;
import com.qqcs.smartHouse.utils.PhotoUtils;
import com.qqcs.smartHouse.utils.SharePreferenceUtil;
import com.qqcs.smartHouse.utils.ToastUtil;
import com.zhy.http.okhttp.OkHttpUtils;

import java.io.File;
import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;


public class WelcomeHomeActivity extends BaseActivity{

    public static final int REQUEST_CREATE_FAMILY = 101;


    @BindView(R.id.back_img)
    ImageView mBackImg;

    @BindView(R.id.new_layout)
    LinearLayout mNewLayout;

    @BindView(R.id.old_layout)
    LinearLayout mOldLayout;

    @BindView(R.id.new_date_tv)
    TextView mNewDateTv;

    @BindView(R.id.old_date_tv)
    TextView mOldDateTv;

    @BindView(R.id.create_home_btn)
    Button mCreateHomeBtn;

    @BindView(R.id.enter_home_btn)
    Button mEnterHomeBtn;

    boolean fromLogin = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_home);
        ButterKnife.bind(this);
        initView();

    }


    private void initView() {
        fromLogin = getIntent().getBooleanExtra("fromLogin",true);

        Calendar calendar = Calendar.getInstance();
        String date = calendar.get(Calendar.YEAR) + ",  "
                + (calendar.get(Calendar.MONTH) + 1) + "月";
        mNewDateTv.setText(date);
        mOldDateTv.setText(date);
        mCreateHomeBtn.setOnClickListener(this);
        mEnterHomeBtn.setOnClickListener(this);
        mBackImg.setOnClickListener(this);

        if(!fromLogin){
            mBackImg.setVisibility(View.VISIBLE);
            mNewLayout.setVisibility(View.GONE);
            mOldLayout.setVisibility(View.VISIBLE);
        }

    }


    @Override
    public void onMultiClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.create_home_btn:
                intent = new Intent(this, CreateHomeActivity.class);
                intent.putExtra("fromLogin",fromLogin);
                startActivityForResult(intent,REQUEST_CREATE_FAMILY);
                break;
            case R.id.enter_home_btn:
                intent = new Intent(this, ScanQRCodeActivity.class);
                startActivity(intent);
                break;
            case R.id.back_img:
                finish();

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode != RESULT_OK) {
            return;
        }


        switch (requestCode) {

            case REQUEST_CREATE_FAMILY:
                Intent intent = new Intent();
                setResult(RESULT_OK,intent);
                finish();

                break;

        }


    }

}
