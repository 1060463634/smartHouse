package com.qqcs.smartHouse.fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.qqcs.smartHouse.R;
import com.qqcs.smartHouse.activity.FootActivity;
import com.qqcs.smartHouse.activity.HealthCenterActivity;
import com.qqcs.smartHouse.activity.MemberManageActivity;
import com.qqcs.smartHouse.adapter.HealthListAdapter;
import com.qqcs.smartHouse.adapter.HomeManageAdapter;
import com.qqcs.smartHouse.application.Constants;
import com.qqcs.smartHouse.application.SP_Constants;
import com.qqcs.smartHouse.models.FamilyInfoBean;
import com.qqcs.smartHouse.models.FamilyMemberBean;
import com.qqcs.smartHouse.network.CommonJsonList;
import com.qqcs.smartHouse.network.MyStringCallback;
import com.qqcs.smartHouse.utils.CommonUtil;
import com.qqcs.smartHouse.utils.MD5Utils;
import com.qqcs.smartHouse.utils.SharePreferenceUtil;
import com.qqcs.smartHouse.utils.ToastUtil;
import com.zhy.http.okhttp.OkHttpUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import okhttp3.MediaType;


public class HealthFragment extends BaseFragment {
    private View mRootView;
    private Context mContext;
    private Unbinder bind;

    @BindView(R.id.list_view)
    ListView mListView;

    private HealthListAdapter mAdapter;
    private ArrayList<FamilyMemberBean> mDataSource = new ArrayList<>();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
    }

    @Override
    public void onResume() {
        super.onResume();
        getData();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setSystemBarWhite();
        if (mRootView == null) {
            mRootView = inflater.inflate(R.layout.fragment_health, null);
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

        getData();

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(mContext, HealthCenterActivity.class);
                intent.putExtra("name",mDataSource.get(position).getNickName());
                startActivity(intent);
            }
        });
    }

    private void getData() {
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
                .url(Constants.HTTP_LIST_FAMILY)
                .addHeader("access-token",accessToken)
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .content(object.toString())
                .build()
                .execute(new MyStringCallback<FamilyMemberBean>(mContext, FamilyMemberBean.class, false, true) {

                    @Override
                    public void onSuccess(CommonJsonList<FamilyMemberBean> json) {
                        mDataSource.clear();
                        mDataSource.addAll(json.getData());
                        setMyAdapter();
                    }

                    @Override
                    public void onFailure(String message) {
                        ToastUtil.showToast(mContext, message);
                    }
                });
    }

    public void setMyAdapter() {
        if (mAdapter == null) {
            mAdapter = new HealthListAdapter(mContext, mDataSource);
            mListView.setAdapter(mAdapter);
        } else {
            mAdapter.refreshData(mDataSource);
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //解除绑定
        bind.unbind();
    }


}
