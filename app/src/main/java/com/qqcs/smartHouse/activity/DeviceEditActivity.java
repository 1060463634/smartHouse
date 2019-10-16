package com.qqcs.smartHouse.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.qqcs.smartHouse.R;
import com.qqcs.smartHouse.adapter.RoomManageAdapter;
import com.qqcs.smartHouse.adapter.RoomSpinnerAdapter;
import com.qqcs.smartHouse.application.Constants;
import com.qqcs.smartHouse.application.SP_Constants;
import com.qqcs.smartHouse.models.EventBusBean;
import com.qqcs.smartHouse.models.RoomBean;
import com.qqcs.smartHouse.models.RoomListBean;
import com.qqcs.smartHouse.network.MyStringCallback;
import com.qqcs.smartHouse.utils.CommonUtil;
import com.qqcs.smartHouse.utils.MD5Utils;
import com.qqcs.smartHouse.utils.SharePreferenceUtil;
import com.qqcs.smartHouse.utils.ToastUtil;
import com.qqcs.smartHouse.widgets.MyAlertDialog;
import com.zhy.http.okhttp.OkHttpUtils;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.MediaType;


public class DeviceEditActivity extends BaseActivity{


    @BindView(R.id.device_name_edt)
    EditText mDeviceNameEdt;

    @BindView(R.id.save_tv)
    TextView mSaveBtn;

    @BindView(R.id.room_spinner)
    Spinner mRoomSpinner;

    private String mDeviceId;
    private String mPropId;
    private String mDeviceName;
    private String mRoomId;
    private ArrayList<RoomBean> mRoomInfos = new ArrayList<>();
    private RoomSpinnerAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_edit);
        ButterKnife.bind(this);
        setTitleName("编辑设备");
        initView();

    }

    private void initView() {
        Intent intent = getIntent();
        mDeviceId = intent.getStringExtra("deviceId");
        mPropId = intent.getStringExtra("propId");
        mDeviceName = intent.getStringExtra("deviceName");
        mRoomId = intent.getStringExtra("roomId");

        mDeviceNameEdt.setText(mDeviceName);
        mDeviceNameEdt.setSelection(mDeviceNameEdt.getText().length());
        mSaveBtn.setOnClickListener(this);
        getRoomList();

    }

    private void getRoomList() {
        String accessToken = (String) SharePreferenceUtil.
                get(this, SP_Constants.ACCESS_TOKEN,"");

        String familyId = (String) SharePreferenceUtil.
                get(this, SP_Constants.CURRENT_FAMILY_ID,"");

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
                .url(Constants.HTTP_GET_ROOM_LIST)
                .addHeader("access-token",accessToken)
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .content(object.toString())
                .build()
                .execute(new MyStringCallback<RoomListBean>(this, RoomListBean.class, false, false) {

                    @Override
                    public void onSuccess(RoomListBean json) {
                        mRoomInfos.clear();
                        mRoomInfos.addAll(json.getRooms());
                        setMyAdapter();

                        for(int i = 0;i < mRoomInfos.size();i++){
                            if(mRoomInfos.get(i).getRoomId().equalsIgnoreCase(mRoomId)){
                                mRoomSpinner.setSelection(i);
                                break;
                            }
                        }
                    }

                    @Override
                    public void onFailure(String message) {
                        ToastUtil.showToast(DeviceEditActivity.this, message);
                    }
                });
    }

    private void saveDeviceInfo(String name,String rooId) {

        if(TextUtils.isEmpty(name)){
            ToastUtil.showToast(this, "请输入设备名称");
            return;
        }

        String accessToken = (String) SharePreferenceUtil.
                get(this, SP_Constants.ACCESS_TOKEN,"");

        String timestamp = System.currentTimeMillis() + "";
        JSONObject object = CommonUtil.getRequstJson(getApplicationContext());
        JSONObject dataObject = new JSONObject();

        try {
            dataObject.put("deviceId", mDeviceId);
            dataObject.put("name", name);
            dataObject.put("roomId", rooId);
            dataObject.put("propId", mPropId);
            dataObject.put("timestamp", timestamp);

            object.put("data", dataObject);
            object.put("sign", MD5Utils.md5s(
                    mDeviceId + name + rooId + mPropId + timestamp));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        OkHttpUtils
                .postString()
                .tag(this)
                .addHeader("access-token",accessToken)
                .url(Constants.HTTP_DEVICE_UPDATE)
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .content(object.toString())
                .build()
                .execute(new MyStringCallback<Object>(this, Object.class, true, false) {

                    @Override
                    public void onSuccess(Object json) {
                        EventBus.getDefault().post(new EventBusBean(EventBusBean.REFRESH_HOME));
                        ToastUtil.showToast(DeviceEditActivity.this, "修改成功！");
                        finish();
                    }

                    @Override
                    public void onFailure(String message) {
                        ToastUtil.showToast(DeviceEditActivity.this, message);
                    }
                });
    }

    public void setMyAdapter() {
        if (mAdapter == null) {
            mAdapter = new RoomSpinnerAdapter(this, mRoomInfos);
            mRoomSpinner.setAdapter(mAdapter);
        } else {
            mAdapter.refreshData(mRoomInfos);
        }

    }


    @Override
    public void onMultiClick(View v) {
        Intent intent;
        switch (v.getId()) {

            case R.id.save_tv:
                saveDeviceInfo(mDeviceNameEdt.getText().toString(),
                        mRoomInfos.get(mRoomSpinner.getSelectedItemPosition()).getRoomId());

                break;

        }
    }



}
