package com.qqcs.smartHouse.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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
import com.qqcs.smartHouse.widgets.SpeedPopupWindow;
import com.qqcs.smartHouse.widgets.WeiboDialogUtils;
import com.zhy.http.okhttp.OkHttpUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.MediaType;
import okhttp3.Request;


public class AirConditionControlActivity extends BaseActivity {

    @BindView(R.id.temp_num_tv)
    TextView mTempNumTv;
    @BindView(R.id.tv_left_img)
    ImageView mTvLeftImg;
    @BindView(R.id.tv_right_img)
    ImageView mTvRightImg;


    @BindView(R.id.cold_cbx)
    ImageView mColdCbx;
    @BindView(R.id.water_cbx)
    ImageView mWaterCbx;
    @BindView(R.id.hot_cbx)
    ImageView mHotCbx;
    @BindView(R.id.wind_lr_cbx)
    ImageView mLeftRightCbx;
    @BindView(R.id.switch_cbx)
    ImageView mSwitchCbx;
    @BindView(R.id.auto_cbx)
    ImageView mAutoCbx;
    @BindView(R.id.speed_cbx)
    ImageView mSpeedCbx;
    @BindView(R.id.wind_ud_cbx)
    ImageView mUpDownCbx;

    @BindView(R.id.switch_layout)
    LinearLayout mSwitchLayout;
    @BindView(R.id.cold_layout)
    LinearLayout mColdLayout;
    @BindView(R.id.water_layout)
    LinearLayout mWaterLayout;
    @BindView(R.id.left_right_layout)
    LinearLayout mLeftRightLayout;
    @BindView(R.id.auto_layout)
    LinearLayout mAutoLayout;
    @BindView(R.id.speed_layout)
    LinearLayout mSpeedLayout;
    @BindView(R.id.up_down_layout)
    LinearLayout mUpdownLayout;
    @BindView(R.id.hot_layout)
    LinearLayout mHotLayout;

    @BindView(R.id.speed_tv)
    TextView mSpeedTv;


