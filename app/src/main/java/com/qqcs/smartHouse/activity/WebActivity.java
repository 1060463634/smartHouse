package com.qqcs.smartHouse.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;

import com.qqcs.smartHouse.R;
import com.qqcs.smartHouse.widgets.MyWebView;

public class WebActivity extends BaseActivity{

    private MyWebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.web_layout);
        setTitleName("百度");
        findView();
        initView();

    }


    private void findView(){

        mWebView = (MyWebView) findViewById(R.id.webView);
    }

    private void initView(){
        String url = getIntent().getStringExtra("url");
        mWebView.loadUrl(url);
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK){

            switch (requestCode){
                case 1:

                    break;

            }

        }


    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {

        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN)
        {
            //如果在首页按返回，则退出程序
            if(mWebView.canGoBack()){
                mWebView.goBack();
            }else{
				finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    public void onMultiClick(View v) {
        switch (v.getId()){
            case R.id.back_img:

                break;

        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mWebView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mWebView.onResume();

    }

    @Override
    protected void onDestroy() 	{
        if (mWebView != null) {
            mWebView.destroy();
            mWebView = null;
        }
        super.onDestroy();
    }

}
