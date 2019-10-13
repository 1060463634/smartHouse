package com.videogo.ui.devicelist;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.videogo.RootActivity;
import com.videogo.errorlayer.ErrorInfo;
import com.videogo.exception.BaseException;
import com.videogo.openapi.EZOpenSDK;
import com.videogo.openapi.bean.EZProbeDeviceInfoResult;
import com.videogo.ui.cameralist.EZCameraListActivity;
import com.videogo.util.LogUtil;
import com.videogo.wificonfig.APWifiConfig;
import com.videogo.wificonfig.ConfigWifiErrorEnum;

import ezviz.ezopensdk.R;

import static com.videogo.ui.devicelist.AutoWifiNetConfigActivity.WIFI_PASSWORD;
import static com.videogo.ui.devicelist.AutoWifiNetConfigActivity.WIFI_SSID;
import static com.videogo.ui.devicelist.SeriesNumSearchActivity.BUNDE_SERIANO;
import static com.videogo.ui.devicelist.SeriesNumSearchActivity.BUNDE_VERYCODE;

public class APWifiConfigActivity extends RootActivity {

    private String mVerifyCode;
    private String mDeviceSerial;
    private String mSSID;
    private String mPassword;
    private final static String TAG = APWifiConfigActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apwifi_config);

        Intent intent = getIntent();
        mDeviceSerial = intent.getStringExtra(BUNDE_SERIANO);
        mVerifyCode = intent.getStringExtra(BUNDE_VERYCODE);
        mSSID = getIntent().getStringExtra(WIFI_SSID);
        mPassword = getIntent().getStringExtra(WIFI_PASSWORD);
        getWindow().getDecorView().post(new Runnable() {
            @Override
            public void run() {
                startApConfig();
            }
        });

    }

    private final static String CONFIG_WIFI_PREFIX = "EZVIZ_";
    private boolean isNeedCheckDeviceOnlineAgain = false;
    private void startApConfig() {
        getWindow().getDecorView().post(new Runnable() {
            @Override
            public void run() {
                hideConfigWifiErrorUi();
                EZOpenSDK.getInstance().startAPConfigWifiWithSsid(mSSID, mPassword, mDeviceSerial, mVerifyCode,
                        CONFIG_WIFI_PREFIX + mDeviceSerial, CONFIG_WIFI_PREFIX + mVerifyCode,
                        true, new APWifiConfig.APConfigCallback() {
                            @Override
                            public void onSuccess() {
                                Log.e(TAG, "connected to device");
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (checkDeviceOnlineStatusCyclically(30 * 1000, 5 * 1000)){
                                            addDeviceToAccount();
                                        }else{
                                            showConfigWifiErrorUi();
                                        }
                                    }
                                }).start();
                            }

                            @Override
                            public void OnError(int code) {

                            }

                            @Override
                            public void onErrorNew(ConfigWifiErrorEnum error) {
                                Log.e(TAG, error.code + "," + error.description);
                                if (error.code == ConfigWifiErrorEnum.CONFIG_TIMEOUT.code){
                                    showConfigWifiErrorUi();
                                }
                            }
                        });
            }
        });
    }

    private void stopApConfig(){
        EZOpenSDK.getInstance().stopAPConfigWifiWithSsid();
        isNeedCheckDeviceOnlineAgain = false;
    }

    /**
     * 隔一段时间查询一次设备是否上线，上线后自动添加到账户
     * @param totalTimeMs 总共用于查询设备的时间
     * @param intervalTimeMs 两次查询间隔
     */
    private boolean checkDeviceOnlineStatusCyclically(int totalTimeMs, int intervalTimeMs){
        EZOpenSDK.getInstance().stopAPConfigWifiWithSsid();
        isNeedCheckDeviceOnlineAgain = true;
        int tryCnt = 0;
        while (isNeedCheckDeviceOnlineAgain){
            EZProbeDeviceInfoResult result = EZOpenSDK.getInstance().probeDeviceInfo(mDeviceSerial, null);
            if (result != null && (result.getBaseException() == null || result.getBaseException().getErrorCode() == 120020 )){
                Log.e(TAG, "device is online");
                isNeedCheckDeviceOnlineAgain = false;
                return true;
            }
            tryCnt++;
            if (tryCnt > (totalTimeMs / intervalTimeMs)){
                isNeedCheckDeviceOnlineAgain = false;
            }
            // 每隔5秒查一次设备是否上线
            try {
                Thread.sleep(intervalTimeMs);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    private void showConfigWifiErrorUi() {
        getWindow().getDecorView().post(new Runnable() {
            @Override
            public void run() {
                ViewGroup errLayout = (ViewGroup) findViewById(R.id.vg_err_info);
                if (errLayout != null){
                    errLayout.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void hideConfigWifiErrorUi() {
        getWindow().getDecorView().post(new Runnable() {
            @Override
            public void run() {
                ViewGroup errLayout = (ViewGroup) findViewById(R.id.vg_err_info);
                if (errLayout != null){
                    errLayout.setVisibility(View.GONE);
                }
            }
        });
    }

    private void addDeviceToAccount(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean addSuccess = false;
                try {
                    addSuccess = EZOpenSDK.getInstance().addDevice(mDeviceSerial, mVerifyCode);
                } catch (BaseException e) {
                    ErrorInfo errorInfo = e.getErrorInfo();
                    LogUtil.infoLog(TAG, "" + errorInfo);
                    // 设备在线，已被自己添加
                    if (errorInfo.errorCode == 120017){
                        addSuccess = true;
                    }
                }
                // 添加成功，回到主界面
                if (addSuccess){
                    Log.e(TAG, "added device to account");
                    getWindow().getDecorView().post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), getApplicationContext().getString(R.string.config_wifi_succeed),
                                    Toast.LENGTH_LONG).show();
                            jumpToCameraListActivity();
                        }
                    });
                }
            }
        }).start();
    }

    private void jumpToCameraListActivity(){
        Intent toCameraListActivity = new Intent(getApplicationContext(), EZCameraListActivity.class);
        startActivity(toCameraListActivity);
        finish();
    }

    public void onClickTryConfigWifiAgain(View view) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (checkDeviceOnlineStatusCyclically(0, 1)){
                    addDeviceToAccount();
                }else{
                    startApConfig();
                }
            }
        }).start();
    }

    @Override
    public void onBackPressed() {
        stopApConfig();
        super.onBackPressed();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

}
