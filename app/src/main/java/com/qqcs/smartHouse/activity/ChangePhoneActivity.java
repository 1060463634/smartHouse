package com.qqcs.smartHouse.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.qqcs.smartHouse.R;
import com.qqcs.smartHouse.application.Constants;
import com.qqcs.smartHouse.application.SP_Constants;
import com.qqcs.smartHouse.network.MyStringCallback;
import com.qqcs.smartHouse.utils.AESUtils;
import com.qqcs.smartHouse.utils.ActivityManagerUtil;
import com.qqcs.smartHouse.utils.CommonUtil;
import com.qqcs.smartHouse.utils.MD5Utils;
import com.qqcs.smartHouse.utils.SharePreferenceUtil;
import com.qqcs.smartHouse.utils.ToastUtil;
import com.zhy.http.okhttp.OkHttpUtils;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.MediaType;


public class ChangePhoneActivity extends BaseActivity{


    @BindView(R.id.save_btn)
    Button mSaveBtn;

    @BindView(R.id.old_phone_tv)
    TextView mOldPhoneTv;

    @BindView(R.id.ver_code_edt)
    EditText mVerCodeEdt;

    @BindView(R.id.new_phone_edt)
    EditText mNewPhoneEdt;

    @BindView(R.id.confirm_phone_edt)
    EditText mConfirmPhoneEdt;

    @BindView(R.id.ver_code_btn)
    Button mVerCodeBtn;

    private TimeCount mTimeCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_phone);
        ButterKnife.bind(this);
        setTitleName("修改手机号码");
        initView();

    }


    private void initView(){
        String phone = getIntent().getStringExtra("phone");
        mOldPhoneTv.setText(phone);

        mSaveBtn.setOnClickListener(this);
        mVerCodeBtn.setOnClickListener(this);

    }

    private void sendCode(String phone){
        if (TextUtils.isEmpty(phone)) {
            ToastUtil.showToast(this, "当前手机号码为空");
            return;
        }

        String encryptPhone = AESUtils.encrypt(phone,
                Constants.PASSWORD_ENCRYPT_SEED, AESUtils.MODE_BASE64);

        String accessToken = (String) SharePreferenceUtil.
                get(this, SP_Constants.ACCESS_TOKEN,"");
        String uid = (String) SharePreferenceUtil.
                get(this, SP_Constants.USER_ID,"");
        String timestamp = System.currentTimeMillis() + "";
        JSONObject object = CommonUtil.getRequstJson(getApplicationContext());
        JSONObject dataObject = new JSONObject();

        try {
            dataObject.put("uid", uid);
            dataObject.put("mobile", encryptPhone);
            dataObject.put("timestamp", timestamp);
            object.put("data", dataObject);
            object.put("sign", MD5Utils.md5s(uid + encryptPhone + timestamp));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        OkHttpUtils
                .postString()
                .tag(this)
                .url(Constants.HTTP_SEND_CHANGE_PHONE_CODE)
                .addHeader("access-token",accessToken)
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .content(object.toString())
                .build()
                .execute(new MyStringCallback<Object>(this, Object.class, true, false) {

                    @Override
                    public void onSuccess(Object data) {
                        ToastUtil.showToast(ChangePhoneActivity.this, "验证码发送成功,请注意查收");
                        mTimeCount = new TimeCount(60000, 1000);
                        mTimeCount.start();
                        mVerCodeBtn.setEnabled(false);
                        mVerCodeBtn.setTextColor(getResources().getColor(R.color.text_gray));
                    }

                    @Override
                    public void onFailure(String message) {
                        ToastUtil.showToast(ChangePhoneActivity.this, message);
                    }
                });
    }

    private void changePhone(String code,String newPhone,String confirmPhone){

        if (TextUtils.isEmpty(code)) {
            ToastUtil.showToast(this, "请输入短信验证码");
            return;
        }
        if (TextUtils.isEmpty(newPhone)) {
            ToastUtil.showToast(this, "请输入新手机号");
            return;
        }
        if (!CommonUtil.checkMobile(newPhone)) {
            ToastUtil.showToast(this, "新手机号格式不正确");
            return;
        }
        if (TextUtils.isEmpty(confirmPhone)) {
            ToastUtil.showToast(this, "请再次输入新手机号");
            return;
        }
        if (!newPhone.equalsIgnoreCase(confirmPhone)) {
            ToastUtil.showToast(this, "两次输入的手机号不一致");
            return;
        }


        String accessToken = (String) SharePreferenceUtil.
                get(this, SP_Constants.ACCESS_TOKEN,"");
        String uid = (String) SharePreferenceUtil.
                get(this, SP_Constants.USER_ID,"");
        String timestamp = System.currentTimeMillis() + "";
        JSONObject object = CommonUtil.getRequstJson(getApplicationContext());
        JSONObject dataObject = new JSONObject();

        try {
            dataObject.put("mobile", newPhone);
            dataObject.put("code", code);
            dataObject.put("uid", uid);
            dataObject.put("timestamp", timestamp);
            object.put("data", dataObject);
            object.put("sign", MD5Utils.md5s(newPhone + code + uid + timestamp));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        OkHttpUtils
                .postString()
                .tag(this)
                .url(Constants.HTTP_CHANGE_PHONE)
                .addHeader("access-token",accessToken)
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .content(object.toString())
                .build()
                .execute(new MyStringCallback<Object>(this, Object.class, true, false) {

                    @Override
                    public void onSuccess(Object data) {
                        ToastUtil.showToast(ChangePhoneActivity.this, "更换手机操作成功！");
                        SharePreferenceUtil.clear(ChangePhoneActivity.this);
                        ActivityManagerUtil.getInstance().finishAllActivity();
                        Intent intent = new Intent(ChangePhoneActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onFailure(String message) {
                        ToastUtil.showToast(ChangePhoneActivity.this, message);
                    }
                });
    }


    @Override
    public void onMultiClick(View v) {
        switch (v.getId()){
            case R.id.save_btn:
                changePhone(mVerCodeEdt.getText().toString(),
                        mNewPhoneEdt.getText().toString(),mConfirmPhoneEdt.getText().toString());
                break;
            case R.id.ver_code_btn:
                sendCode(mOldPhoneTv.getText().toString());
                break;

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
