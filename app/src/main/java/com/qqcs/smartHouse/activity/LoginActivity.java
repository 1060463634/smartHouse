package com.qqcs.smartHouse.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.qqcs.smartHouse.R;
import com.qqcs.smartHouse.application.Constants;
import com.qqcs.smartHouse.application.SP_Constants;
import com.qqcs.smartHouse.models.LoginBean;
import com.qqcs.smartHouse.network.MyStringCallback;
import com.qqcs.smartHouse.utils.AESUtils;
import com.qqcs.smartHouse.utils.CommonUtil;
import com.qqcs.smartHouse.utils.LogUtil;
import com.qqcs.smartHouse.utils.MD5Utils;
import com.qqcs.smartHouse.utils.SharePreferenceUtil;
import com.qqcs.smartHouse.utils.ToastUtil;
import com.zhy.http.okhttp.OkHttpUtils;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.MediaType;


public class LoginActivity extends BaseActivity{

    @BindView(R.id.phone_layout)
    LinearLayout mPhoneLayout;

    @BindView(R.id.ver_code_layout)
    LinearLayout mVerCodeLayout;

    @BindView(R.id.service_agreement_layout)
    LinearLayout mServiceAgreementLayout;

    @BindView(R.id.secret_agreement_layout)
    LinearLayout mSecretAgreementLayout;

    @BindView(R.id.next_btn)
    Button mNextBtn;

    @BindView(R.id.login_register_btn)
    Button mLoginRegisterBtn;

    @BindView(R.id.phone_edt)
    EditText mPhoneEdt;

    @BindView(R.id.ver_code_edt)
    EditText mVerCodeEdt;

    @BindView(R.id.back_img)
    ImageView mBackImg;

    @BindView(R.id.service_agreement_cbx)
    CheckBox mServiceAgreementCbx;

    @BindView(R.id.secret_agreement_cbx)
    CheckBox mSecretAgreementCbx;

    @BindView(R.id.service_agreement_tv)
    TextView mServiceAgreementTv;

    @BindView(R.id.secret_agreement_tv)
    TextView mSecretAgreementTv;

    @BindView(R.id.ver_code_btn)
    Button mVerCodeBtn;

