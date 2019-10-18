package com.qqcs.smartHouse.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qqcs.smartHouse.R;
import com.qqcs.smartHouse.application.Constants;
import com.qqcs.smartHouse.application.SP_Constants;
import com.qqcs.smartHouse.fragment.HomeFragment;
import com.qqcs.smartHouse.models.EventBusBean;
import com.qqcs.smartHouse.models.FamilyInfoBean;
import com.qqcs.smartHouse.models.LoginBean;
import com.qqcs.smartHouse.network.CommonJsonList;
import com.qqcs.smartHouse.network.MyStringCallback;
import com.qqcs.smartHouse.utils.AESUtils;
import com.qqcs.smartHouse.utils.CommonUtil;
import com.qqcs.smartHouse.utils.ImageLoaderUtil;
import com.qqcs.smartHouse.utils.MD5Utils;
import com.qqcs.smartHouse.utils.PhotoUtils;
import com.qqcs.smartHouse.utils.SharePreferenceUtil;
import com.qqcs.smartHouse.utils.ToastUtil;
import com.yzq.zxinglibrary.android.CaptureActivity;
import com.yzq.zxinglibrary.bean.ZxingConfig;
import com.yzq.zxinglibrary.common.Constant;
import com.zhy.http.okhttp.OkHttpUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.MediaType;


public class WelcomeHomeActivity extends BaseActivity{


    @BindView(R.id.back_img)
    ImageView mBackImg;

    @BindView(R.id.new_layout)
    LinearLayout mNewLayout;

    @BindView(R.id.old_layout)
    LinearLayout mOldLayout;

    @BindView(R.id.new_date_tv)
    TextView mNewDateTv;

    @BindView(R.id.old_date_tv)
    TextView mOldDateTv;

    @BindView(R.id.create_home_btn)
    Button mCreateHomeBtn;

    @BindView(R.id.enter_home_btn)
    Button mEnterHomeBtn;

    boolean fromLogin = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_home);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        initView();

    }


    private void initView() {
        fromLogin = getIntent().getBooleanExtra("fromLogin",true);

        Calendar calendar = Calendar.getInstance();
        String date = calendar.get(Calendar.YEAR) + ",  "
                + (calendar.get(Calendar.MONTH) + 1) + "月";
        mNewDateTv.setText(date);
        mOldDateTv.setText(date);
        mCreateHomeBtn.setOnClickListener(this);
        mEnterHomeBtn.setOnClickListener(this);
        mBackImg.setOnClickListener(this);

        if(!fromLogin){
            mBackImg.setVisibility(View.VISIBLE);
            mNewLayout.setVisibility(View.GONE);
            mOldLayout.setVisibility(View.VISIBLE);
        }

    }


    @Override
    public void onMultiClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.create_home_btn:
                intent = new Intent(this, CreateHomeActivity.class);
                intent.putExtra("fromLogin",fromLogin);
                startActivityForResult(intent,Constants.REQUEST_CREATE_FAMILY);
                break;
            case R.id.enter_home_btn:
                intent = new Intent(this, CaptureActivity.class);
                ZxingConfig config = new ZxingConfig();
                config.setPlayBeep(true);//是否播放扫描声音 默认为true
                config.setShake(true);//是否震动  默认为true
                config.setDecodeBarCode(true);//是否扫描条形码 默认为true
                config.setFullScreenScan(false);//是否全屏扫描  默认为true  设为false则只会在扫描框中扫描
                intent.putExtra(Constant.INTENT_ZXING_CONFIG, config);
                startActivityForResult(intent, Constants.REQUEST_CODE_SCAN);
                break;
            case R.id.back_img:
                finish();

        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventBus(EventBusBean event) {

        switch (event.getType()) {

            case EventBusBean.ACCEPT_BY_HOST:

                if(fromLogin){
                    getFamily();
                }

                break;

        }

    }

    private void getFamily() {
        String accessToken = (String) SharePreferenceUtil.
                get(this, SP_Constants.ACCESS_TOKEN, "");
        String timestamp = System.currentTimeMillis() + "";
        JSONObject object = CommonUtil.getRequstJson(this.getApplicationContext());
        JSONObject dataObject = new JSONObject();

        try {
            dataObject.put("familyId", "0");
            dataObject.put("timestamp", timestamp);

            object.put("data", dataObject);
            object.put("sign", MD5Utils.md5s("0" + timestamp));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        OkHttpUtils
                .postString()
                .tag(this)
                .url(Constants.HTTP_GET_FAMILY_LIST)
                .addHeader("access-token", accessToken)
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .content(object.toString())
                .build()
                .execute(new MyStringCallback<FamilyInfoBean>(this, FamilyInfoBean.class, true, true) {

                    @Override
                    public void onSuccess(CommonJsonList<FamilyInfoBean> json) {
                        String familyId = json.getData().get(0).getFamilyId();
                        if(!TextUtils.isEmpty(familyId)){

                            SharePreferenceUtil.put(WelcomeHomeActivity.this,
                                    SP_Constants.HAS_FAMILY, true);
                            SharePreferenceUtil.put(WelcomeHomeActivity.this,
                                    SP_Constants.CURRENT_FAMILY_ID, familyId);
                            Intent intent = new Intent(WelcomeHomeActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }

                    }

                    @Override
                    public void onFailure(String message) {
                        ToastUtil.showToast(WelcomeHomeActivity.this, message);
                    }
                });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode != RESULT_OK) {
            return;
        }


        switch (requestCode) {

            case Constants.REQUEST_CREATE_FAMILY:
                Intent intent = new Intent();
                setResult(RESULT_OK,intent);
                finish();

                break;
            case Constants.REQUEST_CODE_SCAN:
                String content = data.getStringExtra(Constant.CODED_CONTENT);
                try {
                    String familyId = AESUtils.decrypt(content,
                            Constants.PASSWORD_ENCRYPT_SEED, AESUtils.MODE_BASE64);
                    intent = new Intent(this,QrHomeDetailActivity.class);
                    intent.putExtra("familyId",familyId);
                    startActivity(intent);
                    finish();

                } catch (Exception e) {
                    e.printStackTrace();
                    ToastUtil.showToast(this,"解码失败，不是家庭二维码");
                }

                break;

        }


    }

}
