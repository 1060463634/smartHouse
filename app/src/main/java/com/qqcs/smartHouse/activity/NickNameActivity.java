package com.qqcs.smartHouse.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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


public class NickNameActivity extends BaseActivity{


    @BindView(R.id.save_btn)
    Button mSaveBtn;

    @BindView(R.id.nick_name_edt)
    EditText mNickNameEdt;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nick_name);
        ButterKnife.bind(this);
        setTitleName("昵称");
        initView();

    }


    private void initView(){
        String nickName = getIntent().getStringExtra("nickName");
        mNickNameEdt.setText(nickName);
        mNickNameEdt.setSelection(mNickNameEdt.getText().length());

        mSaveBtn.setOnClickListener(this);

    }

    private void updateUserInfo(String nickName){
        if (TextUtils.isEmpty(nickName)) {
            ToastUtil.showToast(this, "请输入昵称");
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
            dataObject.put("nickname", nickName);
            dataObject.put("uid", uid);
            dataObject.put("timestamp", timestamp);
            object.put("data", dataObject);
            object.put("sign", MD5Utils.md5s(nickName + uid + timestamp));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        OkHttpUtils
                .postString()
                .tag(this)
                .url(Constants.HTTP_UPDATE_USER_INFO)
                .addHeader("access-token",accessToken)
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .content(object.toString())
                .build()
                .execute(new MyStringCallback<Object>(this, Object.class, true, false) {

                    @Override
                    public void onSuccess(Object data) {
                        ToastUtil.showToast(NickNameActivity.this, "修改用户信息成功!");
                        Intent intent = new Intent();
                        setResult(RESULT_OK,intent);
                        finish();
                    }

                    @Override
                    public void onFailure(String message) {
                        ToastUtil.showToast(NickNameActivity.this, message);
                    }
                });
    }





    @Override
    public void onMultiClick(View v) {
        switch (v.getId()){
            case R.id.save_btn:
                updateUserInfo(mNickNameEdt.getText().toString());
                break;

        }
    }

}
