package com.qqcs.smartHouse.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
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
import com.qqcs.smartHouse.models.UploadPicBean;
import com.qqcs.smartHouse.network.CommonJsonList;
import com.qqcs.smartHouse.network.MyStringCallback;
import com.qqcs.smartHouse.utils.BitmapUtil;
import com.qqcs.smartHouse.utils.CommonUtil;
import com.qqcs.smartHouse.utils.ImageLoaderUtil;
import com.qqcs.smartHouse.utils.MD5Utils;
import com.qqcs.smartHouse.utils.PhotoUtils;
import com.qqcs.smartHouse.utils.SharePreferenceUtil;
import com.qqcs.smartHouse.utils.ToastUtil;
import com.qqcs.smartHouse.widgets.MyAlertDialog;
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
    EditText mHomeNameTV;

    @BindView(R.id.home_address_tv)
    EditText mHomeAddressTV;

    @BindView(R.id.delete_tv)
    TextView mDeleteTV;

    @BindView(R.id.save_tv)
    TextView mSaveTV;

    private String familyId;
    private String familyName;
    private String address;
    private String userRole;
    private String familyImg;

    private File mFile;
    private Uri mUri;
    private File mDestFile;

    //根据状态值，判断图片状态，0：未修改图片；-1：拍照或相册；其它值：来自摄影师
    private int mCurrentPictureId = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_detail);
        ButterKnife.bind(this);
        setTitle(R.string.home_detail_title);
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
        mHomeNameTV.setSelection(mHomeNameTV.getText().length());
        mHomeImgLayout.setOnClickListener(this);
        mSaveTV.setOnClickListener(this);

        //初始化用户操作权限
        if(userRole.equalsIgnoreCase(Constants.ROLE_MANAGE)){
            mDeleteTV.setText("退出家庭");
            mDeleteHomeLayout.setOnClickListener(this);
        } else if(userRole.equalsIgnoreCase(Constants.ROLE_NORMAL)){
            mDeleteTV.setText("退出家庭");
            mDeleteHomeLayout.setOnClickListener(this);
            mBindGatewayLayout.setVisibility(View.GONE);
            mHomeMemberLayout.setVisibility(View.GONE);
            mSaveTV.setVisibility(View.GONE);
            mHomeNameTV.setEnabled(false);
            mHomeAddressTV.setEnabled(false);
            mHomeImgLayout.setClickable(false);
        }else if(userRole.equalsIgnoreCase(Constants.ROLE_LOAD)){
            mDeleteHomeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showConfirmDialog(true);
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
                .url(Constants.HTTP_QUIT_FAMILY)
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

    private void saveFamily(String imgId){
        String accessToken = (String) SharePreferenceUtil.
                get(this, SP_Constants.ACCESS_TOKEN,"");
        String timestamp = System.currentTimeMillis() + "";
        JSONObject object = CommonUtil.getRequstJson(getApplicationContext());
        JSONObject dataObject = new JSONObject();

        try {
            String lng = object.getJSONObject("device").getString("lng");
            String lat = object.getJSONObject("device").getString("lat");
            String name = mHomeNameTV.getText().toString();
            String address = mHomeAddressTV.getText().toString();

            dataObject.put("familyId", familyId);
            dataObject.put("lng", lng);
            dataObject.put("lat", lat);
            dataObject.put("address", address);
            dataObject.put("familyNickname", name);
            dataObject.put("familyImg", imgId);
            dataObject.put("timestamp", timestamp);

            object.put("data", dataObject);
            object.put("sign", MD5Utils.md5s(familyId
                    + lng + lat + address + name + imgId +timestamp));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        OkHttpUtils
                .postString()
                .tag(this)
                .url(Constants.HTTP_UPDATE_FAMILY)
                .addHeader("access-token",accessToken)
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .content(object.toString())
                .build()
                .execute(new MyStringCallback<Object>(this, Object.class, true, false) {

                    @Override
                    public void onSuccess(Object data) {
                        ToastUtil.showToast(HomeDetailActivity.this, "保存成功！");
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

    private void updatePicture() {

        String accessToken = (String) SharePreferenceUtil.
                get(this, SP_Constants.ACCESS_TOKEN,"");

        final File file ;
        if (mCurrentPictureId == -1) {
            file = mDestFile;
        } else {
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), mCurrentPictureId);
            BitmapUtil.saveBitmapToSDCard(bitmap,Constants.FILE_DIR + "crop.jpg");
            file = new File(Constants.FILE_DIR + "crop.jpg");
        }


        OkHttpUtils
                .post()
                .tag(this)
                .addFile("file", "crop.jpg", file)
                .url(Constants.HTTP_UPLOAD_PICTURE)
                .addHeader("access-token",accessToken)
                .build()
                .execute(new MyStringCallback<UploadPicBean>(this, UploadPicBean.class, true, false) {
                    @Override
                    public void onSuccess(UploadPicBean data) {
                        saveFamily(data.getId());
                    }

                    @Override
                    public void onFailure(String message) {
                        ToastUtil.showToast(HomeDetailActivity.this, message);

                    }
                });
    }


    private void showConfirmDialog(final boolean isDelete){
        final MyAlertDialog dialog = new MyAlertDialog(this);
        dialog.show();
        dialog.setTitle("警告");
        if(isDelete){
            dialog.setText("是否确认删除此家庭，此操作不可逆");
        }else {
            dialog.setText("是否确认退出此家庭，此操作不可逆");
        }

        dialog.setPositiveListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if(isDelete){
                    deleteFamily();
                }else {
                    exitFamily();
                }

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
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()){
            case R.id.home_img_layout:
                showDialog();
                break;
            case R.id.delete_home_layout:
                showConfirmDialog(false);

                break;
            case R.id.save_tv:

                if (TextUtils.isEmpty(mHomeNameTV.getText().toString())) {
                    ToastUtil.showToast(this, "请输入家庭名称");
                    return;
                }

                if (TextUtils.isEmpty(mHomeAddressTV.getText().toString())) {
                    ToastUtil.showToast(this, "请输入家庭地址");
                    return;
                }

                if(mCurrentPictureId == 0){
                    String [] strings = familyImg.split("/");
                    saveFamily(strings[strings.length - 1]);

                }else {
                    updatePicture();
                }
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

                mCurrentPictureId = -1;

                break;
        }


    }

}
