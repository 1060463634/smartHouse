package com.qqcs.smartHouse.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qqcs.smartHouse.R;
import com.qqcs.smartHouse.application.Constants;
import com.qqcs.smartHouse.application.SP_Constants;
import com.qqcs.smartHouse.models.DeviceBean;
import com.qqcs.smartHouse.models.PropBean;
import com.qqcs.smartHouse.network.CommonJsonList;
import com.qqcs.smartHouse.network.MyStringCallback;
import com.qqcs.smartHouse.utils.CommonUtil;
import com.qqcs.smartHouse.utils.MD5Utils;
import com.qqcs.smartHouse.utils.SharePreferenceUtil;
import com.qqcs.smartHouse.utils.ToastUtil;
import com.zhy.http.okhttp.OkHttpUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.MediaType;


public class TvControlActivity extends BaseActivity{

    public static final int REQUEST_CREATE_FAMILY = 101;
    @BindView(R.id.tv_ok_img)
    ImageView mTvOkImg;

    @BindView(R.id.tv_up_img)
    ImageView mTvUpImg;

    @BindView(R.id.tv_down_img)
    ImageView mTvDownImg;

    @BindView(R.id.tv_left_img)
    ImageView mTvLeftImg;

    @BindView(R.id.tv_right_img)
    ImageView mTvRightImg;

    @BindView(R.id.tv_open_img)
    ImageView mTvOpenImg;

    @BindView(R.id.volume_add_img)
    ImageView mVolumeAddImg;

    @BindView(R.id.volume_delete_img)
    ImageView mVolumeDeleteImg;

    @BindView(R.id.channel_add_img)
    ImageView mChannelAddImg;

    @BindView(R.id.channel_delete_img)
    ImageView mChannelDeleteImg;

    @BindView(R.id.tv_home_img)
    ImageView mTvHomeImg;

    @BindView(R.id.tv_back_img)
    ImageView mTvBackImg;

    private String mDeviceId;
    private String mPropId;
    private String mCommandType;
    private String mTitleName;
    private List<PropBean> propBeans = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tv_control);
        ButterKnife.bind(this);
        mDeviceId = getIntent().getStringExtra("deviceId");
        mPropId = getIntent().getStringExtra("propId");
        mCommandType = getIntent().getStringExtra("commandType");
        mTitleName = getIntent().getStringExtra("titleName");

        if(mCommandType.equalsIgnoreCase("F1")){
            setTitleName(mTitleName + " - 学习模式");
        }else {
            setTitleName(mTitleName);
        }

        initView();

    }

    private void initView() {

        mTvOkImg.setOnClickListener(this);
        mTvUpImg.setOnClickListener(this);
        mTvDownImg.setOnClickListener(this);
        mTvLeftImg.setOnClickListener(this);
        mTvRightImg.setOnClickListener(this);
        mTvOpenImg.setOnClickListener(this);

        mVolumeAddImg.setOnClickListener(this);
        mVolumeDeleteImg.setOnClickListener(this);
        mChannelAddImg.setOnClickListener(this);
        mChannelDeleteImg.setOnClickListener(this);
        mTvHomeImg.setOnClickListener(this);
        mTvBackImg.setOnClickListener(this);


    }

    private void getDeviceProp() {

        String accessToken = (String) SharePreferenceUtil.
                get(this, SP_Constants.ACCESS_TOKEN,"");

        String timestamp = System.currentTimeMillis() + "";
        JSONObject object = CommonUtil.getRequstJson(getApplicationContext());
        JSONObject dataObject = new JSONObject();

        try {
            dataObject.put("deviceId", mDeviceId);
            dataObject.put("propId", mPropId);
            dataObject.put("timestamp", timestamp);

            object.put("data", dataObject);
            object.put("sign", MD5Utils.md5s(mDeviceId + mPropId + timestamp));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        OkHttpUtils
                .postString()
                .tag(this)
                .url(Constants.HTTP_GET_DEVICE_PROPS)
                .addHeader("access-token",accessToken)
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .content(object.toString())
                .build()
                .execute(new MyStringCallback<PropBean>(this,
                        PropBean.class, true, true) {

                    @Override
                    public void onSuccess(CommonJsonList<PropBean> json) {
                        propBeans = json.getData();
                        for(int i = 0;i < propBeans.size();i++){
                            if(propBeans.get(i).getPropTypeCode().equalsIgnoreCase("Power")){
                                String value = propBeans.get(i).getPropValue();
                                if(value.equalsIgnoreCase("0")){
                                    controlTv("Power","1",mCommandType);
                                }else {
                                    controlTv("Power","0",mCommandType);
                                }
                            }
                        }

                    }


                    @Override
                    public void onFailure(String message) {
                        ToastUtil.showToast(TvControlActivity.this, message);
                    }
                });
    }


    private void controlTv(String propTypeCode,String propValue,String commandType) {

        String accessToken = (String) SharePreferenceUtil.
                get(this, SP_Constants.ACCESS_TOKEN,"");

        String timestamp = System.currentTimeMillis() + "";
        JSONObject object = CommonUtil.getRequstJson(getApplicationContext());
        JSONObject dataObject = new JSONObject();

        try {
            dataObject.put("deviceId", mDeviceId);
            dataObject.put("propId", mPropId);
            dataObject.put("propTypeCode", propTypeCode);
            dataObject.put("propValue", propValue);
            dataObject.put("commandType", commandType);
            dataObject.put("timestamp", timestamp);

            object.put("data", dataObject);
            object.put("sign", MD5Utils.md5s(mDeviceId + mPropId
                     + propTypeCode + propValue + commandType + timestamp));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        OkHttpUtils
                .postString()
                .tag(this)
                .url(Constants.HTTP_INFRARED_COMMAND)
                .addHeader("access-token",accessToken)
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .content(object.toString())
                .build()
                .execute(new MyStringCallback<Object>(this,
                        Object.class, false, false) {

                    @Override
                    public void onSuccess(Object data) {
                        ToastUtil.showToast(TvControlActivity.this, "执行成功,等待响应");
                    }


                    @Override
                    public void onFailure(String message) {
                        ToastUtil.showToast(TvControlActivity.this, message);
                    }
                });
    }


    @Override
    public void onMultiClick(View v) {
        Intent intent;
        switch (v.getId()) {

            case R.id.tv_open_img:
                getDeviceProp();
                break;

            case R.id.tv_ok_img:
                controlTv("OK","1",mCommandType);
                break;
            case R.id.tv_up_img:
                controlTv("Up","1",mCommandType);
                break;
            case R.id.tv_down_img:
                controlTv("Down","1",mCommandType);
                break;
            case R.id.tv_left_img:
                controlTv("Left","1",mCommandType);
                break;
            case R.id.tv_right_img:
                controlTv("Right","1",mCommandType);
                break;

            case R.id.volume_add_img:
                controlTv("VoiceUp","1",mCommandType);
                break;
            case R.id.volume_delete_img:
                controlTv("VoiceDown","1",mCommandType);
                break;
            case R.id.channel_add_img:
                controlTv("ChannelUp","1",mCommandType);
                break;
            case R.id.channel_delete_img:
                controlTv("ChannelDown","1",mCommandType);
                break;
            case R.id.tv_home_img:
                controlTv("Main","1",mCommandType);
                break;
            case R.id.tv_back_img:
                controlTv("Back","1",mCommandType);
                break;



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
