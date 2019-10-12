package com.qqcs.smartHouse.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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


public class RoomDetailActivity extends BaseActivity{


    @BindView(R.id.delete_btn)
    Button mDeleteBtn;

    @BindView(R.id.save_tv)
    TextView mSaveTv;

    @BindView(R.id.room_name_edt)
    EditText mRoomNameEdt;

    private String mRoomId;
    private String mRoomName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_detail);
        ButterKnife.bind(this);
        setTitleName("房间详情");
        initView();

    }


    private void initView(){
        mRoomId = getIntent().getStringExtra("roomId");
        mRoomName = getIntent().getStringExtra("roomName");
        mRoomNameEdt.setText(mRoomName);
        mRoomNameEdt.setSelection(mRoomNameEdt.getText().length());

        mDeleteBtn.setOnClickListener(this);
        mSaveTv.setOnClickListener(this);

    }

    private void saveRoomInfo(String roomName){
        if (TextUtils.isEmpty(roomName)) {
            ToastUtil.showToast(this, "请输入房间名称");
            return;
        }

        String accessToken = (String) SharePreferenceUtil.
                get(this, SP_Constants.ACCESS_TOKEN,"");
        String timestamp = System.currentTimeMillis() + "";
        JSONObject object = CommonUtil.getRequstJson(getApplicationContext());
        JSONObject dataObject = new JSONObject();

        try {
            dataObject.put("roomId", mRoomId);
            dataObject.put("name", roomName);
            dataObject.put("timestamp", timestamp);
            object.put("data", dataObject);
            object.put("sign", MD5Utils.md5s(mRoomId + roomName + timestamp));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        OkHttpUtils
                .postString()
                .tag(this)
                .url(Constants.HTTP_UPDATE_ROOM)
                .addHeader("access-token",accessToken)
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .content(object.toString())
                .build()
                .execute(new MyStringCallback<Object>(this, Object.class, true, false) {

                    @Override
                    public void onSuccess(Object data) {
                        ToastUtil.showToast(RoomDetailActivity.this, "修改房间信息成功!");
                        Intent intent = new Intent();
                        setResult(RESULT_OK,intent);
                        finish();
                    }

                    @Override
                    public void onFailure(String message) {
                        ToastUtil.showToast(RoomDetailActivity.this, message);
                    }
                });
    }

    private void deleteRoom(){

        String accessToken = (String) SharePreferenceUtil.
                get(this, SP_Constants.ACCESS_TOKEN,"");
        String timestamp = System.currentTimeMillis() + "";
        JSONObject object = CommonUtil.getRequstJson(getApplicationContext());
        JSONObject dataObject = new JSONObject();

        try {
            dataObject.put("roomId", mRoomId);
            dataObject.put("timestamp", timestamp);
            object.put("data", dataObject);
            object.put("sign", MD5Utils.md5s(mRoomId + timestamp));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        OkHttpUtils
                .postString()
                .tag(this)
                .url(Constants.HTTP_DELETE_ROOM)
                .addHeader("access-token",accessToken)
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .content(object.toString())
                .build()
                .execute(new MyStringCallback<Object>(this, Object.class, true, false) {

                    @Override
                    public void onSuccess(Object data) {
                        ToastUtil.showToast(RoomDetailActivity.this, "删除房间成功!");
                        Intent intent = new Intent();
                        setResult(RESULT_OK,intent);
                        finish();
                    }

                    @Override
                    public void onFailure(String message) {
                        ToastUtil.showToast(RoomDetailActivity.this, message);
                    }
                });
    }


    private void showConfirmDialog(){
        final MyAlertDialog dialog = new MyAlertDialog(this);
        dialog.show();
        dialog.setTitle("警告");
        dialog.setText("是否确认删除此房间，此操作不可逆");


        dialog.setPositiveListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                deleteRoom();

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
    public void onMultiClick(View v) {
        switch (v.getId()){
            case R.id.save_tv:
                saveRoomInfo(mRoomNameEdt.getText().toString());
                break;
            case R.id.delete_btn:
                showConfirmDialog();
                break;


        }
    }

}
