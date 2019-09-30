package com.qqcs.smartHouse.network;

import android.app.Dialog;
import android.content.Context;


import com.qqcs.smartHouse.R;
import com.qqcs.smartHouse.utils.LogUtil;
import com.qqcs.smartHouse.utils.ToastUtil;
import com.qqcs.smartHouse.widgets.WeiboDialogUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import okhttp3.Call;
import okhttp3.Request;

public abstract class MyStringCallback<T> extends StringCallback {

	private Context mContext;
	private Class mCls;
	private boolean mShowDialog;
	private boolean mIsList;
	private Dialog mDialog;


	public MyStringCallback(Context context, Class cls,
							boolean showDialog,boolean isList) {
		this.mContext = context;
		this.mCls = cls;
		this.mShowDialog = showDialog;
		this.mIsList = isList;
	}

	@Override
	public void onBefore(Request request, int id) {
		super.onBefore(request, id);
		if (mShowDialog) {
			showLoadingDialog();
		}
	}

	@Override
	public void onAfter(int id) {
		super.onAfter(id);
		if (mShowDialog) {
			dismissLoadingDialog();
		}
	}

	@Override
	public void onError(Call call, Exception e, int i) {
		ToastUtil.showToast(mContext, e.getMessage());
		LogUtil.d("http onError  :" + e.getMessage());

	}

	@Override
	public void onResponse(String s, int i) {
		LogUtil.d("http onSuccess  :" + s);

			try {
				if(!mIsList){
					CommonJson<T> commonJson = GsonUtils.fromJson(s, mCls);
					if (commonJson.getStatus().equalsIgnoreCase("Success")) {
						onSuccess(commonJson.getData());
					} else {
						onFailure(commonJson.getMessage());
						onFailure(commonJson.getData());
					}
				}else{
					CommonJsonList<T> commonJsonList = GsonUtils.fromJsonList(s, mCls);
					if (commonJsonList.getStatus().equalsIgnoreCase("Success")) {
						onSuccess(commonJsonList);
					} else {
						onFailure(commonJsonList.getMessage());
					}
				}

			} catch (Exception e1) {
				e1.printStackTrace();
				ToastUtil.showToast(mContext, "json 解析错误");
				LogUtil.d( "json_parse_error:" + e1.toString());
			}

		}


	protected void showLoadingDialog() {
		if (mDialog == null) {
			mDialog = WeiboDialogUtils.createLoadingDialog(mContext, "加载中...");
		}else{
			mDialog.show();
		}

	}

	protected void dismissLoadingDialog() {
		WeiboDialogUtils.closeDialog(mDialog);

	}

	public void onSuccess(CommonJsonList<T> json) {

	}

	public void onSuccess(T data) {

	}

	public void onFailure(String message) {
	}

	public void onFailure(T data) {

	}


}
