package com.qqcs.smartHouse.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.FileProvider;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.qqcs.smartHouse.R;
import com.qqcs.smartHouse.adapter.FillInfoAdapter;
import com.qqcs.smartHouse.application.Constants;
import com.qqcs.smartHouse.application.SP_Constants;
import com.qqcs.smartHouse.models.FamilyInfoBean;
import com.qqcs.smartHouse.network.CommonJsonList;
import com.qqcs.smartHouse.network.MyStringCallback;
import com.qqcs.smartHouse.utils.CommonUtil;
import com.qqcs.smartHouse.utils.ImageLoaderUtil;
import com.qqcs.smartHouse.utils.MD5Utils;
import com.qqcs.smartHouse.utils.PhotoUtils;
import com.qqcs.smartHouse.utils.SharePreferenceUtil;
import com.qqcs.smartHouse.utils.ToastUtil;
import com.qqcs.smartHouse.widgets.SelectPictureDialog;
import com.zhy.http.okhttp.OkHttpUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.MediaType;

import static com.qqcs.smartHouse.activity.CreateHomeActivity.REQUEST_CAPTURE_PIC;
import static com.qqcs.smartHouse.activity.CreateHomeActivity.REQUEST_CROP_PIC;
import static com.qqcs.smartHouse.activity.CreateHomeActivity.REQUEST_SELECT_DEMO;
import static com.qqcs.smartHouse.activity.CreateHomeActivity.REQUEST_SELECT_PIC;


public class HomeDetailActivity extends BaseActivity implements View.OnClickListener{

    @BindView(R.id.home_img_layout)
    RelativeLayout mHomeImgLayout;

    @BindView(R.id.home_name_layout)
    RelativeLayout mHomeNameLayout;

    @BindView(R.id.home_location_layout)
    RelativeLayout mHomeLocationLayout;

    @BindView(R.id.home_qrcode_layout)
    RelativeLayout mQrcodeLayout;

    @BindView(R.id.home_member_layout)
    RelativeLayout mHomeMemberLayout;

    @BindView(R.id.bind_gateway_layout)
    RelativeLayout mBindGatewayLayout;

    @BindView(R.id.delete_home_layout)
    RelativeLayout mDeleteHomeLayout;

    @BindView(R.id.home_img)
    ImageView mHomeImg;

    @BindView(R.id.home_name_tv)
    TextView mHomeNameTV;

    @BindView(R.id.home_address_tv)
    TextView mHomeAddressTV;

    @BindView(R.id.delete_tv)
    TextView mDeleteTV;

    private String familyId;
    private String familyName;
    private String address;
    private String userRole;
    private String familyImg;

