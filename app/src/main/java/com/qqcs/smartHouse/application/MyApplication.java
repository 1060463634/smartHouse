package com.qqcs.smartHouse.application;

import android.app.Application;

import com.qqcs.smartHouse.utils.ImageLoaderUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.cookie.CookieJarImpl;
import com.zhy.http.okhttp.cookie.store.PersistentCookieStore;

import java.io.File;

import okhttp3.OkHttpClient;


/**
 *
 *
 */
public class MyApplication extends Application {


    @Override
    public void onCreate() {
        super.onCreate();

        ImageLoaderUtil.getInstance().initImageLoader(getApplicationContext());

        initOkHttp();

        creatDir();
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
}
