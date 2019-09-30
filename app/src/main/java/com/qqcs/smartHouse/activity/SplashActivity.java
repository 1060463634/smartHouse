package com.qqcs.smartHouse.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.google.gson.Gson;
import com.qqcs.smartHouse.R;
import com.qqcs.smartHouse.application.Constants;
import com.qqcs.smartHouse.application.SP_Constants;
import com.qqcs.smartHouse.models.CitySn;
import com.qqcs.smartHouse.models.LoginBean;
import com.qqcs.smartHouse.network.MyStringCallback;
import com.qqcs.smartHouse.utils.AESUtils;
import com.qqcs.smartHouse.utils.CommonUtil;
import com.qqcs.smartHouse.utils.LocationUtil;
import com.qqcs.smartHouse.utils.LogUtil;
import com.qqcs.smartHouse.utils.SharePreferenceUtil;
import com.qqcs.smartHouse.utils.ToastUtil;
import com.qqcs.smartHouse.widgets.MyAlertDialog;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.FileCallBack;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SplashActivity extends BaseActivity {

    private Dialog mVersionDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);
        initView();
        checkPermisson();

    }

    private void initView(){

    }

    private void checkPermisson(){
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(getApplicationContext(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.CAMERA,
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.READ_PHONE_STATE}, 1);
            } else {
                timeLogin();
            }
        } else {
            timeLogin();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == 1) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED &&
                      grantResults[2] == PackageManager.PERMISSION_GRANTED &&
                        grantResults[3] == PackageManager.PERMISSION_GRANTED) {
                timeLogin();
            } else {
                finish();
                Toast.makeText(this, "请打开权限后重试", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void timeLogin(){
        getCityInfo();
        LocationUtil.getCurrentLocation(this, callBack);
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                autoLogin();

            }
        },1500);

    }

    private void getCityInfo(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();//创建OkHttpClient对象
                    Request request = new Request.Builder()
                            .url("http://pv.sohu.com/cityjson")//请求接口。如果需要传参拼接到接口后面。
                            .build();//创建Request 对象
                    Response response = null;
                    response = client.newCall(request).execute();//得到Response 对象
                    if (response.isSuccessful()) {
                        String json=response.body().string();
                        String newJson=json.substring(json.indexOf("{"), json.indexOf("}") + 1);
                        CitySn citySn = new Gson().fromJson(newJson, CitySn.class);
                        SharePreferenceUtil.put(SplashActivity.this, SP_Constants.LOCATION_CITY, citySn.getCname());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private LocationUtil.LocationCallBack callBack = new LocationUtil.LocationCallBack() {
        @Override
        public void onSuccess(Location location) {
            LogUtil.d(location.getLongitude() + ","+location.getLatitude());
            SharePreferenceUtil.put(SplashActivity.this,
                    SP_Constants.LOCATION_LNG, location.getLongitude());
            SharePreferenceUtil.put(SplashActivity.this,
                    SP_Constants.LOCATION_LAT, location.getLatitude());

        }

        @Override
        public void onFail(String msg) {
            LogUtil.d(msg);
        }
    };







    private void autoLogin(){
        String accessToken = (String) SharePreferenceUtil.get(this,
                SP_Constants.ACCESS_TOKEN,"");
        Boolean hasFamily = (boolean) SharePreferenceUtil.get(this,
                SP_Constants.HAS_FAMILY,false);

       if(!TextUtils.isEmpty(accessToken)){
           if(hasFamily){
               Intent intent = new Intent(this,MainActivity.class);
               startActivity(intent);
               finish();

           }else {
               Intent intent = new Intent(this, WelcomeHomeActivity.class);
               startActivity(intent);
               finish();
           }
       }else {
           gotoLoginActivity();

       }

    }

    private void gotoLoginActivity(){
        startActivity(new Intent(SplashActivity.this,LoginActivity.class));
        finish();

    }

    public void createDialog() {
        final MyAlertDialog dialog = new MyAlertDialog(this);
        dialog.show();
        dialog.setText("有新版本可以更新");
        dialog.setPositiveText("更新");
        dialog.setNagtiveText("取消");
        dialog.setPositiveListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                showDownloadingDialog();
            }
        });
        dialog.setNegativeListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {

            }
        });

    }

    private void showDownloadingDialog() {
        LayoutInflater inflater = LayoutInflater.from(this);
        RelativeLayout layout = (RelativeLayout) inflater.inflate
                (R.layout.dialog_version_downloading_layout, null);
        mVersionDialog = new AlertDialog.Builder(this).create();
        mVersionDialog.setCanceledOnTouchOutside(false);
        mVersionDialog.setCancelable(false);
        mVersionDialog.show();
        mVersionDialog.getWindow().setContentView(layout);

        ProgressBar progressBar = (ProgressBar) layout.findViewById(R.id.progress_bar);
        TextView downloadingTv = (TextView) layout.findViewById(R.id.downloading_tv);
        download(progressBar, downloadingTv);

    }

    public void download(final ProgressBar progressBar, final TextView textView) {

        OkHttpUtils//
                .get()//
                .tag(this)
                .url("http://openbox.mobilem.360.cn/index/d/sid/3970928")
                //.url("http://www.kamdellar.com/upload/app/kamdellar.apk")
                .build()//

                .execute(new FileCallBack(Constants.FILE_DIR, "kamdellar.apk")
                {
                    @Override
                    public void onBefore(Request request, int id) {

                    }

                    @Override
                    public void inProgress(float progress, long total, int id)
                    {
                        progressBar.setProgress((int)(progress * 100));
                    }

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        ToastUtil.showToast(SplashActivity.this, "下载出错");
                        mVersionDialog.dismiss();
                        finish();
                    }

                    @Override
                    public void onResponse(File file, int id) {

                        textView.setText("下载完成");
                        mVersionDialog.dismiss();

                        if (Build.VERSION.SDK_INT >= 24){

                            Uri apkUri = FileProvider.getUriForFile(SplashActivity.this,
                                    SplashActivity.this.getPackageName(), file);
                            Intent install = new Intent(Intent.ACTION_VIEW);
                            install.addCategory(Intent.CATEGORY_DEFAULT);
                            install.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            install.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            install.setDataAndType(apkUri, "application/vnd.android.package-archive");
                            startActivity(install);
                            finish();
                        } else {
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.setDataAndType(Uri.fromFile(file),
                                    "application/vnd.android.package-archive");
                            startActivity(intent);
                            finish();
                        }

                    }
                });


    }



}
