package com.qqcs.smartHouse.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.qqcs.smartHouse.R;
import com.qqcs.smartHouse.adapter.MemberManageAdapter;
import com.qqcs.smartHouse.adapter.RoomManageAdapter;
import com.qqcs.smartHouse.application.Constants;
import com.qqcs.smartHouse.application.SP_Constants;
import com.qqcs.smartHouse.models.FamilyMemberBean;
import com.qqcs.smartHouse.models.RoomBean;
import com.qqcs.smartHouse.models.RoomListBean;
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


public class RoomManageActivity extends BaseActivity{


    @BindView(R.id.list_view)
    ListView mListView;

    @BindView(R.id.save_tv)
    TextView mSaveTv;

    private ArrayList<RoomBean> mDataSource = new ArrayList<>();
    private RoomManageAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_manage);
        ButterKnife.bind(this);
        setTitleName("房间管理");
        mSaveTv.setText("添加");
        initView();

    }


    private void initView(){

        getData();

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {



            }
        });

    }

    private void getData() {
        String accessToken = (String) SharePreferenceUtil.
                get(this, SP_Constants.ACCESS_TOKEN,"");

        String familyId = (String) SharePreferenceUtil.
                get(this, SP_Constants.CURRENT_FAMILY_ID,"");

        String timestamp = System.currentTimeMillis() + "";
        JSONObject object = CommonUtil.getRequstJson(getApplicationContext());
        JSONObject dataObject = new JSONObject();

        try {
            dataObject.put("familyId", familyId);
            dataObject.put("timestamp", timestamp);

            object.put("data", dataObject);
            object.put("sign", MD5Utils.md5s(familyId + timestamp));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        OkHttpUtils
                .postString()
                .tag(this)
                .url(Constants.HTTP_GET_ROOM_LIST)
                .addHeader("access-token",accessToken)
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .content(object.toString())
                .build()
                .execute(new MyStringCallback<RoomListBean>(this, RoomListBean.class, false, false) {

                    @Override
                    public void onSuccess(RoomListBean json) {
                        mDataSource.clear();
                        mDataSource.addAll(json.getRooms());
                        setMyAdapter();
                    }

                    @Override
                    public void onFailure(String message) {
                        ToastUtil.showToast(RoomManageActivity.this, message);
                    }
                });
    }

    public void setMyAdapter() {
        if (mAdapter == null) {
            mAdapter = new RoomManageAdapter(this, mDataSource);
            mListView.setAdapter(mAdapter);
        } else {
            mAdapter.refreshData(mDataSource);
        }

    }

    @Override
    public void onMultiClick(View v) {
        Intent intent;
        switch (v.getId()){
            case R.id.more_img:
                intent = new Intent(this, AddMemberActivity.class);

                startActivityForResult(intent,1);

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
                getData();
                break;

        }


    }

}