    private File mFile;
    private Uri mUri;
    private File mDestFile;
    private int mCurrentPictureId = R.drawable.ic_home_1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_detail);
        ButterKnife.bind(this);
        setTitle(R.string.home_detail_title);
        setOptionsButtonVisible();
        initView();

    }


    private void initView(){

        //获取传入值
        Intent intent = getIntent();
        familyId = intent.getStringExtra("familyId");
        familyName = intent.getStringExtra("familyName");
        address = intent.getStringExtra("address");
        userRole =intent.getStringExtra("userRole");
        familyImg =intent.getStringExtra("familyImg");

        //初始化页面
        ImageLoaderUtil.getInstance().displayImage(
                Constants.HTTP_SERVER_DOMAIN + familyImg,mHomeImg);
        mHomeNameTV.setText(familyName);
        mHomeAddressTV.setText(address);
        mHomeImgLayout.setOnClickListener(this);

        //初始化用户操作权限
        if(userRole.equalsIgnoreCase(Constants.ROLE_MANAGE)){
            mDeleteTV.setText("退出家庭");
            mDeleteHomeLayout.setOnClickListener(this);
        } else if(userRole.equalsIgnoreCase(Constants.ROLE_NORMAL)){
            mDeleteTV.setText("退出家庭");
            mDeleteHomeLayout.setOnClickListener(this);
            mBindGatewayLayout.setVisibility(View.GONE);
            mHomeMemberLayout.setVisibility(View.GONE);
        }else if(userRole.equalsIgnoreCase(Constants.ROLE_LOAD)){
            mDeleteHomeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteFamily();
                }
            });

        }

        //初始化图片文件
        mFile = new File(Constants.FILE_DIR + "homeTake.jpg");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            mUri = FileProvider.getUriForFile(this, "com.qqcs.smartHouse", mFile);

        } else {
            mUri = Uri.fromFile(mFile);
        }

    }


    private void deleteFamily(){
        String accessToken = (String) SharePreferenceUtil.
                get(this, SP_Constants.ACCESS_TOKEN,"");
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
                .url(Constants.HTTP_DELETE_FAMILY)
                .addHeader("access-token",accessToken)
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .content(object.toString())
                .build()
                .execute(new MyStringCallback<Object>(this, Object.class, true, false) {

                    @Override
                    public void onSuccess(Object data) {
                        ToastUtil.showToast(HomeDetailActivity.this, "删除此家庭成功!");
                        Intent intent = new Intent();
                        setResult(RESULT_OK,intent);
                        finish();
                    }


                    @Override
                    public void onFailure(String message) {
                        ToastUtil.showToast(HomeDetailActivity.this, message);
                    }
                });
    }

    private void exitFamily(){
        String accessToken = (String) SharePreferenceUtil.
                get(this, SP_Constants.ACCESS_TOKEN,"");
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
                .url(Constants.HTTP_DELETE_FAMILY)
                .addHeader("access-token",accessToken)
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .content(object.toString())
                .build()
                .execute(new MyStringCallback<Object>(this, Object.class, true, false) {

                    @Override
                    public void onSuccess(Object data) {
                        ToastUtil.showToast(HomeDetailActivity.this, "退出家庭成功!");
                        Intent intent = new Intent();
                        setResult(RESULT_OK,intent);
                        finish();
                    }


                    @Override
                    public void onFailure(String message) {
                        ToastUtil.showToast(HomeDetailActivity.this, message);
                    }
                });
    }


    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()){
            case R.id.home_img_layout:
                showDialog();
                break;
            case R.id.delete_home_layout:
                exitFamily();
                break;

        }
    }

    private void showDialog() {
        final SelectPictureDialog dialog = new SelectPictureDialog(this);
        dialog.show();
        dialog.setDemoListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeDetailActivity.this,
                        DemoHomePictureActivity.class);
                startActivityForResult(intent, REQUEST_SELECT_DEMO);
                dialog.dismiss();
            }
        });

        dialog.setSelectPhotoListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PhotoUtils.openPic(HomeDetailActivity.this, REQUEST_SELECT_PIC);
                dialog.dismiss();

            }
        });

        dialog.setTakePhotoListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PhotoUtils.takePicture(HomeDetailActivity.this, mUri, REQUEST_CAPTURE_PIC);
                dialog.dismiss();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode != RESULT_OK) {
            return;
        }


        switch (requestCode) {

            case REQUEST_SELECT_DEMO:
                int pictureId = data.getIntExtra("picture_id", R.drawable.ic_home_1);

                ImageLoaderUtil.getInstance().displayImage("drawable://"
                        + pictureId, mHomeImg);

                mCurrentPictureId = pictureId;

                break;
            case REQUEST_SELECT_PIC:

                mDestFile = new File(Constants.FILE_DIR + "crop.jpg");
                PhotoUtils.cropImageUri(this, data.getData(), Uri.fromFile(mDestFile),
                        1, 1, 720, 720, REQUEST_CROP_PIC);

                break;
            case REQUEST_CAPTURE_PIC:

                mDestFile = new File(Constants.FILE_DIR + "crop.jpg");
                PhotoUtils.cropImageUri(this, mUri, Uri.fromFile(mDestFile),
                        1, 1, 720, 720, REQUEST_CROP_PIC);

                break;

            case REQUEST_CROP_PIC:

                mHomeImg.setImageBitmap(PhotoUtils.getBitmapFromUri
                        (Uri.fromFile(mDestFile), this));

                mCurrentPictureId = 0;

                break;
        }


    }

}
