package com.qqcs.smartHouse.activity;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.qqcs.smartHouse.R;
import com.qqcs.smartHouse.application.Constants;
import com.qqcs.smartHouse.application.SP_Constants;
import com.qqcs.smartHouse.network.MyStringCallback;
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


public class MemberDetailActivity extends BaseActivity{


    @BindView(R.id.nick_name_tv)
    TextView mNickNameTv;

    @BindView(R.id.phone_tv)
    TextView mPhoneTv;

    @BindView(R.id.tip_name_edt)
    EditText mTipNameEdt;

    @BindView(R.id.delete_member_btn)
    Button mDeleteMemberBtn;

    @BindView(R.id.save_tv)
    TextView mSaveBtn;

    @BindView(R.id.role_spinner)
    Spinner mRoleSpinner;

    @BindView(R.id.role_layout)
    RelativeLayout mRoleLayout;

    private String mMemberId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_detail);
        ButterKnife.bind(this);
        setTitleName("成员详情");
        initView();

    }

    private void initView() {
        Intent intent = getIntent();
        String phone = intent.getStringExtra("phone");
        String nickName = intent.getStringExtra("nickName");
        String remarkName = intent.getStringExtra("remarkName");
        String userRole = intent.getStringExtra("userRole");
        mMemberId = intent.getStringExtra("memberId");

        mPhoneTv.setText(phone);
        mNickNameTv.setText(nickName);
        mTipNameEdt.setText(remarkName);
        mTipNameEdt.setSelection(mTipNameEdt.getText().length());

        String[] mItems = {"普通成员","管理员"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>
                (this,android.R.layout.simple_spinner_item, mItems);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mRoleSpinner.setAdapter(adapter);
        if(userRole.equalsIgnoreCase(Constants.ROLE_LOAD)){
            mRoleLayout.setVisibility(View.GONE);
            mDeleteMemberBtn.setVisibility(View.GONE);

        } else if(userRole.equalsIgnoreCase(Constants.ROLE_MANAGE)){
            mRoleSpinner.setSelection(1);
        }else {
            mRoleSpinner.setSelection(0);
        }

        mSaveBtn.setOnClickListener(this);
        mDeleteMemberBtn.setOnClickListener(this);

    }


    private void updateFamilyMember(String name,String role) {

        String accessToken = (String) SharePreferenceUtil.
                get(this, SP_Constants.ACCESS_TOKEN,"");
        String timestamp = System.currentTimeMillis() + "";
        JSONObject object = CommonUtil.getRequstJson(getApplicationContext());
        JSONObject dataObject = new JSONObject();

        try {
            dataObject.put("memberId", mMemberId);
            dataObject.put("remarkName", name);
            dataObject.put("userRole", role);
            dataObject.put("timestamp", timestamp);

            object.put("data", dataObject);
            object.put("sign", MD5Utils.md5s(
                    mMemberId  + name + role + timestamp));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        OkHttpUtils
                .postString()
                .tag(this)
                .addHeader("access-token",accessToken)
                .url(Constants.HTTP_UPDATE_MEMBER)
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .content(object.toString())
                .build()
                .execute(new MyStringCallback<Object>(this, Object.class, true, false) {

                    @Override
                    public void onSuccess(Object json) {
                        ToastUtil.showToast(MemberDetailActivity.this, "保存成功！");
                        setResult(RESULT_OK);
                        finish();
                    }

                    @Override
                    public void onFailure(String message) {
                        ToastUtil.showToast(MemberDetailActivity.this, message);
                    }
                });
    }

    public void deleteFamilyMember() {

        String accessToken = (String) SharePreferenceUtil.
                get(this, SP_Constants.ACCESS_TOKEN,"");

        String timestamp = System.currentTimeMillis() + "";
        JSONObject object = CommonUtil.getRequstJson(getApplicationContext());
        JSONObject dataObject = new JSONObject();

        try {
            dataObject.put("memberId", mMemberId);
            dataObject.put("timestamp", timestamp);

            object.put("data", dataObject);
            object.put("sign", MD5Utils.md5s(mMemberId + timestamp));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        OkHttpUtils
                .postString()
                .tag(this)
                .url(Constants.HTTP_DELETE_FAMILY_MEMBER)
                .addHeader("access-token",accessToken)
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .content(object.toString())
                .build()
                .execute(new MyStringCallback<Object>(this, Object.class, true, false) {

                    @Override
                    public void onSuccess(Object json) {
                        ToastUtil.showToast(MemberDetailActivity.this, "删除成功！");
                        setResult(RESULT_OK);
                        finish();
                    }

                    @Override
                    public void onFailure(String message) {
                        ToastUtil.showToast(MemberDetailActivity.this, message);
                    }
                });
    }

    @Override
    public void onMultiClick(View v) {
        Intent intent;
        switch (v.getId()) {

            case R.id.save_tv:
                String userRole = getIntent().getStringExtra("userRole");
                if(userRole.equalsIgnoreCase(Constants.ROLE_LOAD)){
                    updateFamilyMember(mTipNameEdt.getText().toString(), "0");
                }else {
                    updateFamilyMember(mTipNameEdt.getText().toString(),
                            mRoleSpinner.getSelectedItemPosition() == 0 ? "2" : "1");
                }
                break;

            case R.id.delete_member_btn:
                showConfirmDialog();
                break;


        }
    }

    private void showConfirmDialog(){
        final MyAlertDialog dialog = new MyAlertDialog(this);
        dialog.show();
        dialog.setTitle("确定删除此成员？");
        dialog.setText("此操作不可逆");


        dialog.setPositiveListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                deleteFamilyMember();

            }
        });

        dialog.setNegativeListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });


    }


}
