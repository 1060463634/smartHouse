package com.qqcs.smartHouse.widgets;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.qqcs.smartHouse.activity.LoginActivity;
import com.qqcs.smartHouse.utils.LogUtil;


public class MyWebView extends WebView
{
	private Context mContext;
	private boolean isFirstIn = true;
	private Dialog mLoadingDialog;


	public MyWebView(final Context context, AttributeSet attrs)
	{
		super(context, attrs);
		mContext = context;
		initWebView();

		//屏蔽长按操作
		setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View view) {
				return true;
			}
		});

		setWebViewClient(new WebViewClient() {


			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				LogUtil.d("start wang url",url);

				if (isFirstIn ) {
					isFirstIn = false;
					mLoadingDialog = WeiboDialogUtils.createLoadingDialog(mContext, "加载中...");
				}

			}

			@Override
			public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {

			}

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				LogUtil.d("shouldOverride wang url",url);

				Uri uri = Uri.parse(url);
				String path = uri.getPath();

				if(TextUtils.isEmpty(path)){
					return false;
				}

				Intent intent;
				switch (path){


					//首页
					case "/mobile.jhtml":
						intent = new Intent(mContext, LoginActivity.class);
						intent.putExtra("url",url);
						mContext.startActivity(intent);
						break;


					default:
						loadUrl(url);
						break;
				}


				return true;
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				CookieManager cookieManager = CookieManager.getInstance();
				String cookieStr = cookieManager.getCookie(url);
				LogUtil.d("cookieStr:" + cookieStr);

				WeiboDialogUtils.closeDialog(mLoadingDialog);

				super.onPageFinished(view, url);
			}
		});

		setWebChromeClient(new WebChromeClient(){

			@Override
			public void onProgressChanged(WebView webView, int i) {
				super.onProgressChanged(webView, i);
				LogUtil.d(i+"");
			}


			@Override
			public boolean onJsConfirm(WebView webView, String s, String s1, final JsResult jsResult) {
				final MyAlertDialog dialog = new MyAlertDialog(mContext);
				dialog.show();
				dialog.setText(s1);
				dialog.setPositiveListener(new OnClickListener() {
					@Override
					public void onClick(View view) {
						jsResult.confirm();
						dialog.dismiss();
					}
				});
				dialog.setNegativeListener(new OnClickListener() {
					@Override
					public void onClick(View view) {
						jsResult.cancel();
						dialog.dismiss();
					}
				});
				dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
					@Override
					public void onCancel(DialogInterface dialogInterface) {
						jsResult.cancel();
					}
				});

				return true;

			}
		});

	}

	private void initWebView() {
		WebSettings webSettings = getSettings();
		//支持js
		webSettings.setJavaScriptEnabled(true);

		//设置webview的字体大小不跟谁系统改变
		webSettings.setTextSize(WebSettings.TextSize.NORMAL);

		//缓存模式：
		webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
		webSettings.setDomStorageEnabled(true);

		// 设置自适应屏幕，两者合用
//		webSettings.setLoadWithOverviewMode(true);
//		webSettings.setUseWideViewPort(true);
		//隐藏原生的缩放控件
		//webSettings.setDisplayZoomControls(false);

	}




}
