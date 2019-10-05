package com.qqcs.smartHouse.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.qqcs.smartHouse.R;
import com.qqcs.smartHouse.application.Constants;
import com.qqcs.smartHouse.application.SP_Constants;
import com.qqcs.smartHouse.models.FamilyInfoBean;
import com.qqcs.smartHouse.models.LoginBean;
import com.qqcs.smartHouse.models.UploadPicBean;
import com.qqcs.smartHouse.network.MyStringCallback;
import com.qqcs.smartHouse.utils.AESUtils;
import com.qqcs.smartHouse.utils.BitmapUtil;
import com.qqcs.smartHouse.utils.CommonUtil;
import com.qqcs.smartHouse.utils.FileUtil;
import com.qqcs.smartHouse.utils.ImageLoaderUtil;
import com.qqcs.smartHouse.utils.LogUtil;
import com.qqcs.smartHouse.utils.MD5Utils;
import com.qqcs.smartHouse.utils.PhotoUtils;
import com.qqcs.smartHouse.utils.SharePreferenceUtil;
import com.qqcs.smartHouse.utils.ToastUtil;
import com.qqcs.smartHouse.widgets.SelectPictureDialog;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.MediaType;


public class CreateHomeActivity extends BaseActivity{


    public static final int REQUEST_CAPTURE_PIC = 101;
    public static final int REQUEST_CROP_PIC = 103;
    public static final int REQUEST_SELECT_PIC = 102;
    public static final int REQUEST_SELECT_DEMO = 104;

    @BindView(R.id.next_btn)
    Button mNextBtn;

    @BindView(R.id.home_name_edt)
    EditText mHomeNameEdt;

    @BindView(R.id.home_location_edt)
    EditText mHomeLocationEdt;

    @BindView(R.id.home_img_layout)
    RelativeLayout mHomeImgLayout;

    @BindView(R.id.home_img)
    ImageView mHomeImg;

    boolean fromLogin = true;
    private File mFile;
    private Uri mUri;
    private File mDestFile;
    private int mCurrentPictureId = R.drawable.ic_home_1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_home);
        ButterKnife.bind(this);
        setTitle(R.string.create_home_title);
        initView();

    }


    private void initView() {
        fromLogin = getIntent().getBooleanExtra("fromLogin", true);
        mNextBtn.setOnClickListener(this);
        mHomeImgLayout.setOnClickListener(this);

        if (!fromLogin) {
            mNextBtn.setText("完成");
        }


        mFile = new File(Constants.FILE_DIR + "homeTake.jpg");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            mUri = FileProvider.getUriForFile(this, "com.qqcs.smartHouse", mFile);

        } else {
            mUri = Uri.fromFile(mFile);
        }

    }


    private void showDialog() {
        final SelectPictureDialog dialog = new SelectPictureDialog(this);
        dialog.show();
        dialog.setDemoListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CreateHomeActivity.this,
                        DemoHomePictureActivity.class);
                startActivityForResult(intent, REQUEST_SELECT_DEMO);
                dialog.dismiss();
            }
        });

        dialog.setSelectPhotoListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PhotoUtils.openPic(CreateHomeActivity.this, REQUEST_SELECT_PIC);
                dialog.dismiss();

            }
        });

        dialog.setTakePhotoListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PhotoUtils.takePicture(CreateHomeActivity.this, mUri, REQUEST_CAPTURE_PIC);
                dialog.dismiss();
            }
        });

    }


    private void createFamily(String familyName, String familyLocation, String pictureId) {
        String accessToken = (String) SharePreferenceUtil.
                get(this, SP_Constants.ACCESS_TOKEN,"");

        String timestamp = System.currentTimeMillis() + "";
        JSONObject object = CommonUtil.getRequstJson(getApplicationContext());
        JSONObject dataObject = new JSONObject();

        try {
            String lng = object.getJSONObject("device").getString("lng");
            String lat = object.getJSONObject("device").getString("lat");

            dataObject.put("familyName", familyName);
            dataObject.put("address", familyLocation);
            dataObject.put("lng", lng);
            dataObject.put("lat", lat);
            dataObject.put("picId", pictureId);
            dataObject.put("timestamp", timestamp);

            object.put("data", dataObject);
            object.put("sign", MD5Utils.md5s(familyName
                    + familyLocation + lng + lat + pictureId + timestamp));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        OkHttpUtils
                .postString()
                .tag(this)
                .url(Constants.HTTP_CREATE_FAMILY)
                .addHeader("access-token",accessToken)
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .content(object.toString())
                .build()
                .execute(new MyStringCallback<FamilyInfoBean>(this, FamilyInfoBean.class, true, false) {
                    @Override
                    public void onSuccess(FamilyInfoBean data) {

                        ToastUtil.showToast(CreateHomeActivity.this, "创建家庭成功！");

                        Intent intent;
                        if (!fromLogin) {
                            intent = new Intent();
                            setResult(RESULT_OK,intent);
                            intent.putExtra("familyId",data.getFamilyId());
                            finish();
                        } else {
                            SharePreferenceUtil.put(CreateHomeActivity.this,
                                    SP_Constants.HAS_FAMILY, true);
                            SharePreferenceUtil.put(CreateHomeActivity.this,
                                    SP_Constants.CURRENT_FAMILY_ID, data.getFamilyId());
                            intent = new Intent(CreateHomeActivity.this, FillInfomationActivity.class);
                            intent.putExtra("familyId",data.getFamilyId());
                            startActivity(intent);
                        }


                    }

                    @Override
                    public void onFailure(String message) {
                        ToastUtil.showToast(CreateHomeActivity.this, message);
                    }
                });
    }

    private void updatePicture() {
        if (TextUtils.isEmpty(mHomeNameEdt.getText().toString())) {
            ToastUtil.showToast(this, "请输入家庭名称");
            return;
        }

        if (TextUtils.isEmpty(mHomeLocationEdt.getText().toString())) {
            ToastUtil.showToast(this, "请输入地理位置");
            return;
        }

        String accessToken = (String) SharePreferenceUtil.
                get(this, SP_Constants.ACCESS_TOKEN,"");

        final File file ;
        if (mCurrentPictureId == 0) {
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
                        createFamily(mHomeNameEdt.getText().toString(),
                                mHomeLocationEdt.getText().toString(), data.getId());

                    }

                    @Override
                    public void onFailure(String message) {
                        ToastUtil.showToast(CreateHomeActivity.this, message);

                    }
                });
    }

    @Override
    public void onMultiClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.next_btn:
                updatePicture();

                break;
            case R.id.home_img_layout:
                showDialog();

                break;

        }
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
