package com.qqcs.smartHouse.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.qqcs.smartHouse.R;
import com.qqcs.smartHouse.adapter.HomeManageAdapter;
import com.qqcs.smartHouse.application.Constants;
import com.qqcs.smartHouse.application.SP_Constants;
import com.qqcs.smartHouse.models.FamilyInfoBean;
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


public class HomeManageActivity extends BaseActivity implements View.OnClickListener{


    public static final int REQUEST_CREATE_FAMILY = 101;

    @BindView(R.id.list_view)
    ListView mListView;

    private HomeManageAdapter mAdapter;
    private ArrayList<FamilyInfoBean> mDataSource = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_manage);
        ButterKnife.bind(this);
        setTitle(R.string.home_manage_title);
        setOptionsButtonVisible();
        setOptionsButtonListener(this);
        initView();

    }


    private void initView(){
        getData();

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(HomeManageActivity.this, HomeDetailActivity.class);
                intent.putExtra("familyId",mDataSource.get(position).getFamilyId());
                intent.putExtra("familyName",mDataSource.get(position).getFamilyName());
                intent.putExtra("address",mDataSource.get(position).getAddress());
                intent.putExtra("userRole",mDataSource.get(position).getUserRole());
                intent.putExtra("familyImg",mDataSource.get(position).getImg());
                startActivityForResult(intent,REQUEST_CREATE_FAMILY);
            }
        });

    }

    private void getData(){
        String accessToken = (String) SharePreferenceUtil.
                get(this, SP_Constants.ACCESS_TOKEN,"");
        String currentFamilyId = (String) SharePreferenceUtil.
                get(this, SP_Constants.CURRENT_FAMILY_ID,"");
        String timestamp = System.currentTimeMillis() + "";
        JSONObject object = CommonUtil.getRequstJson(getApplicationContext());
        JSONObject dataObject = new JSONObject();

        try {
            dataObject.put("familyId", currentFamilyId);
            dataObject.put("timestamp", timestamp);

            object.put("data", dataObject);
            object.put("sign", MD5Utils.md5s(currentFamilyId + timestamp));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        OkHttpUtils
                .postString()
                .tag(this)
                .url(Constants.HTTP_GET_FAMILY_LIST)
                .addHeader("access-token",accessToken)
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .content(object.toString())
                .build()
                .execute(new MyStringCallback<FamilyInfoBean>(this, FamilyInfoBean.class, false, true) {

                    @Override
                    public void onSuccess(CommonJsonList<FamilyInfoBean> json) {
                        mDataSource.clear();
                        mDataSource.addAll(json.getData());
                        setMyAdapter();

                        if(json.getData().size() == 0){
                            ToastUtil.showToast(HomeManageActivity.this, "暂无家庭信息");
                            SharePreferenceUtil.put(HomeManageActivity.this,
                                    SP_Constants.HAS_FAMILY, false);
                            SharePreferenceUtil.put(HomeManageActivity.this,
                                    SP_Constants.CURRENT_FAMILY_ID, "");


                        }else {

                        }
                    }

                    @Override
                    public void onFailure(String message) {
                        ToastUtil.showToast(HomeManageActivity.this, message);
                    }
                });
    }

    public void setMyAdapter() {
        if (mAdapter == null) {
            mAdapter = new HomeManageAdapter(this, mDataSource);
            mListView.setAdapter(mAdapter);
        } else {
            mAdapter.refreshData(mDataSource);
        }

    }





    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()){
            case R.id.more_img:
                intent = new Intent(this, WelcomeHomeActivity.class);
                intent.putExtra("fromLogin",false);
                startActivityForResult(intent,REQUEST_CREATE_FAMILY);

                break;

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode != RESULT_OK) {
            return;
        }


        switch (requestCode) {

            case REQUEST_CREATE_FAMILY:
                getData();
                break;

        }


    }

}
