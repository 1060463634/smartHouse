package com.qqcs.smartHouse.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.qqcs.smartHouse.R;
import com.qqcs.smartHouse.adapter.FillInfoAdapter;
import com.qqcs.smartHouse.adapter.HomeManageAdapter;
import com.qqcs.smartHouse.adapter.MemberManageAdapter;
import com.qqcs.smartHouse.application.Constants;
import com.qqcs.smartHouse.application.SP_Constants;
import com.qqcs.smartHouse.models.FamilyInfoBean;
import com.qqcs.smartHouse.models.FamilyMemberBean;
import com.qqcs.smartHouse.network.CommonJsonList;
import com.qqcs.smartHouse.network.MyStringCallback;
import com.qqcs.smartHouse.utils.ActivityManagerUtil;
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


public class MemberManageActivity extends BaseActivity implements View.OnClickListener{


    @BindView(R.id.list_view)
    ListView mListView;

    private ArrayList<FamilyMemberBean> mDataSource = new ArrayList<>();
    private MemberManageAdapter mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_manage);
        ButterKnife.bind(this);
        setTitleName("成员管理");
        setOptionsButtonVisible();
        setOptionsButtonListener(this);
        ImageView moreImg = findViewById(R.id.more_img);
        moreImg.setImageResource(R.drawable.ic_add_member);
        initView();

    }


    private void initView(){
        getData();

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MemberManageActivity.this, MemberDetailActivity.class);
                intent.putExtra("phone",mDataSource.get(position).getMobile());
                intent.putExtra("nickName",mDataSource.get(position).getNickName());
                intent.putExtra("remarkName",mDataSource.get(position).getRemarkName());
                intent.putExtra("userRole",mDataSource.get(position).getUserRole());
                intent.putExtra("memberId",mDataSource.get(position).getMemberId());

                startActivityForResult(intent,1);
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
                .url(Constants.HTTP_LIST_FAMILY)
                .addHeader("access-token",accessToken)
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .content(object.toString())
                .build()
                .execute(new MyStringCallback<FamilyMemberBean>(this, FamilyMemberBean.class, false, true) {

                    @Override
                    public void onSuccess(CommonJsonList<FamilyMemberBean> json) {
                        mDataSource.clear();
                        mDataSource.addAll(json.getData());
                        setMyAdapter();
                    }

                    @Override
                    public void onFailure(String message) {
                        ToastUtil.showToast(MemberManageActivity.this, message);
                    }
                });
    }

    public void setMyAdapter() {
        if (mAdapter == null) {
            mAdapter = new MemberManageAdapter(this, mDataSource);
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
