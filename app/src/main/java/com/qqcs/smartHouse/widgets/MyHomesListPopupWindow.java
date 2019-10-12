package com.qqcs.smartHouse.widgets;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.qqcs.smartHouse.R;
import com.qqcs.smartHouse.activity.CreateHomeActivity;
import com.qqcs.smartHouse.activity.HomeManageActivity;
import com.qqcs.smartHouse.adapter.HomeManageAdapter;
import com.qqcs.smartHouse.adapter.HomesPopAdapter;
import com.qqcs.smartHouse.application.Constants;
import com.qqcs.smartHouse.application.SP_Constants;
import com.qqcs.smartHouse.fragment.HomeFragment;
import com.qqcs.smartHouse.models.EventBusBean;
import com.qqcs.smartHouse.models.FamilyInfoBean;
import com.qqcs.smartHouse.network.CommonJsonList;
import com.qqcs.smartHouse.network.MyStringCallback;
import com.qqcs.smartHouse.utils.CommonUtil;
import com.qqcs.smartHouse.utils.MD5Utils;
import com.qqcs.smartHouse.utils.SharePreferenceUtil;
import com.qqcs.smartHouse.utils.ToastUtil;
import com.zhy.http.okhttp.OkHttpUtils;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;

public class MyHomesListPopupWindow {


    private PopupWindow popupWindow;
    private Context mContext;
    private HomeFragment mFragment;
    private View view;
    private ListView mListView;
    private HomesPopAdapter mAdapter;
    private List<FamilyInfoBean> mListDatas = new ArrayList<FamilyInfoBean>();



    /**
     *
     * 构造函数
     * @param context
     * @param view 在这个控件下面
     */
    public MyHomesListPopupWindow(Context context,HomeFragment fragment, View view) {
        this.mContext = context;
        this.mFragment = fragment;
        this.view = view;
        initPopupWindow();
    }

    public void show() {
        if (popupWindow == null) {
            return;
        }
        if (popupWindow.isShowing()) {
            popupWindow.dismiss();
        } else {
            popupWindow.showAsDropDown(view, 0, 0);

        }

    }


    /**
     * 初始化
     */
    public void initPopupWindow() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_home_pop, null);
        mListView = (ListView) view.findViewById(R.id.pop_list);
        initListView();

        popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupWindow.setOutsideTouchable(true);
        popupWindow.setTouchable(true);

    }

    private void initListView() {
        setListDatas();
        setListViewListener();
    }

    private void setListDatas() {
        String accessToken = (String) SharePreferenceUtil.
                get(mContext, SP_Constants.ACCESS_TOKEN, "");
        final String currentFamilyId = (String) SharePreferenceUtil.
                get(mContext, SP_Constants.CURRENT_FAMILY_ID, "");
        String timestamp = System.currentTimeMillis() + "";
        JSONObject object = CommonUtil.getRequstJson(mContext.getApplicationContext());
        JSONObject dataObject = new JSONObject();

        try {
            dataObject.put("familyId", currentFamilyId);
            dataObject.put("timestamp", timestamp);

            object.put("data", dataObject);
            object.put("sign", MD5Utils.md5s(currentFamilyId + timestamp));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        OkHttpUtils
                .postString()
                .tag(this)
                .url(Constants.HTTP_GET_FAMILY_LIST)
                .addHeader("access-token", accessToken)
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .content(object.toString())
                .build()
                .execute(new MyStringCallback<FamilyInfoBean>(mContext, FamilyInfoBean.class, false, true) {

                    @Override
                    public void onSuccess(CommonJsonList<FamilyInfoBean> json) {
                        mListDatas.clear();
                        mListDatas.addAll(json.getData());
                        setMyAdapter();
                    }

                    @Override
                    public void onFailure(String message) {
                        ToastUtil.showToast(mContext, message);
                    }
                });
    }


    private void setListViewListener() {
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int p, long id) {
                popupWindow.dismiss();
                SharePreferenceUtil.put(mContext,
                        SP_Constants.CURRENT_FAMILY_ID, mListDatas.get(p).getFamilyId());
                EventBus.getDefault().post(new EventBusBean(EventBusBean.FAMILY_ID_CHANGED));

            }
        });

    }

    public void setMyAdapter() {
        if (mAdapter == null) {
            mAdapter = new HomesPopAdapter(mContext, mListDatas);
            mListView.setAdapter(mAdapter);
        } else {
            mAdapter.refreshData(mListDatas);
        }

    }


}
