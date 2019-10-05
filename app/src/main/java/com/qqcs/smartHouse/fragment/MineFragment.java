package com.qqcs.smartHouse.fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qqcs.smartHouse.R;
import com.qqcs.smartHouse.activity.CreateHomeActivity;
import com.qqcs.smartHouse.activity.FillInfomationActivity;
import com.qqcs.smartHouse.activity.HomeManageActivity;
import com.qqcs.smartHouse.activity.LoginActivity;
import com.qqcs.smartHouse.activity.MainActivity;
import com.qqcs.smartHouse.activity.MemberManageActivity;
import com.qqcs.smartHouse.activity.UserInfoActivity;
import com.qqcs.smartHouse.activity.WelcomeHomeActivity;
import com.qqcs.smartHouse.application.Constants;
import com.qqcs.smartHouse.application.SP_Constants;
import com.qqcs.smartHouse.models.CurUserInfoBean;
import com.qqcs.smartHouse.models.FamilyInfoBean;
import com.qqcs.smartHouse.network.MyStringCallback;
import com.qqcs.smartHouse.utils.CommonUtil;
import com.qqcs.smartHouse.utils.ImageLoaderUtil;
import com.qqcs.smartHouse.utils.MD5Utils;
import com.qqcs.smartHouse.utils.SharePreferenceUtil;
import com.qqcs.smartHouse.utils.ToastUtil;
import com.zhy.http.okhttp.OkHttpUtils;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import okhttp3.MediaType;


public class MineFragment extends BaseFragment{

    @BindView(R.id.my_home_layout)
    LinearLayout mHomeLayout;

    @BindView(R.id.my_info_layout)
    LinearLayout mInfoLayout;

    @BindView(R.id.member_manage_layout)
    LinearLayout mMemberLayout;

    @BindView(R.id.version_tv)
    TextView mVersionTv;

    @BindView(R.id.home_img)
    ImageView mHomeImg;

    @BindView(R.id.home_name)
    TextView mHomeTv;

    @BindView(R.id.message_img)
    ImageView mMessageImg;

    @BindView(R.id.message_tv)
    TextView mMessageTv;

    private View mRootView;
    private Context mContext;
    private Unbinder bind;




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setSystemBarTransPrent();
        if (mRootView == null) {
            mRootView = inflater.inflate(R.layout.fragment_mine, null);

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
        mHomeLayout.setOnClickListener(this);
        mInfoLayout.setOnClickListener(this);
        mMemberLayout.setOnClickListener(this);
        mVersionTv.setText(CommonUtil.getVersionName(mContext));
        getCurrentInfo();


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
                        mHomeTv.setText(data.getFamilyName());
                        mVersionTv.setText(data.getAppVersion());
                        mMessageTv.setText(data.getUnreadCount());

                        //如果超过99条消息显示99+
                        try {
                            int num = Integer.parseInt(data.getUnreadCount());
                            if(num > 99){
                                mMessageTv.setText("99+");
                            }
                        }catch (Exception e){

                        }
                        //如果没有消息不显示提示
                        if(!data.getUnreadCount().equalsIgnoreCase("0")){
                            mMessageTv.setVisibility(View.VISIBLE);
                        }

                        //普通成员成员管理不显示
                        if(data.getUserRole().equalsIgnoreCase(Constants.ROLE_NORMAL) ){
                            mMemberLayout.setVisibility(View.GONE);
                        }

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
            case R.id.my_home_layout:
                intent = new Intent(mContext, HomeManageActivity.class);
                startActivity(intent);
                break;
            case R.id.my_info_layout:
                intent = new Intent(mContext, UserInfoActivity.class);
                startActivity(intent);
                break;
            case R.id.member_manage_layout:
                intent = new Intent(mContext, MemberManageActivity.class);
                startActivity(intent);
                break;
            case R.id.version_manage_layout:

                break;

        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        //解除绑定
        bind.unbind();
    }


}