    private String mDeviceId;
    private String mPropId;
    private String mCommandType;
    private String mTitleName;
    private List<PropBean> propBeans = new ArrayList<>();
    private SpeedPopupWindow mSpeedPopwindow;
    private Dialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_air_control);
        ButterKnife.bind(this);

        mDeviceId = getIntent().getStringExtra("deviceId");
        mPropId = getIntent().getStringExtra("propId");
        mCommandType = getIntent().getStringExtra("commandType");
        mTitleName = getIntent().getStringExtra("titleName");

        if(mCommandType.equalsIgnoreCase("F1")){
            setTitleName(mTitleName +" - 学习模式");
        }else {
            setTitleName(mTitleName);
        }

        EventBus.getDefault().register(this);
        initView();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);

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

    private void initView() {

        mTvLeftImg.setOnClickListener(this);
        mTvRightImg.setOnClickListener(this);
        mSwitchLayout.setOnClickListener(this);
        mColdLayout.setOnClickListener(this);
        mHotLayout.setOnClickListener(this);
        mAutoLayout.setOnClickListener(this);
        mWaterLayout.setOnClickListener(this);
        mLeftRightLayout.setOnClickListener(this);
        mUpdownLayout.setOnClickListener(this);
        mSpeedLayout.setOnClickListener(this);
        mSpeedPopwindow = new SpeedPopupWindow(this, this, mSpeedLayout);

        getDeviceProp();

    }


    private void controlAC(String propTypeCode, String propValue, String commandType) {

        String accessToken = (String) SharePreferenceUtil.
                get(this, SP_Constants.ACCESS_TOKEN, "");

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
                .addHeader("access-token", accessToken)
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .content(object.toString())
                .build()
                .execute(new MyStringCallback<Object>(this,
                        Object.class, false, false) {

                    @Override
                    public void onBefore(Request request, int id) {
                        if (mDialog == null) {
                            mDialog = WeiboDialogUtils.createLoadingDialog(
                                    AirConditionControlActivity.this, "加载中...");
                        }else{
                            mDialog.show();
                        }

                        final Timer t = new Timer();
                        t.schedule(new TimerTask() {
                            public void run() {
                                if (mDialog != null) {
                                    mDialog.dismiss();
                                }
                                t.cancel();
                            }
                        }, 1300);
                    }

                    @Override
                    public void onAfter(int id) {
                    }

                    @Override
                    public void onSuccess(Object data) {
                        //ToastUtil.showToast(AirConditionControlActivity.this, "执行成功,等待响应");
                    }


                    @Override
                    public void onFailure(String message) {
                        ToastUtil.showToast(AirConditionControlActivity.this, message);
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
                        PropBean.class, false, true) {

                    @Override
                    public void onSuccess(CommonJsonList<PropBean> json) {
                        propBeans = json.getData();
                        setViewByProp();
                    }


                    @Override
                    public void onFailure(String message) {
                        ToastUtil.showToast(AirConditionControlActivity.this, message);
                    }
                });
    }


    private void setViewByProp() {

        for (int i = 0; i < propBeans.size(); i++) {
            String type = propBeans.get(i).getPropTypeCode();
            String value = propBeans.get(i).getPropValue();

            switch (type) {

                case "Temperature":
                    mTempNumTv.setText(value);
                    break;
                case "Mode":
                    setAirMode(value);
                    break;
                case "Power":
                    mSwitchCbx.setEnabled(value.equalsIgnoreCase("1"));
                    mSpeedCbx.setEnabled(value.equalsIgnoreCase("1"));
                    break;
                case "WindSpeed":
                    setSpeed(value,false);
                    break;
                case "LateralSwinging":
                    mLeftRightCbx.setEnabled(value.equalsIgnoreCase("1"));
                    break;
                case "LongitudinalSwinging":
                    mUpDownCbx.setEnabled(value.equalsIgnoreCase("1"));
                    break;

            }
        }

    }

    private String getValueByType(String type) {

        String result = "";
        for (int i = 0; i < propBeans.size(); i++) {
            String c_type = propBeans.get(i).getPropTypeCode();
            if (c_type.equalsIgnoreCase(type)) {
                result = propBeans.get(i).getPropValue();
                break;
            }
        }

        return result;
    }

    private void setAirMode(String mode) {
        mColdCbx.setEnabled(false);
        mHotCbx.setEnabled(false);
        mWaterCbx.setEnabled(false);
        mAutoCbx.setEnabled(false);


        switch (mode) {
            //制冷
            case "1":
                mColdCbx.setEnabled(true);
                break;
            //制热
            case "2":
                mHotCbx.setEnabled(true);
                break;
            //除湿
            case "3":
                mWaterCbx.setEnabled(true);
                break;
            //自动
            case "4":
                mAutoCbx.setEnabled(true);
                break;

        }

    }


    @Override
    public void onMultiClick(View v) {
        Intent intent;
        String value;
        switch (v.getId()) {

            case R.id.tv_left_img:
                value = getValueByType("Temperature");
                try {
                    int temp = Integer.parseInt(value);
                    int temp2 = temp - 1;
                    controlAC("Temperature", temp2 + "", mCommandType);
                } catch (Exception e) {
                    ToastUtil.showToast(this, "温度值错误");
                }

                break;

            case R.id.tv_right_img:
                value = getValueByType("Temperature");
                try {
                    int temp = Integer.parseInt(value);
                    int temp2 = temp + 1;
                    controlAC("Temperature", temp2 + "", mCommandType);
                } catch (Exception e) {
                    ToastUtil.showToast(this, "温度值错误");
                }

                break;

            case R.id.switch_layout:
                value = getValueByType("Power");
                if (value.equalsIgnoreCase("1")) {
                    controlAC("Power", "0", mCommandType);
                } else {
                    controlAC("Power", "1", mCommandType);
                }

                break;

            case R.id.left_right_layout:
                value = getValueByType("LateralSwinging");
                if (value.equalsIgnoreCase("1")) {
                    controlAC("LateralSwinging", "0", mCommandType);
                } else {
                    controlAC("LateralSwinging", "1", mCommandType);
                }

                break;
            case R.id.up_down_layout:
                value = getValueByType("LongitudinalSwinging");
                if (value.equalsIgnoreCase("1")) {
                    controlAC("LongitudinalSwinging", "0", mCommandType);
                } else {
                    controlAC("LongitudinalSwinging", "1", mCommandType);
                }

                break;

            case R.id.cold_layout:
                controlAC("Mode", "1", mCommandType);
                break;
            case R.id.hot_layout:
                controlAC("Mode", "2", mCommandType);
                break;
            case R.id.water_layout:
                controlAC("Mode", "3", mCommandType);
                break;
            case R.id.auto_layout:
                controlAC("Mode", "4", mCommandType);
                break;


            case R.id.speed_layout:
                mSpeedPopwindow.show();
                break;


        }
    }



    public void setSpeed(String speedValue,boolean sendValue) {

        if(sendValue){
            controlAC("WindSpeed", speedValue, mCommandType);

        }else {

            switch (speedValue) {

                case "1":
                    mSpeedTv.setText("风速-强风");
                    break;
                case "2":
                    mSpeedTv.setText("风速-弱风");
                    break;
                case "3":
                    mSpeedTv.setText("风速-微风");
                    break;
                case "4":
                    mSpeedTv.setText("风速-自动");
                    break;
            }

        }



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode != RESULT_OK) {
            return;
        }


        switch (requestCode) {

        }

    }


}
