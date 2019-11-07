package com.qqcs.smartHouse.activity;

import android.app.Dialog;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.inuker.bluetooth.library.connect.listener.BleConnectStatusListener;
import com.inuker.bluetooth.library.connect.listener.BluetoothStateListener;
import com.inuker.bluetooth.library.connect.options.BleConnectOptions;
import com.inuker.bluetooth.library.connect.response.BleConnectResponse;
import com.inuker.bluetooth.library.connect.response.BleNotifyResponse;
import com.inuker.bluetooth.library.connect.response.BleReadResponse;
import com.inuker.bluetooth.library.connect.response.BleWriteResponse;
import com.inuker.bluetooth.library.model.BleGattCharacter;
import com.inuker.bluetooth.library.model.BleGattProfile;
import com.inuker.bluetooth.library.model.BleGattService;
import com.inuker.bluetooth.library.search.SearchRequest;
import com.inuker.bluetooth.library.search.SearchResult;
import com.inuker.bluetooth.library.search.response.SearchResponse;
import com.inuker.bluetooth.library.utils.BluetoothLog;
import com.inuker.bluetooth.library.utils.BluetoothUtils;
import com.inuker.bluetooth.library.utils.ByteUtils;
import com.qqcs.smartHouse.R;
import com.qqcs.smartHouse.adapter.BlueDeviceListAdapter;
import com.qqcs.smartHouse.application.Constants;
import com.qqcs.smartHouse.application.SP_Constants;
import com.qqcs.smartHouse.bluetooth.ClientManager;
import com.qqcs.smartHouse.bluetooth.DetailItem;
import com.qqcs.smartHouse.models.GatewayInfoBean;
import com.qqcs.smartHouse.models.TrunpleInfoBean;
import com.qqcs.smartHouse.network.MyStringCallback;
import com.qqcs.smartHouse.utils.CommonUtil;
import com.qqcs.smartHouse.utils.DateUtil;
import com.qqcs.smartHouse.utils.LogUtil;
import com.qqcs.smartHouse.utils.MD5Utils;
import com.qqcs.smartHouse.utils.NetworkUtil;
import com.qqcs.smartHouse.utils.SharePreferenceUtil;
import com.qqcs.smartHouse.utils.ToastUtil;
import com.qqcs.smartHouse.widgets.WeiboDialogUtils;
import com.zhy.http.okhttp.OkHttpUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.header.MaterialHeader;
import okhttp3.MediaType;
import okhttp3.internal.Util;

import static com.inuker.bluetooth.library.Constants.REQUEST_SUCCESS;
import static com.inuker.bluetooth.library.Constants.STATUS_CONNECTED;


public class BlueToothDetailActivity extends BaseActivity {


    @BindView(R.id.wifi_name_edt)
    EditText mNameEdit;

    @BindView(R.id.wifi_psw_edt)
    EditText mPswEdit;

    @BindView(R.id.ok_btn)
    Button mOkBtn;

