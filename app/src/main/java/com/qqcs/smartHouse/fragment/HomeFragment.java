package com.qqcs.smartHouse.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.flyco.tablayout.SlidingTabLayout;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.qqcs.smartHouse.R;
import com.qqcs.smartHouse.activity.HomeManageActivity;
import com.qqcs.smartHouse.activity.MainActivity;

import com.qqcs.smartHouse.activity.RoomManageActivity;
import com.qqcs.smartHouse.adapter.DeviceListAdapter;
import com.qqcs.smartHouse.adapter.RoomManageAdapter;
import com.qqcs.smartHouse.application.Constants;
import com.qqcs.smartHouse.application.SP_Constants;
import com.qqcs.smartHouse.models.CurUserInfoBean;
import com.qqcs.smartHouse.models.DeviceBean;
import com.qqcs.smartHouse.models.EventBusBean;
import com.qqcs.smartHouse.models.RoomBean;
import com.qqcs.smartHouse.models.RoomListBean;
import com.qqcs.smartHouse.network.CommonJsonList;
import com.qqcs.smartHouse.network.MyStringCallback;
import com.qqcs.smartHouse.utils.ActivityManagerUtil;
import com.qqcs.smartHouse.utils.CommonUtil;
import com.qqcs.smartHouse.utils.ImageLoaderUtil;
import com.qqcs.smartHouse.utils.LogUtil;
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
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.header.MaterialHeader;
import okhttp3.Call;
import okhttp3.MediaType;

import static android.app.Activity.RESULT_OK;


/**
 * Created by ameng on 2016/6/15.
 */
public class HomeFragment extends BaseFragment implements OnTabSelectListener {

    private View mRootView;
    private Context mContext;
    private Unbinder bind;

    @BindView(R.id.welcome_date_tv)
    TextView mWelcomeDateTv;

    @BindView(R.id.home_name_tv)
    TextView mHomeNameTv;

    @BindView(R.id.home_img)
    ImageView mHomeImg;

    @BindView(R.id.tab_layout)
    SlidingTabLayout mTabLayout;

    @BindView(R.id.vp)
    ViewPager mViewPager;

    @BindView(R.id.room_manage_img)
    ImageView mRoomManageImg;

    @BindView(R.id.ptr_frame)
    PtrFrameLayout mPtrFrame;

    @BindView(R.id.device_list)
    ListView mDeviceList;

    private MyHomesListPopupWindow mPopwindow;

    private List<RoomBean> mRoomTitles = new ArrayList();
    private MyPagerAdapter mAdapter;
    private ArrayList<Fragment> mFragments = new ArrayList<>();
    private DeviceListAdapter mDeviceAdapter;
    private ArrayList<DeviceBean> mDeviceDatas = new ArrayList<>();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
        EventBus.getDefault().register(this);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setSystemBarTransPrent();
        if (mRootView == null) {
            mRootView = inflater.inflate(R.layout.fragment_home, null);

            bind = ButterKnife.bind(this, mRootView);

            initView();
        }
        ViewGroup parent = (ViewGroup) mRootView.getParent();
        if (parent != null) {
            parent.removeView(mRootView);
        }

