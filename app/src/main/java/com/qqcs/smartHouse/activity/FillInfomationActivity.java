package com.qqcs.smartHouse.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import com.qqcs.smartHouse.R;
import com.qqcs.smartHouse.adapter.FillInfoAdapter;
import com.qqcs.smartHouse.application.Constants;
import com.qqcs.smartHouse.application.SP_Constants;
import com.qqcs.smartHouse.models.EmptyBean;
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
import okhttp3.MediaType;


public class FillInfomationActivity extends BaseActivity{


    @BindView(R.id.phone_edt)
    EditText mPhoneEdt;

    @BindView(R.id.add_member_img)
    ImageView mAddMemberImg;

    @BindView(R.id.list_view)
    ListView mListView;

    @BindView(R.id.ok_btn)
    Button mOkBtn;


    private ArrayList<FamilyMemberBean> mDataSource = new ArrayList<>();
    private FillInfoAdapter mAdapter;
    private String mFamilyId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fill_info);
        ButterKnife.bind(this);
        setTitle(R.string.fill_info_title);
        mFamilyId = getIntent().getStringExtra("familyId");
        initView();

    }

    private void initView() {
        mOkBtn.setOnClickListener(this);
        mAddMemberImg.setOnClickListener(this);
        getData();

    }

    private void getData() {
        String accessToken = (String) SharePreferenceUtil.
                get(this, SP_Constants.ACCESS_TOKEN,"");
        String timestamp = System.currentTimeMillis() + "";
        JSONObject object = CommonUtil.getRequstJson(getApplicationContext());
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
                .url(Constants.HTTP_LIST_FAMILY)
                .addHeader("access-token",accessToken)
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .content(object.toString())
                .build()
                .execute(new MyStringCallback<FamilyMemberBean>(this, FamilyMemberBean.class, true, true) {

                    @Override
                    public void onSuccess(CommonJsonList<FamilyMemberBean> json) {
                        mDataSource.clear();
                        mDataSource.addAll(json.getData());
                        setMyAdapter();
                    }

                    @Override
                    public void onFailure(String message) {
                        ToastUtil.showToast(FillInfomationActivity.this, message);
                    }
                });
    }

    private void addFamilyMember(String phone) {

        if(TextUtils.isEmpty(phone)){
            ToastUtil.showToast(this, "请输入家庭成员手机号");
            return;
        }
        if (!CommonUtil.checkMobile(phone)) {
            ToastUtil.showToast(this, "请输入正确的手机号");
            return;
        }

        String accessToken = (String) SharePreferenceUtil.
                get(this, SP_Constants.ACCESS_TOKEN,"");
        String timestamp = System.currentTimeMillis() + "";
        JSONObject object = CommonUtil.getRequstJson(getApplicationContext());
        JSONObject dataObject = new JSONObject();

        try {
            dataObject.put("mobile", phone);
            dataObject.put("userRole", "2");
            dataObject.put("familyId", mFamilyId);
            dataObject.put("remarkName", "");
            dataObject.put("timestamp", timestamp);

            object.put("data", dataObject);
            object.put("sign", MD5Utils.md5s(phone + "2" + mFamilyId + timestamp));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        OkHttpUtils
                .postString()
                .tag(this)
                .addHeader("access-token",accessToken)
                .url(Constants.HTTP_ADD_FAMILY_MEMBER)
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .content(object.toString())
                .build()
                .execute(new MyStringCallback<Object>(this, Object.class, true, false) {

                    @Override
                    public void onSuccess(Object json) {
                        ToastUtil.showToast(FillInfomationActivity.this, "添加新成员成功！");
                        getData();
                    }

                    @Override
                    public void onFailure(String message) {
                        ToastUtil.showToast(FillInfomationActivity.this, message);
                    }
                });
    }

    public void deleteFamilyMember(String memberId) {

        String accessToken = (String) SharePreferenceUtil.
                get(this, SP_Constants.ACCESS_TOKEN,"");

        String timestamp = System.currentTimeMillis() + "";
        JSONObject object = CommonUtil.getRequstJson(getApplicationContext());
        JSONObject dataObject = new JSONObject();

        try {
            dataObject.put("memberId", memberId);
            dataObject.put("timestamp", timestamp);

            object.put("data", dataObject);
            object.put("sign", MD5Utils.md5s(memberId + timestamp));

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
                        ToastUtil.showToast(FillInfomationActivity.this, "删除成功！");
                        getData();
                    }

                    @Override
                    public void onFailure(String message) {
                        ToastUtil.showToast(FillInfomationActivity.this, message);
                    }
                });
    }



    public void setMyAdapter() {
        if (mAdapter == null) {
            mAdapter = new FillInfoAdapter(this, mDataSource);
            mListView.setAdapter(mAdapter);
        } else {
            mAdapter.refreshData(mDataSource);
        }

    }


    @Override
    public void onMultiClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.add_member_img:
                addFamilyMember(mPhoneEdt.getText().toString());
                break;
            case R.id.ok_btn:
                intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                finish();

        }
    }

}