    private SearchResult mResult;
    private BluetoothDevice mDevice;
    private boolean mConnected;
    private String mDeviceName = "";
    private String mProductKey = "";
    private String mDeviceSecret = "";
    private String mFamilyId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blue_tooth_detail);
        ButterKnife.bind(this);
        setTitleName("输入Wi-Fi信息");
        initView();

    }


    private void initView() {
        String wifiAccount = NetworkUtil.getConnectWifiSsid(this);
        mNameEdit.setText(wifiAccount);
        mOkBtn.setOnClickListener(this);

        Intent intent = getIntent();
        String mac = intent.getStringExtra("mac");
        mFamilyId = getIntent().getStringExtra("familyId");
        mResult = intent.getParcelableExtra("device");
        mDevice = BluetoothUtils.getRemoteDevice(mac);

        ClientManager.getClient().registerConnectStatusListener(mDevice.getAddress(), mConnectStatusListener);

        connectDeviceIfNeeded();

    }


    private final BleConnectStatusListener mConnectStatusListener = new BleConnectStatusListener() {
        @Override
        public void onConnectStatusChanged(String mac, int status) {
            BluetoothLog.v(String.format("DeviceDetailActivity onConnectStatusChanged %d in %s",
                    status, Thread.currentThread().getName()));
            if (status == STATUS_CONNECTED) {
                ToastUtil.showToast(BlueToothDetailActivity.this, "已连接");
            } else {
                ToastUtil.showToast(BlueToothDetailActivity.this, "连接已断开");
            }

            mConnected = (status == STATUS_CONNECTED);
            connectDeviceIfNeeded();
        }
    };

    private void connectDevice() {
        showLoadingDialog();

        BleConnectOptions options = new BleConnectOptions.Builder()
                .setConnectRetry(3)
                .setConnectTimeout(20000)
                .setServiceDiscoverRetry(3)
                .setServiceDiscoverTimeout(10000)
                .build();

        ClientManager.getClient().connect(mDevice.getAddress(), options, new BleConnectResponse() {
            @Override
            public void onResponse(int code, BleGattProfile profile) {
                BluetoothLog.v(String.format("profile:\n%s", profile));
                dismissLoadingDialog();

                if (code == REQUEST_SUCCESS) {
                    setGattProfile(profile);
                }
            }
        });
    }


    public void setGattProfile(BleGattProfile profile) {
        List<DetailItem> items = new ArrayList<DetailItem>();

        List<BleGattService> services = profile.getServices();

        for (BleGattService service : services) {
            items.add(new DetailItem(DetailItem.TYPE_SERVICE, service.getUUID(), null));
            List<BleGattCharacter> characters = service.getCharacters();
            for (BleGattCharacter character : characters) {
                items.add(new DetailItem(DetailItem.TYPE_CHARACTER, character.getUuid(), service.getUUID()));
            }
        }

        for (DetailItem item : items) {
            if (item.type == DetailItem.TYPE_CHARACTER) {
                if (item.uuid.toString().toUpperCase().contains("C4B2")) {
                    ClientManager.getClient().notify(mDevice.getAddress(),
                            item.service, item.uuid, mNotifyRsp);


                }
                if (item.uuid.toString().toUpperCase().contains("ACE7")) {
                    mServiceId = item.service;
                    mUuid = item.uuid;
                    //getDeviceUuid();
                }

            }
        }

    }

    private UUID mServiceId;
    private UUID mUuid;


    private void connectDeviceIfNeeded() {
        if (!mConnected) {
            connectDevice();
        }
    }

    @Override
    protected void onDestroy() {
        ClientManager.getClient().disconnect(mDevice.getAddress());
        ClientManager.getClient().unregisterConnectStatusListener(mDevice.getAddress(), mConnectStatusListener);
        super.onDestroy();
    }

    private Dialog mDialog;

    protected void showLoadingDialog() {
        if (mDialog == null) {
            mDialog = WeiboDialogUtils.createLoadingDialog(this, "连接中...");
        } else {
            mDialog.show();
        }

    }

    protected void dismissLoadingDialog() {
        WeiboDialogUtils.closeDialog(mDialog);

    }

    private void getDeviceUuid() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                String mid = CommonUtil.getUUID(BlueToothDetailActivity.this).toUpperCase();
                String muuid = mid.replace("-", "");
                String randomHex = CommonUtil.getRandomStr(32);
                String timeHex = CommonUtil.getTimeHex();

                String dataHex = muuid + CommonUtil.get00Str(16)
                        + randomHex + timeHex + "01" + CommonUtil.get00Str(29);

                String prefix = "EF01" + "0003" + "0082" + "0012" + "0100" + muuid + "0010"
                        + dataHex;

                String postfix = CommonUtil.getHexSum(prefix);

                String fix = prefix + postfix;

                mNofifyInfo = new StringBuffer();
                for (int i = 0; i < fix.length() / 20; i++) {
                    String sub = fix.substring(i * 20, i * 20 + 20);
                    ClientManager.getClient().write(mDevice.getAddress(), mServiceId, mUuid,
                            ByteUtils.stringToBytes(sub), mWriteRsp);
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }

            }
        }).start();


    }

    private void sendWifiInfo() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                String accountHex = CommonUtil.str2HexStr(mNameEdit.getText().toString());
                String pswHex = CommonUtil.str2HexStr(mPswEdit.getText().toString());
                String dataHex = accountHex + pswHex + CommonUtil.get00Str(36);

                String mid = CommonUtil.getUUID(BlueToothDetailActivity.this).toUpperCase();
                String muuid = mid.replace("-", "");

                String prefix = "EF01" + "0002" + "0082" + "0012" + "0100" + muuid + "0010"
                        + dataHex;
                String postfix = CommonUtil.getHexSum(prefix);

                String fix = prefix + postfix;

                for (int i = 0; i < fix.length() / 20; i++) {
                    String sub = fix.substring(i * 20, i * 20 + 20);
                    ClientManager.getClient().write(mDevice.getAddress(), mServiceId, mUuid,
                            ByteUtils.stringToBytes(sub), mWriteRsp);
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                sendThreeInfo();

            }
        }).start();


    }

    private void sendThreeInfo() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                String deviceNameHex = CommonUtil.str2HexStr(mDeviceName);
                String productKeyHex = CommonUtil.str2HexStr(mProductKey);
                String deviceSecretHex = CommonUtil.str2HexStr(mDeviceSecret);

                String dataHex = productKeyHex + deviceNameHex + deviceSecretHex
                        + CommonUtil.get00Str(4);

                String mid = CommonUtil.getUUID(BlueToothDetailActivity.this).toUpperCase();
                String muuid = mid.replace("-", "");

                String prefix = "EF01" + "0001" + "0082" + "0012" + "0100" + muuid + "0010"
                        + dataHex;
                String postfix = CommonUtil.getHexSum(prefix);

                String fix = prefix + postfix;

                for (int i = 0; i < fix.length() / 20; i++) {
                    String sub = fix.substring(i * 20, i * 20 + 20);
                    ClientManager.getClient().write(mDevice.getAddress(), mServiceId, mUuid,
                            ByteUtils.stringToBytes(sub), mWriteRsp);
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                rebootGateWay();

            }
        }).start();


    }

    private void rebootGateWay() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                String mid = CommonUtil.getUUID(BlueToothDetailActivity.this).toUpperCase();
                String muuid = mid.replace("-", "");
                String randomHex = CommonUtil.getRandomStr(32);
                String timeHex = CommonUtil.getTimeHex();

                String dataHex = muuid + CommonUtil.get00Str(16)
                        + randomHex + timeHex + "02" + CommonUtil.get00Str(29);

                String prefix = "EF01" + "0003" + "0082" + "0012" + "0100" + muuid + "0010"
                        + dataHex;

                String postfix = CommonUtil.getHexSum(prefix);

                String fix = prefix + postfix;

                mNofifyInfo = new StringBuffer();
                for (int i = 0; i < fix.length() / 20; i++) {
                    String sub = fix.substring(i * 20, i * 20 + 20);
                    ClientManager.getClient().write(mDevice.getAddress(), mServiceId, mUuid,
                            ByteUtils.stringToBytes(sub), mWriteRsp);
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                mHandler.sendEmptyMessage(1);


            }
        }).start();


    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            dismissLoadingDialog();
            ToastUtil.showToast(BlueToothDetailActivity.this, "成功");
            BondGateway(mGateWayUid, mDeviceName);
        }
    };


    @Override
    public void onMultiClick(View v) {
        switch (v.getId()) {

            case R.id.ok_btn:

                if (TextUtils.isEmpty(mNameEdit.getText().toString())) {
                    ToastUtil.showToast(this, "请输入WiFi账号");
                    return;
                }
                if (TextUtils.isEmpty(mPswEdit.getText().toString())) {
                    ToastUtil.showToast(this, "请输入WiFi密码");
                    return;
                }
                showLoadingDialog();
                getDeviceUuid();

                break;

        }
    }

    private final BleWriteResponse mWriteRsp = new BleWriteResponse() {
        @Override
        public void onResponse(int code) {
            if (code == REQUEST_SUCCESS) {
                //ToastUtil.showToast(BlueToothDetailActivity.this,"写入成功");
            } else {
                //ToastUtil.showToast(BlueToothDetailActivity.this,"写入失败");
            }
        }
    };


    private StringBuffer mNofifyInfo = new StringBuffer();
    private String mGateWayUid = "";
    private final BleNotifyResponse mNotifyRsp = new BleNotifyResponse() {
        @Override
        public void onNotify(UUID service, UUID character, byte[] value) {
            LogUtil.d("onNotify:" + ByteUtils.byteToString(value));

            mNofifyInfo.append(ByteUtils.byteToString(value));
            if (mNofifyInfo.length() >= 130) {
                mGateWayUid = mNofifyInfo.substring(20, 52);
                LogUtil.d(mGateWayUid);
                registerGateway(mGateWayUid, GatewayManageActivity.mDeviceName);

            }

        }

        @Override
        public void onResponse(int code) {
            if (code == REQUEST_SUCCESS) {
//                mBtnNotify.setEnabled(false);
//                mBtnUnnotify.setEnabled(true);
//                CommonUtils.toast("success");
            } else {
//                CommonUtils.toast("failed");
            }
        }
    };


    boolean hasRegisterGateway = false;

    private void registerGateway(String uuid, String deviceName) {
        if (hasRegisterGateway) {
            return;
        }
        hasRegisterGateway = true;
        String accessToken = (String) SharePreferenceUtil.
                get(this, SP_Constants.ACCESS_TOKEN, "");


        String timestamp = System.currentTimeMillis() + "";
        JSONObject object = CommonUtil.getRequstJson(this);
        JSONObject dataObject = new JSONObject();

        try {
            dataObject.put("uuid", uuid);
            dataObject.put("familyId", mFamilyId);
            dataObject.put("deviceName", deviceName);
            dataObject.put("timestamp", timestamp);

            object.put("data", dataObject);
            object.put("sign", MD5Utils.md5s(uuid
                    + mFamilyId + deviceName + timestamp));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        OkHttpUtils
                .postString()
                .tag(this)
                .url(Constants.HTTP_GATEWAY_REGISTER)
                .addHeader("access-token", accessToken)
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .content(object.toString())
                .build()
                .execute(new MyStringCallback<TrunpleInfoBean>(this,
                        TrunpleInfoBean.class, false, false) {
                    @Override
                    public void onSuccess(TrunpleInfoBean data) {
                        mDeviceName = data.getDeviceName();
                        mProductKey = data.getProductKey();
                        mDeviceSecret = data.getDeviceSecret();
                        sendWifiInfo();
                    }

                    @Override
                    public void onFailure(String message) {
                        dismissLoadingDialog();
                        ToastUtil.showToast(BlueToothDetailActivity.this, message);
                    }
                });
    }

    private void BondGateway(String uuid, String deviceName) {
        String accessToken = (String) SharePreferenceUtil.
                get(this, SP_Constants.ACCESS_TOKEN, "");


        String timestamp = System.currentTimeMillis() + "";
        JSONObject object = CommonUtil.getRequstJson(this);
        JSONObject dataObject = new JSONObject();

        try {
            dataObject.put("uuid", uuid);
            dataObject.put("familyId", mFamilyId);
            dataObject.put("deviceName", deviceName);
            dataObject.put("timestamp", timestamp);

            object.put("data", dataObject);
            object.put("sign", MD5Utils.md5s(uuid
                    + mFamilyId + deviceName + timestamp));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        OkHttpUtils
                .postString()
                .tag(this)
                .url(Constants.HTTP_GATEWAY_BOND)
                .addHeader("access-token", accessToken)
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .content(object.toString())
                .build()
                .execute(new MyStringCallback<TrunpleInfoBean>(this,
                        TrunpleInfoBean.class, false, false) {
                    @Override
                    public void onSuccess(TrunpleInfoBean data) {
                        Intent intent = new Intent(BlueToothDetailActivity.this,
                                GatewayManageActivity.class);
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onFailure(String message) {
                        ToastUtil.showToast(BlueToothDetailActivity.this, message);
                    }
                });
    }
}
