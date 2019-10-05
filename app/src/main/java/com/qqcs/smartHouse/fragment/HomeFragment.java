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
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.flyco.tablayout.SlidingTabLayout;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.qqcs.smartHouse.R;
import com.qqcs.smartHouse.activity.HomeManageActivity;
import com.qqcs.smartHouse.activity.MainActivity;

import com.qqcs.smartHouse.activity.RoomManageActivity;
import com.qqcs.smartHouse.application.Constants;
import com.qqcs.smartHouse.application.SP_Constants;
import com.qqcs.smartHouse.models.CurUserInfoBean;
import com.qqcs.smartHouse.models.FamilyInfoBean;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import okhttp3.MediaType;


/**
 * Created by ameng on 2016/6/15.
 */
public class HomeFragment extends BaseFragment {

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

    private MyHomesListPopupWindow mPopwindow;

    private final String[] mTitles = {
            "全部", "会客厅", "卧室"
            , "卫生间", "书房", "厨房", "浴室"
    };
    private MyPagerAdapter mAdapter;
    private ArrayList<Fragment> mFragments = new ArrayList<>();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
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



    private void initView(){

        mRoomManageImg.setOnClickListener(this);

        Calendar calendar = Calendar.getInstance();
        String date = calendar.get(Calendar.YEAR) + ", "
                + (calendar.get(Calendar.MONTH) + 1) + "月"
                + calendar.get(Calendar.DAY_OF_MONTH) + "日";
        mWelcomeDateTv.setText(date);
        mHomeImg.setOnClickListener(this);
        getCurrentInfo();

        //初始化popupwindow
        mPopwindow = new MyHomesListPopupWindow(getContext(),mHomeImg);


        //初始化tab
        for (String title : mTitles) {
            mFragments.add(SimpleCardFragment.getInstance(title));
        }
        mAdapter = new MyPagerAdapter(getFragmentManager());
        mViewPager.setAdapter(mAdapter);
        mTabLayout.setViewPager(mViewPager);
        mTabLayout.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                LogUtil.d("onTabSelect --->" + position);

            }

            @Override
            public void onTabReselect(int position) {
                LogUtil.d("onTabSelec --->" + position);

            }
        });


    }


    private void getCurrentInfo() {

        String accessToken = (String) SharePreferenceUtil.
                get(mContext, SP_Constants.ACCESS_TOKEN,"");

        String familyId = (String) SharePreferenceUtil.
                get(mContext, SP_Constants.CURRENT_FAMILY_ID,"");

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
                .addHeader("access-token",accessToken)
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .content(object.toString())
                .build()
                .execute(new MyStringCallback<CurUserInfoBean>(mContext,
                        CurUserInfoBean.class, false, false) {
                    @Override
                    public void onSuccess(CurUserInfoBean data) {
                        ImageLoaderUtil.getInstance().displayImage
                                (Constants.HTTP_SERVER_DOMAIN + data.getFamilyImg(),mHomeImg);
                        mHomeNameTv.setText(data.getFamilyName());
                        ((MainActivity)getActivity()).setmMessageNumTv(data.getUnreadCount());

                    }

                    @Override
                    public void onFailure(String message) {
                        ToastUtil.showToast(mContext, message);
                    }
                });
    }

    @Override
    public void onMultiClick(View v) {
        Intent intent;
        switch (v.getId()){
            case R.id.home_img:
                mPopwindow.show();

                break;
            case R.id.room_manage_img:
                intent = new Intent(mContext, RoomManageActivity.class);
                startActivity(intent);

                break;


        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //解除绑定
        bind.unbind();
    }


    private class MyPagerAdapter extends FragmentPagerAdapter {
        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTitles[position];
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }
    }
}
