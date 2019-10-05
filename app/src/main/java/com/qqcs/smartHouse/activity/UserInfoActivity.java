package com.qqcs.smartHouse.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.qqcs.smartHouse.R;
import com.qqcs.smartHouse.application.Constants;
import com.qqcs.smartHouse.application.SP_Constants;
import com.qqcs.smartHouse.models.UserInfoBean;
import com.qqcs.smartHouse.network.MyStringCallback;
import com.qqcs.smartHouse.utils.ActivityManagerUtil;
import com.qqcs.smartHouse.utils.CommonUtil;
import com.qqcs.smartHouse.utils.MD5Utils;
import com.qqcs.smartHouse.utils.SharePreferenceUtil;
import com.qqcs.smartHouse.utils.ToastUtil;
import com.qqcs.smartHouse.widgets.MyAlertDialog;
import com.zhy.http.okhttp.OkHttpUtils;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.MediaType;


public class UserInfoActivity extends BaseActivity{

    @BindView(R.id.nick_name_layout)
    RelativeLayout mNickNameLayout;

    @BindView(R.id.phone_layout)
    RelativeLayout mPhoneLayout;

    @BindView(R.id.exit_login_btn)
    Button mExitLoginLayout;

    @BindView(R.id.nick_name_tv)
    TextView mNickNameTv;

    @BindView(R.id.phone_tv)
    TextView mPhoneTv;

    private String mNickname;
    private String mPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        ButterKnife.bind(this);
        setTitleName("个人信息");
        initView();

    }


    private void initView(){
        mNickNameLayout.setOnClickListener(this);
        mPhoneLayout.setOnClickListener(this);
        mExitLoginLayout.setOnClickListener(this);

        getUserInfo();
    }

    private void getUserInfo(){
        String accessToken = (String) SharePreferenceUtil.
                get(this, SP_Constants.ACCESS_TOKEN,"");
        String timestamp = System.currentTimeMillis() + "";
        JSONObject object = CommonUtil.getRequstJson(getApplicationContext());
        JSONObject dataObject = new JSONObject();

        try {
            dataObject.put("timestamp", timestamp);
            object.put("data", dataObject);
            object.put("sign", MD5Utils.md5s(timestamp));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        OkHttpUtils
                .postString()
                .tag(this)
                .url(Constants.HTTP_GET_USER_INFO)
                .addHeader("access-token",accessToken)
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .content(object.toString())
                .build()
                .execute(new MyStringCallback<UserInfoBean>(this, UserInfoBean.class, false, false) {

                    @Override
                    public void onSuccess(UserInfoBean data) {
                        mNickNameTv.setText(data.getNickName());
                        mPhoneTv.setText(data.getMobile());
                        mNickname = data.getNickName();
                        mPhone = data.getMobile();
                    }

                    @Override
                    public void onFailure(String message) {
                        ToastUtil.showToast(UserInfoActivity.this, message);
                    }
                });
    }



    private void showExitConfirmDialog(){
        final MyAlertDialog dialog = new MyAlertDialog(this);
        dialog.show();
        dialog.setTitle("是否退出登录");
        dialog.setText("用户信息仍会被保留");

        dialog.setPositiveListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharePreferenceUtil.clear(UserInfoActivity.this);
                ActivityManagerUtil.getInstance().finishAllActivity();
                Intent intent = new Intent(UserInfoActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        dialog.setNegativeListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode != RESULT_OK) {
            return;
        }


        switch (requestCode) {

            case 1:
                getUserInfo();
                break;

        }


    }


    @Override
    public void onMultiClick(View v) {
        Intent intent;
        switch (v.getId()){
            case R.id.nick_name_layout:
                intent = new Intent(this, NickNameActivity.class);
                intent.putExtra("nickName",mNickname);
                startActivityForResult(intent,1);
                break;
            case R.id.phone_layout:
                intent = new Intent(this, ChangePhoneActivity.class);
                intent.putExtra("phone",mPhone);
                startActivityForResult(intent,1);
                break;
            case R.id.exit_login_btn:
                showExitConfirmDialog();
                break;

        }
    }

}
