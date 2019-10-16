package com.qqcs.smartHouse.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputFilter.LengthFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.qqcs.smartHouse.application.Constants;
import com.qqcs.smartHouse.application.SP_Constants;
import com.qqcs.smartHouse.models.EventBusBean;
import com.qqcs.smartHouse.network.MyStringCallback;
import com.qqcs.smartHouse.utils.CommonUtil;
import com.qqcs.smartHouse.utils.MD5Utils;
import com.qqcs.smartHouse.utils.SharePreferenceUtil;
import com.qqcs.smartHouse.utils.ToastUtil;
import com.videogo.EzvizApplication;
import com.videogo.RootActivity;
import com.videogo.constant.IntentConsts;
import com.videogo.device.PeripheralInfo;
import com.videogo.errorlayer.ErrorInfo;
import com.videogo.exception.BaseException;
import com.videogo.exception.ErrorCode;
import com.videogo.ui.util.ActivityUtils;
import com.videogo.util.ConnectionDetector;
import com.videogo.util.LogUtil;
import com.videogo.widget.TitleBar;
import com.videogo.widget.WaitDialog;
import com.zhy.http.okhttp.OkHttpUtils;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import ezviz.ezopensdk.R;
import okhttp3.MediaType;

//import com.videogo.restful.exception.VideoGoNetSDKException;
//import com.videogo.util.ActivityUtils;
//import com.videogo.widget.inputfilter.BytesLengthFilter;
//import com.videogo.widget.inputfilter.IllegalWordFilter;

public class ModifyDeviceNameActivity extends RootActivity implements OnClickListener {

    protected static final int MSG_UPDATA_DEVICE_NAME_FAIL = 1001;

    protected static final int MSG_UPDATA_DEVICE_NAME_SUCCESS = 1002;

    private final static int TYPE_DEVICE = 0x01;
    private final static int TYPE_CAMERA = 0x02;
    private final static int TYPE_DETECTOR = 0x04;

    private TitleBar mTitleBar;
    private EditText mNameText;
    private TextView mDetectorTypeView;
    private ImageButton mNameDelButton;
    private TextView mInputHintView;

    private ViewGroup mCommonNameLayout;
    private GridView mCommonNameGridView;

    private PeripheralInfo mDetector;

    private WaitDialog mWaitDialog;
    
    private String mDeviceNameString;

    private Handler mHandler;
    private int mType;
	private Button mSaveButton = null;

    private String mDeviceName;
    private String mDeviceSerial;

