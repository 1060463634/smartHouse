package com.qqcs.smartHouse.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.qqcs.smartHouse.R;
import com.qqcs.smartHouse.application.Constants;
import com.qqcs.smartHouse.application.OnMultiClickListener;
import com.qqcs.smartHouse.application.SP_Constants;
import com.qqcs.smartHouse.models.CurUserInfoBean;
import com.qqcs.smartHouse.models.GatewayInfoBean;
import com.qqcs.smartHouse.network.CommonJson;
import com.qqcs.smartHouse.network.GsonUtils;
import com.qqcs.smartHouse.network.MyStringCallback;
import com.qqcs.smartHouse.utils.CommonUtil;
import com.qqcs.smartHouse.utils.DateUtil;
import com.qqcs.smartHouse.utils.ImageLoaderUtil;
import com.qqcs.smartHouse.utils.LogUtil;
import com.qqcs.smartHouse.utils.MD5Utils;
import com.qqcs.smartHouse.utils.SharePreferenceUtil;
import com.qqcs.smartHouse.utils.ToastUtil;
import com.zhy.http.okhttp.OkHttpUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.MediaType;


public class GatewayManageActivity extends BaseActivity{
    public static String mDeviceName = "";


    @BindView(R.id.gateway_img)
    ImageView mGatewayImg;

    @BindView(R.id.gateway_name_tv)
    TextView mGatewayName;

    @BindView(R.id.gateway_version_tv)
    TextView mGatewayVersion;

    private String mFamilyId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gateway_manage);
        ButterKnife.bind(this);
        setTitleName("网关信息");
        initView();

    }


    private void initView(){
        mFamilyId = getIntent().getStringExtra("familyId");
        mGatewayImg.setOnClickListener(this);
        getGatewayInfo();

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setContentView(R.layout.activity_gateway_manage);
        ButterKnife.bind(this);
        setTitleName("网关信息");
        initView();

    }

    private void getGatewayInfo() {

        String accessToken = (String) SharePreferenceUtil.
                get(this, SP_Constants.ACCESS_TOKEN,"");

        String timestamp = System.currentTimeMillis() + "";
        JSONObject object = CommonUtil.getRequstJson(this);
        JSONObject dataObject = new JSONObject();

        try {
            dataObject.put("familyId", mFamilyId);
            dataObject.put("timestamp", timestamp);

            object.put("data", dataObject);
            object.put("sign", MD5Utils.md5s(mFamilyId + timestamp));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        OkHttpUtils
                .postString()
                .tag(this)
                .url(Constants.HTTP_GATEWAY_INFO)
                .addHeader("access-token",accessToken)
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .content(object.toString())
                .build()
                .execute(new MyStringCallback<GatewayInfoBean>(this,
                        GatewayInfoBean.class, false, false) {

                    @Override
                    public void onResponse(String s, int i) {
                        LogUtil.d("http onSuccess  :" + s);

                        try {
                                CommonJson<GatewayInfoBean> commonJson = GsonUtils.fromJson(s, GatewayInfoBean.class);
                                if (commonJson.getStatus().equalsIgnoreCase("Success")) {
                                    onSuccess(commonJson.getData());
                                } else {
                                    onFailure(commonJson.getMessage());
                                    onFailure(commonJson.getData());
                                }


                        } catch (Exception e1) {
                            setContentView(R.layout.activity_gateway_manage_no);
                            findViewById(R.id.next_btn).setOnClickListener(new OnMultiClickListener() {
                                @Override
                                public void onMultiClick(View v) {
                                    Intent intent = new Intent(GatewayManageActivity.this,GatewayConnectActivity.class);
                                    intent.putExtra("familyId",mFamilyId);
                                    startActivity(intent);
                                }
                            });
                        }

                    }

                    @Override
                    public void onSuccess(GatewayInfoBean data) {
                        mGatewayName.setText("网关名称：" + data.getDeviceName());
                        mDeviceName = data.getDeviceName();
                        Date date = new Date(Long.parseLong(data.getAddTime()));
                        mGatewayVersion.setText("入网时间：" + DateUtil.data2STString(date));

                    }

                    @Override
                    public void onFailure(String message) {
                        ToastUtil.showToast(GatewayManageActivity.this, message);
                    }
                });
    }




    @Override
    public void onMultiClick(View v) {
        switch (v.getId()){
            case R.id.gateway_img:
                Intent intent = new Intent(this,GatewayConnectActivity.class);
                intent.putExtra("familyId",mFamilyId);
                startActivity(intent);
                break;

        }
    }

}
