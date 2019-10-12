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
import com.zhy.http.okhttp.OkHttpUtils;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.MediaType;


public class AddRoomActivity extends BaseActivity{


    @BindView(R.id.room_name_edt)
    EditText mRoomNameEdt;

    @BindView(R.id.save_tv)
    TextView mSaveTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_room);
        ButterKnife.bind(this);
        setTitleName("新增房间");
        initView();

    }

    private void initView() {
        mSaveTv.setOnClickListener(this);

    }


    private void saveRoom(String name) {

        if(TextUtils.isEmpty(name)){
            ToastUtil.showToast(this, "请输入房间名称");
            return;
        }


        String accessToken = (String) SharePreferenceUtil.
                get(this, SP_Constants.ACCESS_TOKEN,"");

        String familyId = (String) SharePreferenceUtil.
                get(this, SP_Constants.CURRENT_FAMILY_ID,"");;

        String timestamp = System.currentTimeMillis() + "";
        JSONObject object = CommonUtil.getRequstJson(getApplicationContext());
        JSONObject dataObject = new JSONObject();

        try {
            dataObject.put("roomName", name);
            dataObject.put("familyId", familyId);
            dataObject.put("timestamp", timestamp);

            object.put("data", dataObject);
            object.put("sign", MD5Utils.md5s(
                    name + familyId + timestamp));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        OkHttpUtils
                .postString()
                .tag(this)
                .addHeader("access-token",accessToken)
                .url(Constants.HTTP_ADD_ROOM)
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .content(object.toString())
                .build()
                .execute(new MyStringCallback<Object>(this, Object.class, true, false) {

                    @Override
                    public void onSuccess(Object json) {
                        ToastUtil.showToast(AddRoomActivity.this, "添加房间成功！");
                        setResult(RESULT_OK);
                        finish();
                    }

                    @Override
                    public void onFailure(String message) {
                        ToastUtil.showToast(AddRoomActivity.this, message);
                    }
                });
    }




    @Override
    public void onMultiClick(View v) {
        Intent intent;
        switch (v.getId()) {

            case R.id.save_tv:
                saveRoom(mRoomNameEdt.getText().toString());
                break;

        }
    }


}
