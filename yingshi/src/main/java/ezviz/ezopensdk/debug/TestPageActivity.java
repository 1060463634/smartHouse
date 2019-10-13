package ezviz.ezopensdk.debug;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.videogo.RootActivity;
import com.videogo.openapi.EZOpenSDK;
import com.videogo.wificonfig.APWifiConfig;
import com.videogo.wificonfig.ConfigWifiErrorEnum;

import ezviz.ezopensdk.R;

public class TestPageActivity extends RootActivity {

    private final static String TAG = TestPageActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_page);
    }

    public void goToCloudPayPage(View view) {
//        try {
//            EZOpenSDK.getInstance().openCloudPage("831108697", 1);
//        } catch (BaseException e) {
//            e.printStackTrace();
//        }
    }

    public void tryToDownloadCert(View view) {
//        RestfulUtils.getInstance().testDownCert();
    }

    public void onClickTestSomething(View view) {
        testSomething();
    }

    private void testSomething() {
        EZOpenSDK.getInstance().startAPConfigWifiWithSsid("wifi_ssid", "wifi_pwd",
                "device_serial", "verifyCode", new APWifiConfig.APConfigCallback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void OnError(int code) {
                        EZOpenSDK.getInstance().stopAPConfigWifiWithSsid();
                    }

                    @Override
                    public void onErrorNew(ConfigWifiErrorEnum error) {
                        Log.e(TAG, error.name());
                        EZOpenSDK.getInstance().stopAPConfigWifiWithSsid();
                    }
                });
    }

    @Override
    public void finish() {
        super.finish();
        exitApp();
    }
}
