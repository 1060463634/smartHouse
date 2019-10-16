package com.qqcs.smartHouse.fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.qqcs.smartHouse.R;
import com.qqcs.smartHouse.activity.CreateHomeActivity;
import com.qqcs.smartHouse.activity.LoginActivity;
import com.qqcs.smartHouse.activity.MainActivity;
import com.qqcs.smartHouse.activity.WelcomeHomeActivity;
import com.qqcs.smartHouse.application.Constants;
import com.qqcs.smartHouse.application.SP_Constants;
import com.qqcs.smartHouse.models.DeviceBean;
import com.qqcs.smartHouse.network.MyStringCallback;
import com.qqcs.smartHouse.utils.CommonUtil;
import com.qqcs.smartHouse.utils.MD5Utils;
import com.qqcs.smartHouse.utils.SharePreferenceUtil;
import com.qqcs.smartHouse.utils.ToastUtil;
import com.zhy.http.okhttp.OkHttpUtils;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import okhttp3.MediaType;


/**
 * Created by ameng on 2016/6/15.
 */
public class ScenceFragment extends BaseFragment {

    private View mRootView;
    private Context mContext;
    private Unbinder bind;
    @BindView(R.id.night_scene_img)
    ImageView mNightImg;

    @BindView(R.id.morning_scene_img)
    ImageView mMoringImg;

    @BindView(R.id.leave_scene_img)
    ImageView mLeaveImg;

    @BindView(R.id.back_scene_img)
    ImageView mBackImg;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setSystemBarWhite();
        if (mRootView == null) {
            mRootView = inflater.inflate(R.layout.fragment_scence, null);

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
        mNightImg.setOnClickListener(this);
        mMoringImg.setOnClickListener(this);
        mLeaveImg.setOnClickListener(this);
        mBackImg.setOnClickListener(this);

    }

    private void showCustomToast(String type){
        if(type.equalsIgnoreCase("1")){


        }else if(type.equalsIgnoreCase("1")){

        }

        Toast toast2;
        View view;
        toast2 = new Toast(mContext);
        view = LayoutInflater.from(mContext).inflate(R.layout.toast_commom, null);
        ((TextView)view.findViewById(R.id.text)).setText("开启晚安模式");
        toast2.setView(view);
        toast2.setGravity(Gravity.CENTER, 0, 0);
        toast2.show();
    }

    private void openSence(final String type) {

        String accessToken = (String) SharePreferenceUtil.
                get(mContext, SP_Constants.ACCESS_TOKEN,"");
        String familyId = (String) SharePreferenceUtil.
                get(mContext, SP_Constants.CURRENT_FAMILY_ID,"");

        String timestamp = System.currentTimeMillis() + "";
        JSONObject object = CommonUtil.getRequstJson(mContext);
        JSONObject dataObject = new JSONObject();

        try {
            dataObject.put("type", type);
            dataObject.put("familyId", familyId);
            dataObject.put("timestamp", timestamp);

            object.put("data", dataObject);
            object.put("sign", MD5Utils.md5s(type + familyId + timestamp));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        OkHttpUtils
                .postString()
                .tag(this)
                .url(Constants.HTTP_SITUATION_DOACTION)
                .addHeader("access-token",accessToken)
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .content(object.toString())
                .build()
                .execute(new MyStringCallback<Object>(mContext,
                        Object.class, true, false) {
                    @Override
                    public void onSuccess(Object data) {
                        ToastUtil.showToast(mContext, "执行成功");
                    }

                    @Override
                    public void onFailure(String message) {
                        ToastUtil.showToast(mContext, message);
                    }
                });
    }


    @Override
    public void onMultiClick(View v) {

        switch (v.getId()) {
            case R.id.night_scene_img:
                openSence("2");

                break;

            case R.id.morning_scene_img:
                openSence("1");

                break;

            case R.id.leave_scene_img:
                openSence("2");

                break;

            case R.id.back_scene_img:
                openSence("1");
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
