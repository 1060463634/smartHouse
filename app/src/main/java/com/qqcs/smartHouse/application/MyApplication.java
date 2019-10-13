package com.qqcs.smartHouse.application;

import android.app.Application;

import com.google.gson.Gson;
import com.qqcs.smartHouse.utils.ImageLoaderUtil;
import com.videogo.openapi.EZOpenSDK;
import com.videogo.openapi.EzvizAPI;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.cookie.CookieJarImpl;
import com.zhy.http.okhttp.cookie.store.PersistentCookieStore;

import java.io.File;

import cn.jpush.android.api.JPushInterface;
import ezviz.ezopensdk.demo.SdkInitParams;
import ezviz.ezopensdk.demo.SdkInitTool;
import ezviz.ezopensdk.demo.SpTool;
import ezviz.ezopensdk.demo.ValueKeys;
import okhttp3.OkHttpClient;


/**
 *
 *
 */
public class MyApplication extends Application {

    private static MyApplication instance;
    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;

        ImageLoaderUtil.getInstance().initImageLoader(getApplicationContext());

        initOkHttp();

        creatDir();

        JPushInterface.setDebugMode(true);
        JPushInterface.init(this);

        //yingshi
        EZOpenSDK.showSDKLog(true);
        /** * 设置是否支持P2P取流,详见api */
        EZOpenSDK.enableP2P(false);

        /** * APP_KEY请替换成自己申请的 */
        EZOpenSDK.initLib(this, "a2b622cdd9bf4c2c9b2b0904181c0a06");


    }



    private void creatDir(){

        // 拍照存放目录
        File pictureDir = new File(Constants.FILE_DIR );

        if (!pictureDir.exists()) {
            pictureDir.mkdirs();
        }

    }


    private void initOkHttp(){
        CookieJarImpl cookieJar = new CookieJarImpl
                (new PersistentCookieStore(getApplicationContext()));
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .cookieJar(cookieJar)
                .build();

        OkHttpUtils.initClient(okHttpClient);

    }

    public static MyApplication getApplication() {
        return instance;
    }




}
