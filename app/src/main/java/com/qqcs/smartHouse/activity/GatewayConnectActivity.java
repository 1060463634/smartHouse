package com.qqcs.smartHouse.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;

import com.inuker.bluetooth.library.connect.listener.BluetoothStateListener;
import com.inuker.bluetooth.library.search.SearchRequest;
import com.inuker.bluetooth.library.search.SearchResult;
import com.inuker.bluetooth.library.search.response.SearchResponse;
import com.inuker.bluetooth.library.utils.BluetoothLog;
import com.qqcs.smartHouse.R;

import com.qqcs.smartHouse.adapter.BlueDeviceListAdapter;
import com.qqcs.smartHouse.adapter.DeviceListAdapter;
import com.qqcs.smartHouse.bluetooth.ClientManager;
import com.qqcs.smartHouse.utils.CommonUtil;
import com.qqcs.smartHouse.utils.ToastUtil;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.header.MaterialHeader;
import okhttp3.MediaType;


public class GatewayConnectActivity extends BaseActivity{



    @BindView(R.id.ptr_frame)
    PtrFrameLayout mPtrFrame;

    @BindView(R.id.device_list)
    ListView mListView;

    private List<SearchResult> mDevices = new ArrayList<SearchResult>();

    private BlueDeviceListAdapter mAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gateway_connect);
        ButterKnife.bind(this);
        setTitleName("建立蓝牙连接");
        initView();

    }


    private void initView(){
        mAdapter = new BlueDeviceListAdapter(this);
        mListView.setAdapter(mAdapter);

        // 下拉刷新
        final MaterialHeader header = new MaterialHeader(this);
        int[] colors = getResources().getIntArray(R.array.google_colors);
        header.setColorSchemeColors(colors);
        header.setLayoutParams(new PtrFrameLayout.LayoutParams(-1, -2));
        header.setPadding(0, CommonUtil.dip2px(this, 15), 0, CommonUtil.dip2px(this, 10));
        header.setPtrFrameLayout(mPtrFrame);
        mPtrFrame.setHeaderView(header);
        mPtrFrame.addPtrUIHandler(header);

        mPtrFrame.setPtrHandler(new PtrDefaultHandler() {
            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                frame.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        searchDevice();
                    }
                }, 100);
            }
        });


        ClientManager.getClient().registerBluetoothStateListener(new BluetoothStateListener() {
            @Override
            public void onBluetoothStateChanged(boolean openOrClosed) {
                BluetoothLog.v(String.format("onBluetoothStateChanged %b", openOrClosed));
                if(openOrClosed){
                    ToastUtil.showToast(GatewayConnectActivity.this,"蓝牙已打开");
                    mPtrFrame.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mPtrFrame.autoRefresh(true);
                        }
                    }, 0);

                }else {
                    ToastUtil.showToast(GatewayConnectActivity.this,"蓝牙已关闭");
                }
            }
        });

        if(ClientManager.getClient().isBluetoothOpened()){
            mPtrFrame.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mPtrFrame.autoRefresh(true);
                }
            }, 0);


        }else {
            ClientManager.getClient().openBluetooth();
        }

    }

    private void searchDevice() {
        SearchRequest request = new SearchRequest.Builder()
                .searchBluetoothLeDevice(3000, 1).build();

        ClientManager.getClient().search(request, mSearchResponse);
    }
    private final SearchResponse mSearchResponse = new SearchResponse() {
        @Override
        public void onSearchStarted() {
            BluetoothLog.w("MainActivity.onSearchStarted");

        }

        @Override
        public void onDeviceFounded(SearchResult device) {
//            BluetoothLog.w("MainActivity.onDeviceFounded " + device.device.getAddress());
            if (!mDevices.contains(device)) {
                mDevices.add(device);
                mAdapter.setDataList(mDevices);

//                Beacon beacon = new Beacon(device.scanRecord);
//                BluetoothLog.v(String.format("beacon for %s\n%s", device.getAddress(), beacon.toString()));

//                BeaconItem beaconItem = null;
//                BeaconParser beaconParser = new BeaconParser(beaconItem);
//                int firstByte = beaconParser.readByte(); // 读取第1个字节
//                int secondByte = beaconParser.readByte(); // 读取第2个字节
//                int productId = beaconParser.readShort(); // 读取第3,4个字节
//                boolean bit1 = beaconParser.getBit(firstByte, 0); // 获取第1字节的第1bit
//                boolean bit2 = beaconParser.getBit(firstByte, 1); // 获取第1字节的第2bit
//                beaconParser.setPosition(0); // 将读取起点设置到第1字节处
            }

        }

        @Override
        public void onSearchStopped() {
            BluetoothLog.w("MainActivity.onSearchStopped");
            mPtrFrame.refreshComplete();

        }

        @Override
        public void onSearchCanceled() {
            BluetoothLog.w("MainActivity.onSearchCanceled");

            mPtrFrame.refreshComplete();
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        ClientManager.getClient().stopSearch();
    }


    @Override
    public void onMultiClick(View v) {
        switch (v.getId()){
            case R.id.gateway_img:
                break;

        }
    }

}