    private String mDeviceId;
    private String mPropId;
    private String mRoomId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.modify_device_name_page);

        findViews();
        initData();
        initTitleBar();
        initViews();
    }

    private void findViews() {
        mTitleBar = (TitleBar) findViewById(R.id.title_bar);
        mTitleBar.setBackButton(R.drawable.common_title_cancel_selector);
        mNameText = (EditText) findViewById(R.id.name_text);
        mNameDelButton = (ImageButton) findViewById(R.id.name_del);
        mDetectorTypeView = (TextView) findViewById(R.id.detector_type);
        mInputHintView = (TextView) findViewById(R.id.input_hint);
        mCommonNameLayout = (ViewGroup) findViewById(R.id.common_name_layout);
        mSaveButton = (Button) findViewById(R.id.btn_id_save_name);
    }

    private void initData() {
        mHandler = new MyHandler();

        mDeviceId = getIntent().getStringExtra("deviceId");
        mPropId = getIntent().getStringExtra("propId");
        mRoomId = getIntent().getStringExtra("roomId");

        if (getIntent().hasExtra(IntentConsts.EXTRA_NAME)) {
            mDeviceName = getIntent().getStringExtra(IntentConsts.EXTRA_NAME);

            mType = TYPE_DEVICE;

            mInputHintView.setText(getString(R.string.detail_modify_device_name_limit_tip, 50));
            mNameText.setFilters(new InputFilter[] {
                    new LengthFilter(50)});

        }
        if (getIntent().hasExtra(IntentConsts.EXTRA_DEVICE_ID)){
            mDeviceSerial = getIntent().getStringExtra(IntentConsts.EXTRA_DEVICE_ID);
        }

        mWaitDialog = new WaitDialog(ModifyDeviceNameActivity.this, android.R.style.Theme_Translucent_NoTitleBar);
        mWaitDialog.setCancelable(false);
    }

    private void initTitleBar() {
        mTitleBar.setTitle(R.string.ez_modify_name);
        mTitleBar.addBackButton(new OnClickListener() {

            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
//        mTitleBar.addRightButton(R.drawable.common_title_confirm_selector, new OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                modifyDeviceName();
//            }
//        });
    }

    private void initViews() {
        mNameText.setText(TextUtils.isEmpty(mDeviceName)?"":mDeviceName);
        mNameText.setSelection(mNameText.getText().length());
        mNameText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String str = s.toString();
                setSelectLabel(str);
            }
        });
        mNameText.setOnEditorActionListener(new OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    modifyDeviceName();
                    return true;
                }
                return false;
            }
        });

        mNameDelButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                mNameText.setText(null);
            }
        });

        if (mType == TYPE_DETECTOR) {} else {
            mCommonNameLayout.setVisibility(View.GONE);
        }
        mSaveButton.setOnClickListener(this);
    }

	@Override
	public void onClick(View view) {

        if(view.getId() == R.id.btn_id_save_name){
            modifyDeviceName();
            saveDeviceInfo(mNameText.getText().toString().trim(),mRoomId);
        }

	}

    private void saveDeviceInfo(final String name,String rooId) {

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
                        ToastUtil.showToast(ModifyDeviceNameActivity.this, "修改成功！");
                        Intent intent = new Intent();
                        intent.putExtra(IntentConsts.EXTRA_NAME, name);
                        setResult(RESULT_OK, intent);
                        finish();
                    }

                    @Override
                    public void onFailure(String message) {
                        ToastUtil.showToast(ModifyDeviceNameActivity.this, message);
                    }
                });
    }

    private void setSelectLabel(String input) {}

    private void modifyDeviceName() {
    	if(TextUtils.isEmpty(mDeviceName)) {
    		return;
    	}
        mDeviceNameString = mNameText.getText().toString().trim();

        if (TextUtils.isEmpty(mDeviceNameString)) {
            showToast(R.string.company_addr_is_empty);
            return;
        }

        // 本地网络检测
        if (!ConnectionDetector.isNetworkAvailable(ModifyDeviceNameActivity.this)) {
            showToast(R.string.offline_warn_text);
            return;
        }

        //mWaitDialog.show();

        new Thread() {
            @Override
            public void run() {
                int errorCode = 0;

                try {
                    EzvizApplication.getOpenSDK().setDeviceName(mDeviceSerial, mDeviceNameString);
                } catch (BaseException e) {
                	e.printStackTrace();

                    ErrorInfo errorInfo = (ErrorInfo) e.getObject();
                    LogUtil.debugLog("TAG", errorInfo.toString());
                }

                if (errorCode != 0) {
                    //mHandler.obtainMessage(MSG_UPDATA_DEVICE_NAME_FAIL, errorCode, 0).sendToTarget();
                } else {
                    //mHandler.obtainMessage(MSG_UPDATA_DEVICE_NAME_SUCCESS).sendToTarget();
                }
            }
        }.start();
    }

    class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_UPDATA_DEVICE_NAME_FAIL:
                    handleUpdateFail(msg.arg1);
                    break;
                case MSG_UPDATA_DEVICE_NAME_SUCCESS:
                    handleUpdateSuccess();
                    break;
                default:
                    break;
            }
        }
    }

    private void handleUpdateFail(int errorCode) {
        mWaitDialog.dismiss();
        switch (errorCode) {
            case ErrorCode.ERROR_TRANSF_DEVICE_OFFLINE:
                showToast(R.string.camera_not_online);
                break;
            case ErrorCode.ERROR_WEB_SESSION_ERROR:
                ActivityUtils.handleSessionException(ModifyDeviceNameActivity.this);
                break;
            case ErrorCode.ERROR_WEB_HARDWARE_SIGNATURE_ERROR:
                ActivityUtils.handleSessionException(ModifyDeviceNameActivity.this);
                break;
            default:
                // 修改失败，提示失败的消息
                showToast(R.string.detail_modify_fail, errorCode);
                break;
        }
    }

    private void handleUpdateSuccess() {
        mWaitDialog.dismiss();
        //showToast(R.string.detail_modify_success);
        //Intent intent = new Intent();
        //intent.putExtra(IntentConsts.EXTRA_NAME, mDeviceNameString);
        //setResult(RESULT_OK, intent);
        //finish();
    }

    private void hideSoftInput() {
        if (getCurrentFocus() != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, R.anim.fade_down);
    }
}