package com.qqcs.smartHouse.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.qqcs.smartHouse.R;
import com.qqcs.smartHouse.application.Constants;
import com.qqcs.smartHouse.application.SP_Constants;
import com.qqcs.smartHouse.models.QrFamilyInfoBean;
import com.qqcs.smartHouse.models.UploadPicBean;
import com.qqcs.smartHouse.network.MyStringCallback;
import com.qqcs.smartHouse.utils.BitmapUtil;
import com.qqcs.smartHouse.utils.CommonUtil;
import com.qqcs.smartHouse.utils.ImageLoaderUtil;
import com.qqcs.smartHouse.utils.MD5Utils;
import com.qqcs.smartHouse.utils.PhotoUtils;
import com.qqcs.smartHouse.utils.SharePreferenceUtil;
import com.qqcs.smartHouse.utils.ToastUtil;
import com.qqcs.smartHouse.widgets.MyAlertDialog;
import com.qqcs.smartHouse.widgets.SelectPictureDialog;
import com.zhy.http.okhttp.OkHttpUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.MediaType;

import static com.qqcs.smartHouse.activity.CreateHomeActivity.REQUEST_CAPTURE_PIC;
import static com.qqcs.smartHouse.activity.CreateHomeActivity.REQUEST_CROP_PIC;
import static com.qqcs.smartHouse.activity.CreateHomeActivity.REQUEST_SELECT_DEMO;
import static com.qqcs.smartHouse.activity.CreateHomeActivity.REQUEST_SELECT_PIC;


public class QrHomeDetailActivity extends BaseActivity{


    @BindView(R.id.home_img)
    ImageView mHomeImg;

    @BindView(R.id.home_name_tv)
    TextView mHomeNameTV;

    @BindView(R.id.home_address_tv)
    TextView mHomeAddressTV;

    @BindView(R.id.home_host_tv)
    TextView mHomeHostTV;

    @BindView(R.id.enter_btn)
    Button mEnterBtn;

    private String familyId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_home_detail);
        ButterKnife.bind(this);
        setTitleName("家庭简介");
        initView();

    }


    private void initView(){

        //获取传入值
        Intent intent = getIntent();
        familyId = intent.getStringExtra("familyId");

        getFamilyInfo();

        mEnterBtn.setOnClickListener(this);

    }


    private void getFamilyInfo(){
        String accessToken = (String) SharePreferenceUtil.
                get(this, SP_Constants.ACCESS_TOKEN,"");
        String timestamp = System.currentTimeMillis() + "";
        JSONObject object = CommonUtil.getRequstJson(getApplicationContext());
        JSONObject dataObject = new JSONObject();

        try {
            dataObject.put("familyId", familyId);
            dataObject.put("timestamp", timestamp);

            object.put("data", dataObject);
            object.put("sign", MD5Utils.md5s(familyId + timestamp));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        OkHttpUtils
                .postString()
                .tag(this)
                .url(Constants.HTTP_GET_FAMILY)
                .addHeader("access-token",accessToken)
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .content(object.toString())
                .build()
                .execute(new MyStringCallback<QrFamilyInfoBean>(this, QrFamilyInfoBean.class, false, false) {

                    @Override
                    public void onSuccess(QrFamilyInfoBean data) {
                        //初始化页面
                        ImageLoaderUtil.getInstance().displayImage(
                                Constants.HTTP_SERVER_DOMAIN + data.getImg(),mHomeImg);
                        mHomeNameTV.setText(data.getFamilyName());
                        mHomeAddressTV.setText(data.getLocation());
                        mHomeHostTV.setText(data.getOwnerName());
                    }


                    @Override
                    public void onFailure(String message) {
                        ToastUtil.showToast(QrHomeDetailActivity.this, message);
                    }
                });
    }

    private void enterFamily(){
        String accessToken = (String) SharePreferenceUtil.
                get(this, SP_Constants.ACCESS_TOKEN,"");
        String timestamp = System.currentTimeMillis() + "";
        JSONObject object = CommonUtil.getRequstJson(getApplicationContext());
        JSONObject dataObject = new JSONObject();

        try {
            dataObject.put("familyId", familyId);
            dataObject.put("timestamp", timestamp);

            object.put("data", dataObject);
            object.put("sign", MD5Utils.md5s(familyId + timestamp));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        OkHttpUtils
                .postString()
                .tag(this)
                .url(Constants.HTTP_ENTER_FAMILY)
                .addHeader("access-token",accessToken)
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .content(object.toString())
                .build()
                .execute(new MyStringCallback<Object>(this, Object.class, true, false) {

                    @Override
                    public void onSuccess(Object data) {
                        ToastUtil.showToast(QrHomeDetailActivity.this,"申请提交成功，等待户主确认");
                        finish();
                    }


                    @Override
                    public void onFailure(String message) {
                        ToastUtil.showToast(QrHomeDetailActivity.this, message);
                    }
                });
    }


    @Override
    public void onMultiClick(View v) {
        Intent intent;
        switch (v.getId()){
            case R.id.enter_btn:
                enterFamily();
                break;


        }
    }



}