        return mRootView;
    }


    private void initView() {

        mRoomManageImg.setOnClickListener(this);

        Calendar calendar = Calendar.getInstance();
        String date = calendar.get(Calendar.YEAR) + ", "
                + (calendar.get(Calendar.MONTH) + 1) + "月"
                + calendar.get(Calendar.DAY_OF_MONTH) + "日";
        mWelcomeDateTv.setText(date);
        mHomeImg.setOnClickListener(this);

        getCurrentInfo();
        getRoomInfo();

        //初始化popupwindow
        mPopwindow = new MyHomesListPopupWindow(getContext(), this, mHomeImg);

        // 下拉刷新
        final MaterialHeader header = new MaterialHeader(mContext);
        int[] colors = getResources().getIntArray(R.array.google_colors);
        header.setColorSchemeColors(colors);
        header.setLayoutParams(new PtrFrameLayout.LayoutParams(-1, -2));
        header.setPadding(0, CommonUtil.dip2px(mContext, 15), 0, CommonUtil.dip2px(mContext, 10));
        header.setPtrFrameLayout(mPtrFrame);
        mPtrFrame.setHeaderView(header);
        mPtrFrame.addPtrUIHandler(header);

        mPtrFrame.setPtrHandler(new PtrDefaultHandler() {
            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                frame.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getDeviceInfo(mRoomTitles.get(mTabLayout.getCurrentTab()).getRoomId());
                        getCurrentInfo();
                        mPopwindow = new MyHomesListPopupWindow(getContext(), HomeFragment.this, mHomeImg);
                    }
                }, 100);
            }
        });

    }


    private void getCurrentInfo() {

        String accessToken = (String) SharePreferenceUtil.
                get(mContext, SP_Constants.ACCESS_TOKEN, "");

        String familyId = (String) SharePreferenceUtil.
                get(mContext, SP_Constants.CURRENT_FAMILY_ID, "");

        String timestamp = System.currentTimeMillis() + "";
        JSONObject object = CommonUtil.getRequstJson(mContext);
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
                .url(Constants.HTTP_GET_CURRENT_INFO)
                .addHeader("access-token", accessToken)
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .content(object.toString())
                .build()
                .execute(new MyStringCallback<CurUserInfoBean>(mContext,
                        CurUserInfoBean.class, false, false) {
                    @Override
                    public void onSuccess(CurUserInfoBean data) {
                        ImageLoaderUtil.getInstance().displayImage
                                (Constants.HTTP_SERVER_DOMAIN + data.getFamilyImg(), mHomeImg);
                        mHomeNameTv.setText(data.getFamilyName());
                        ((MainActivity) getActivity()).setmMessageNumTv(data.getUnreadCount());

                    }

                    @Override
                    public void onFailure(String message) {
                        ToastUtil.showToast(mContext, message);
                    }
                });
    }

    public void getRoomInfo() {
        String accessToken = (String) SharePreferenceUtil.
                get(mContext, SP_Constants.ACCESS_TOKEN, "");

        String familyId = (String) SharePreferenceUtil.
                get(mContext, SP_Constants.CURRENT_FAMILY_ID, "");


        String timestamp = System.currentTimeMillis() + "";
        JSONObject object = CommonUtil.getRequstJson(mContext.getApplicationContext());
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
                .addHeader("access-token", accessToken)
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .content(object.toString())
                .build()
                .execute(new MyStringCallback<RoomListBean>(mContext, RoomListBean.class, false, false) {

                    @Override
                    public void onSuccess(RoomListBean json) {
                        ImageLoaderUtil.getInstance().displayImage
                                (Constants.HTTP_SERVER_DOMAIN + json.getFamilyImg(), mHomeImg);
                        mHomeNameTv.setText(json.getFamilyName());

                        mRoomTitles.clear();
                        mFragments.clear();
                        mRoomTitles.addAll(json.getRooms());

                        for (RoomBean title : mRoomTitles) {
                            mFragments.add(SimpleCardFragment.getInstance());
                        }

                        mAdapter = new MyPagerAdapter(getFragmentManager());
                        mViewPager.setAdapter(mAdapter);

                        mTabLayout.setViewPager(mViewPager);
                        mTabLayout.setOnTabSelectListener(HomeFragment.this);

                        getDeviceInfo(json.getRooms().get(0).getRoomId());
                    }

                    @Override
                    public void onFailure(String message) {
                        ToastUtil.showToast(mContext, message);
                    }
                });
    }

    private void getDeviceInfo(String roomId) {

        String accessToken = (String) SharePreferenceUtil.
                get(mContext, SP_Constants.ACCESS_TOKEN, "");

        String familyId = (String) SharePreferenceUtil.
                get(mContext, SP_Constants.CURRENT_FAMILY_ID, "");

        String timestamp = System.currentTimeMillis() + "";
        JSONObject object = CommonUtil.getRequstJson(mContext);
        JSONObject dataObject = new JSONObject();

        try {
            dataObject.put("familyId", familyId);
            dataObject.put("roomId", roomId);
            dataObject.put("timestamp", timestamp);

            object.put("data", dataObject);
            object.put("sign", MD5Utils.md5s(familyId + roomId + timestamp));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        OkHttpUtils
                .postString()
                .tag(this)
                .url(Constants.HTTP_GET_DEVICE_INFO)
                .addHeader("access-token", accessToken)
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .content(object.toString())
                .build()
                .execute(new MyStringCallback<DeviceBean>(mContext,
                        DeviceBean.class, false, true) {

                    @Override
                    public void onSuccess(CommonJsonList<DeviceBean> json) {
                        mPtrFrame.refreshComplete();
                        mDeviceDatas.clear();
                        mDeviceDatas.addAll(json.getData());
                        setDeviceAdapter();
                    }


                    @Override
                    public void onFailure(String message) {
                        ToastUtil.showToast(mContext, message);
                        mPtrFrame.refreshComplete();
                    }

                    @Override
                    public void onError(Call call, Exception e, int i) {
                        super.onError(call, e, i);
                        mPtrFrame.refreshComplete();
                    }

                });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventBus(EventBusBean event) {

        switch (event.getType()) {

            case EventBusBean.FAMILY_ID_CHANGED:
                getRoomInfo();
                mPopwindow = new MyHomesListPopupWindow(getContext(),
                        HomeFragment.this, mHomeImg);

                break;

            case EventBusBean.REFRESH_HOME:
                getDeviceInfo(mRoomTitles.get(mTabLayout.getCurrentTab()).getRoomId());

                break;

            case EventBusBean.REFRESH_HOME_AND_PROPT:
                getDeviceInfo(mRoomTitles.get(mTabLayout.getCurrentTab()).getRoomId());

                break;

        }

    }

    @Override
    public void onTabSelect(int position) {
        LogUtil.d("onTabSelect:" + position);
        getDeviceInfo(mRoomTitles.get(position).getRoomId());
    }

    @Override
    public void onTabReselect(int position) {
        LogUtil.d("onTabReselect:" + position);
        getDeviceInfo(mRoomTitles.get(position).getRoomId());
    }


    public String getCurrentRoomId() {
        return mRoomTitles.get(mTabLayout.getCurrentTab()).getRoomId();
    }


    public void setDeviceAdapter() {
        if (mDeviceAdapter == null) {
            mDeviceAdapter = new DeviceListAdapter(mContext,this, mDeviceDatas);
            mDeviceList.setAdapter(mDeviceAdapter);
        } else {
            mDeviceAdapter.refreshData(mDeviceDatas);
        }

    }

    @Override
    public void onMultiClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.home_img:
                mPopwindow.show();

                break;
            case R.id.room_manage_img:
                intent = new Intent(mContext, RoomManageActivity.class);
                startActivityForResult(intent, 1);

                break;


        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        }
        switch (requestCode) {

            case 1:
                getRoomInfo();
                break;

        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //解除绑定
        bind.unbind();
        EventBus.getDefault().unregister(this);
    }


    private class MyPagerAdapter extends FragmentPagerAdapter {
        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return mRoomTitles.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mRoomTitles.get(position).getRoomName();
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }
    }
}
