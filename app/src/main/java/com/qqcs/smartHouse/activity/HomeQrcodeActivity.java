package com.qqcs.smartHouse.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.qqcs.smartHouse.R;
import com.qqcs.smartHouse.application.Constants;
import com.qqcs.smartHouse.application.SP_Constants;
import com.qqcs.smartHouse.utils.AESUtils;
import com.qqcs.smartHouse.utils.SharePreferenceUtil;
import com.qqcs.smartHouse.utils.ZXingUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.MediaType;


public class HomeQrcodeActivity extends BaseActivity{



    @BindView(R.id.qrcode_img)
    ImageView mQrcodeImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_qrcode);
        ButterKnife.bind(this);
        setTitleName("家庭二维码");
        initView();

    }


    private void initView(){
        String familyId = (String) SharePreferenceUtil.
                get(this, SP_Constants.CURRENT_FAMILY_ID,"");
        String encryptFamilyId = AESUtils.encrypt(familyId,
                Constants.PASSWORD_ENCRYPT_SEED, AESUtils.MODE_BASE64);


        Bitmap qrCodeBitmap = ZXingUtils.createQRImage(encryptFamilyId,
                1024,1024);
        mQrcodeImg.setImageBitmap(qrCodeBitmap);


    }




    @Override
    public void onMultiClick(View v) {
        switch (v.getId()){
            case R.id.save_btn:
                break;

        }
    }

}
