package com.qqcs.smartHouse.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.qqcs.smartHouse.R;
import com.qqcs.smartHouse.application.Constants;
import com.qqcs.smartHouse.application.SP_Constants;
import com.qqcs.smartHouse.fragment.HomeFragment;
import com.qqcs.smartHouse.models.EventBusBean;
import com.qqcs.smartHouse.models.PropBean;
import com.qqcs.smartHouse.network.CommonJsonList;
import com.qqcs.smartHouse.network.MyStringCallback;
import com.qqcs.smartHouse.utils.CommonUtil;
import com.qqcs.smartHouse.utils.MD5Utils;
import com.qqcs.smartHouse.utils.SharePreferenceUtil;
import com.qqcs.smartHouse.utils.ToastUtil;
import com.qqcs.smartHouse.widgets.MyHomesListPopupWindow;
import com.zhy.http.okhttp.OkHttpUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.MediaType;


public class OpenWindowActivity extends BaseActivity {

    @BindView(R.id.window_open_layout)
    LinearLayout mWindowOpenLayout;

    @BindView(R.id.window_stop_layout)
    LinearLayout mWindowStopLayout;

    @BindView(R.id.window_close_layout)
    LinearLayout mWindowCloseLayout;

    @BindView(R.id.window_open_img)
    ImageView mWindowOpenImg;

    @BindView(R.id.window_stop_img)
    ImageView mWindowStopImg;

    @BindView(R.id.window_close_img)
    ImageView mWindowCloseImg;

    @BindView(R.id.control_label)
    RelativeLayout mHeadLayout;

    private String mDeviceId;
    private String mPropId;
    private String mSubType;
    private String mTitleName;
    private List<PropBean> propBeans = new ArrayList<>();

    private String mOpenPID;
    private String mStopPID;
    private String mClosePID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_window);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        initView();

    }

    private void initView() {
        mDeviceId = getIntent().getStringExtra("deviceId");
        mPropId = getIntent().getStringExtra("propId");
        mSubType = getIntent().getStringExtra("subType");
        mTitleName = getIntent().getStringExtra("titleName");
        setTitleName(mTitleName);
        if(mSubType.equalsIgnoreCase("4")){
            mHeadLayout.setBackgroundResource(R.drawable.ic_bedroom_window);
        }

        mWindowOpenLayout.setOnClickListener(this);
        mWindowStopLayout.setOnClickListener(this);
        mWindowCloseLayout.setOnClickListener(this);

        getDeviceProp();

    }

    private void openSwitch(String pid) {

        String accessToken = (String) SharePreferenceUtil.
                get(this, SP_Constants.ACCESS_TOKEN, "");

        String timestamp = System.currentTimeMillis() + "";
        JSONObject object = CommonUtil.getRequstJson(this);
        JSONObject dataObject = new JSONObject();

        try {
            dataObject.put("propId", pid);
            dataObject.put("propValue", "1");
            dataObject.put("timestamp", timestamp);

            object.put("data", dataObject);
            object.put("sign", MD5Utils.md5s(pid + "1" + timestamp));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        OkHttpUtils
                .postString()
                .tag(this)
                .url(Constants.HTTP_DEVICE_COMMAND)
                .addHeader("access-token", accessToken)
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .content(object.toString())
                .build()
                .execute(new MyStringCallback<Object>(this,
                        Object.class, true, false) {
                    @Override
                    public void onSuccess(Object data) {
                        ToastUtil.showToast(OpenWindowActivity.this, "执行成功");
                    }

                    @Override
                    public void onFailure(String message) {
                        ToastUtil.showToast(OpenWindowActivity.this, message);
                    }
                });
    }

    private void getDeviceProp() {

        String accessToken = (String) SharePreferenceUtil.
                get(this, SP_Constants.ACCESS_TOKEN, "");

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
                .addHeader("access-token", accessToken)
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .content(object.toString())
                .build()
                .execute(new MyStringCallback<PropBean>(this,
                        PropBean.class, true, true) {

                    @Override
                    public void onSuccess(CommonJsonList<PropBean> json) {
                        propBeans = json.getData();
                        refreshGreenImg();
                    }

                    @Override
                    public void onFailure(String message) {
                        ToastUtil.showToast(OpenWindowActivity.this, message);
                    }
                });
    }

    private void refreshGreenImg() {
        mWindowOpenImg.setVisibility(View.INVISIBLE);
        mWindowStopImg.setVisibility(View.INVISIBLE);
        mWindowCloseImg.setVisibility(View.INVISIBLE);
        for (int i = 0; i < propBeans.size(); i++) {
            String index = propBeans.get(i).getPropIndex();
            String value = propBeans.get(i).getPropValue();

            if (index.equalsIgnoreCase("0")) {
                mOpenPID = propBeans.get(i).getPropId();

            } else if (index.equalsIgnoreCase("1")) {
                mStopPID = propBeans.get(i).getPropId();

            } else if (index.equalsIgnoreCase("2")) {
                mClosePID = propBeans.get(i).getPropId();
            }

            if (value.equalsIgnoreCase("1")) {
                if (index.equalsIgnoreCase("0")) {
                    mWindowOpenImg.setVisibility(View.VISIBLE);
                } else if (index.equalsIgnoreCase("1")) {
                    mWindowStopImg.setVisibility(View.VISIBLE);
                } else if (index.equalsIgnoreCase("2")) {
                    mWindowCloseImg.setVisibility(View.VISIBLE);
                }
            }
        }
    }


    @Override
    public void onMultiClick(View v) {
        Intent intent;
        switch (v.getId()) {

            case R.id.window_open_layout:
                openSwitch(mOpenPID);
                break;
            case R.id.window_stop_layout:
                openSwitch(mStopPID);
                break;
            case R.id.window_close_layout:
                openSwitch(mClosePID);
                break;

        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventBus(EventBusBean event){

        switch (event.getType()){


            case EventBusBean.REFRESH_PROPT:
                getDeviceProp();

                break;

            case EventBusBean.REFRESH_HOME_AND_PROPT:
                getDeviceProp();

                break;

        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