    private TimeCount mTimeCount;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        initView();

    }


    private void initView() {
        String account = (String) SharePreferenceUtil.get(this, SP_Constants.LOGIN_ACCOUNT, "");

        mPhoneEdt.setText(account);
        mNextBtn.setOnClickListener(this);
        mLoginRegisterBtn.setOnClickListener(this);
        mBackImg.setOnClickListener(this);
        mServiceAgreementTv.setOnClickListener(this);
        mSecretAgreementTv.setOnClickListener(this);
        mVerCodeBtn.setOnClickListener(this);
    }


    private void sendCode(final String account) {

        if (!CommonUtil.checkMobile(account)) {
            ToastUtil.showToast(this, "请输入正确的手机号");
            return;
        }
        if (!mServiceAgreementCbx.isChecked()) {
            ToastUtil.showToast(this, "请同意软件件许可及服务协议");
            return;
        }

        String timestamp = System.currentTimeMillis() + "";
        JSONObject object = CommonUtil.getRequstJson(getApplicationContext());
        JSONObject dataObject = new JSONObject();
        try {

            String encryptPhone = AESUtils.encrypt(account,
                    Constants.PASSWORD_ENCRYPT_SEED, AESUtils.MODE_BASE64);
            dataObject.put("mobile", encryptPhone);
            dataObject.put("timestamp", timestamp);
            object.put("data",dataObject);
            object.put("sign", MD5Utils.md5s(encryptPhone + timestamp));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        OkHttpUtils
                .postString()
                .tag(this)
                .url(Constants.HTTP_URL_SEND_CODE)
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .content(object.toString())
                .build()
                .execute(new MyStringCallback<String>(this, String.class, true, false) {
                    @Override
                    public void onSuccess(String data) {

                        ToastUtil.showToast(LoginActivity.this, "短信验证码发送成功!");
                        setStep(2);
                        mTimeCount = new TimeCount(60000, 1000);
                        mTimeCount.start();
                        mVerCodeBtn.setEnabled(false);
                        mVerCodeBtn.setTextColor(getResources().getColor(R.color.text_gray));
                    }

                    @Override
                    public void onFailure(String message) {
                        ToastUtil.showToast(LoginActivity.this, message);
                    }
                });
    }

    private void loginRegister(final String phone,String verCode) {

        if (!CommonUtil.checkMobile(phone)) {
            ToastUtil.showToast(this, "请输入正确的手机号");
            return;
        }
        if (TextUtils.isEmpty(verCode)) {
            ToastUtil.showToast(this, "请输入验证码");
            return;
        }

        if (!mSecretAgreementCbx.isChecked()) {
            ToastUtil.showToast(this, "请同意隐私务协议");
            return;
        }

        String registrationId = (String) SharePreferenceUtil.get(this,
                SP_Constants.REGISTRATION_ID,"");

        String timestamp = System.currentTimeMillis() + "";
        JSONObject object = CommonUtil.getRequstJson(getApplicationContext());
        JSONObject dataObject = new JSONObject();
        try {
            dataObject.put("phone", phone);
            dataObject.put("code", verCode);
            dataObject.put("registrationId", registrationId);
            dataObject.put("deviceToken", "");
            dataObject.put("timestamp", timestamp);

            object.put("data",dataObject);
            object.put("sign", MD5Utils.md5s(phone + verCode  + timestamp));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        OkHttpUtils
                .postString()
                .tag(this)
                .url(Constants.HTTP_URL_LOGIN)
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .content(object.toString())
                .build()
                .execute(new MyStringCallback<LoginBean>(this, LoginBean.class, true, false) {
                    @Override
                    public void onSuccess(LoginBean data) {

                        ToastUtil.showToast(LoginActivity.this, "登录成功!");
                        SharePreferenceUtil.put(LoginActivity.this,
                                SP_Constants.LOGIN_ACCOUNT, phone);
                        SharePreferenceUtil.put(LoginActivity.this,
                                SP_Constants.USER_ID, data.getUid());
                        SharePreferenceUtil.put(LoginActivity.this,
                                SP_Constants.ACCESS_TOKEN, data.getAccessToken());
                        SharePreferenceUtil.put(LoginActivity.this,
                                SP_Constants.NICK_NAME, data.getNickName());
                        SharePreferenceUtil.put(LoginActivity.this,
                                SP_Constants.HAS_FAMILY, data.isHasFamily());


                        if(data.isHasFamily()){
                            SharePreferenceUtil.put(LoginActivity.this,
                                    SP_Constants.CURRENT_FAMILY_ID,
                                    data.getFamilyInfo().get(0).getFamilyId());

                            Intent intent = new Intent(LoginActivity.this,
                                    MainActivity.class);
                            startActivity(intent);
                            finish();

                        }else {
                            Intent intent = new Intent(LoginActivity.this,
                                    WelcomeHomeActivity.class);
                            startActivity(intent);
                            finish();

                        }

                    }

                    @Override
                    public void onFailure(String message) {
                        ToastUtil.showToast(LoginActivity.this, message);
                    }
                });
    }


    @Override
    public void onMultiClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.next_btn:

                sendCode(mPhoneEdt.getText().toString());
                break;
            case R.id.login_register_btn:
                loginRegister(mPhoneEdt.getText().toString(),mVerCodeEdt.getText().toString());

                break;
            case R.id.back_img:
                setStep(1);
                break;
            case R.id.service_agreement_tv:
                intent = new Intent(LoginActivity.this, WebActivity.class);
                intent.putExtra("url","http://www.baidu.com");
                startActivity(intent);

                break;
            case R.id.secret_agreement_tv:
                intent = new Intent(LoginActivity.this, WebActivity.class);
                intent.putExtra("url","http://www.baidu.com");
                startActivity(intent);
                break;
            case R.id.ver_code_btn:
                sendCode(mPhoneEdt.getText().toString());
                break;

        }
    }


    private void setStep(int step) {

        if (step == 1) {
            mPhoneLayout.setVisibility(View.VISIBLE);
            mVerCodeLayout.setVisibility(View.GONE);
            mServiceAgreementLayout.setVisibility(View.VISIBLE);
            mSecretAgreementLayout.setVisibility(View.GONE);
            mBackImg.setVisibility(View.GONE);
            if(mTimeCount != null){
                mTimeCount.cancel();
            }
        } else {
            mPhoneLayout.setVisibility(View.GONE);
            mVerCodeLayout.setVisibility(View.VISIBLE);
            mServiceAgreementLayout.setVisibility(View.GONE);
            mSecretAgreementLayout.setVisibility(View.VISIBLE);
            mBackImg.setVisibility(View.VISIBLE);

        }

    }

    @Override
    public void onBackPressed() {
        if (mBackImg.getVisibility() == View.VISIBLE) {

            setStep(1);
        } else {
            super.onBackPressed();
        }

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mTimeCount != null){
            mTimeCount.cancel();
        }
    }


    /* 定义一个倒计时的内部类 */
    class TimeCount extends CountDownTimer {
        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);//参数依次为总时长,和计时的时间间隔
        }

        @Override
        public void onFinish() {//计时完毕时触发
            mVerCodeBtn.setEnabled(true);
            mVerCodeBtn.setText("重新发送");
            mVerCodeBtn.setTextColor(getResources().getColor(R.color.text_blue));
        }

        @Override
        public void onTick(long millisUntilFinished) {//计时过程显示
            mVerCodeBtn.setText( millisUntilFinished / 1000 + "s");
        }
    }


}
