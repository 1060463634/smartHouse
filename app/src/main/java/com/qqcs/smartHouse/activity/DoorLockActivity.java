package com.qqcs.smartHouse.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.MediaType;


public class DoorLockActivity extends BaseActivity{

    @BindView(R.id.control_label)
    RelativeLayout mDoorLockLayout;

    private String mDeviceId;
    private String mPropId;
    private String mTitleName;
    private List<PropBean> propBeans = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_door_lock);
        ButterKnife.bind(this);
        initView();

    }

    private void initView() {
        mDeviceId = getIntent().getStringExtra("deviceId");
        mPropId = getIntent().getStringExtra("propId");
        mTitleName = getIntent().getStringExtra("titleName");
        setTitleName(mTitleName);
        mDoorLockLayout.setOnClickListener(this);

        getDeviceProp();

    }

    private void openSwitch() {

        String accessToken = (String) SharePreferenceUtil.
                get(this, SP_Constants.ACCESS_TOKEN,"");

        String timestamp = System.currentTimeMillis() + "";
        JSONObject object = CommonUtil.getRequstJson(this);
        JSONObject dataObject = new JSONObject();

        try {
            dataObject.put("propId", mPropId);
            dataObject.put("propValue", "2");
            dataObject.put("timestamp", timestamp);

            object.put("data", dataObject);
            object.put("sign", MD5Utils.md5s(mPropId + "2" + timestamp));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        OkHttpUtils
                .postString()
                .tag(this)
                .url(Constants.HTTP_DEVICE_COMMAND)
                .addHeader("access-token",accessToken)
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .content(object.toString())
                .build()
                .execute(new MyStringCallback<Object>(this,
                        Object.class, true, false) {
                    @Override
                    public void onSuccess(Object data) {
                        ToastUtil.showToast(DoorLockActivity.this,"执行成功");
                    }

                    @Override
                    public void onFailure(String message) {
                        ToastUtil.showToast(DoorLockActivity.this, message);
                    }
                });
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
                            if(propBeans.get(i).getPropTypeCode()
                                    .equalsIgnoreCase("DoorOption")){
                                mPropId = propBeans.get(i).getPropId();
                                break;
                            }
                        }
                    }

                    @Override
                    public void onFailure(String message) {
                        ToastUtil.showToast(DoorLockActivity.this, message);
                    }
                });
    }


    @Override
    public void onMultiClick(View v) {
        Intent intent;
        switch (v.getId()) {

            case R.id.control_label:
                openSwitch();
                break;


        }
    }



}
