package com.qqcs.smartHouse.bluetooth;

import com.inuker.bluetooth.library.BluetoothClient;
import com.qqcs.smartHouse.application.MyApplication;

/**
 * Created by dingjikerbo on 2016/8/27.
 */
public class ClientManager {

    private static BluetoothClient mClient;

    public static BluetoothClient getClient() {
        if (mClient == null) {
            synchronized (ClientManager.class) {
                if (mClient == null) {
                    mClient = new BluetoothClient(MyApplication.getApplication());
                }
            }
        }
        return mClient;
    }
}
