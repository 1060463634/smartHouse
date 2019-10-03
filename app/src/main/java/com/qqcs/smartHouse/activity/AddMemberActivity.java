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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.qqcs.smartHouse.R;
import com.qqcs.smartHouse.adapter.FillInfoAdapter;
import com.qqcs.smartHouse.application.Constants;
import com.qqcs.smartHouse.application.SP_Constants;
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


public class AddMemberActivity extends BaseActivity implements View.OnClickListener {


    @BindView(R.id.phone_edt)
    EditText mPhoneEdt;

    @BindView(R.id.tip_name_edt)
    EditText mTipNameEdt;

    @BindView(R.id.open_contact_btn)
    Button mOpenContactBtn;

    @BindView(R.id.save_tv)
    TextView mSaveBtn;

    @BindView(R.id.role_spinner)
    Spinner mRoleSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_member);
        ButterKnife.bind(this);
        setTitleName("新增成员");
        initView();

    }

    private void initView() {
        mOpenContactBtn.setOnClickListener(this);
        mSaveBtn.setOnClickListener(this);
        String[] mItems = {"普通成员","管理员"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>
                (this,R.layout.item_spinner, mItems);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //getData();
        mRoleSpinner.setAdapter(adapter);
    }


    private void addFamilyMember(String phone,String name,String role) {

        if(TextUtils.isEmpty(phone)){
            ToastUtil.showToast(this, "请输入手机号");
            return;
        }
        if (!CommonUtil.checkMobile(phone)) {
            ToastUtil.showToast(this, "请输入正确的手机号");
            return;
        }
        if(TextUtils.isEmpty(name)){
            ToastUtil.showToast(this, "请输入备注名");
            return;
        }

        String accessToken = (String) SharePreferenceUtil.
                get(this, SP_Constants.ACCESS_TOKEN,"");
        String familyId = (String) SharePreferenceUtil.
                get(this, SP_Constants.CURRENT_FAMILY_ID,"");
        String timestamp = System.currentTimeMillis() + "";
        JSONObject object = CommonUtil.getRequstJson(getApplicationContext());
        JSONObject dataObject = new JSONObject();

        try {
            dataObject.put("mobile", phone);
            dataObject.put("userRole", role);
            dataObject.put("familyId", familyId);
            dataObject.put("remarkName", name);
            dataObject.put("timestamp", timestamp);

            object.put("data", dataObject);
            object.put("sign", MD5Utils.md5s(
                    phone + role + familyId + name + timestamp));

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
                        ToastUtil.showToast(AddMemberActivity.this, "添加新成员成功！");
                        setResult(RESULT_OK);
                        finish();
                    }

                    @Override
                    public void onFailure(String message) {
                        ToastUtil.showToast(AddMemberActivity.this, message);
                    }
                });
    }




    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.open_contact_btn:
                intent = new Intent();
                intent.setAction("android.intent.action.PICK");
                intent.addCategory("android.intent.category.DEFAULT");
                intent.setType("vnd.android.cursor.dir/phone_v2");
                startActivityForResult(intent, 1);
                break;
            case R.id.save_tv:
                addFamilyMember(mPhoneEdt.getText().toString()
                        ,mTipNameEdt.getText().toString(),
                        mRoleSpinner.getSelectedItemPosition() == 0 ? "2" : "1");
                break;

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode != RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case 1:
                if (data != null) {
                    Uri uri = data.getData();
                    String phoneNum = null;
                    String contactName = null;
                    // 创建内容解析者
                    ContentResolver contentResolver = getContentResolver();
                    Cursor cursor = null;
                    if (uri != null) {
                        cursor = contentResolver.query(uri,
                                new String[]{"display_name","data1"}, null, null, null);
                    }
                    while (cursor.moveToNext()) {
                        contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                        phoneNum = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    }
                    cursor.close();
                    //  把电话号码中的  -  符号 替换成空格
                    if (phoneNum != null) {
                        phoneNum = phoneNum.replaceAll("-", " ");
                        // 空格去掉  为什么不直接-替换成"" 因为测试的时候发现还是会有空格 只能这么处理
                        phoneNum= phoneNum.replaceAll(" ", "");
                    }

                    mPhoneEdt.setText(phoneNum);
                    mTipNameEdt.setText(contactName);
                }

                break;

        }


    }

}
