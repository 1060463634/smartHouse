package com.qqcs.smartHouse.application;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.qqcs.smartHouse.network.MyStringCallback;
import com.qqcs.smartHouse.utils.CommonUtil;
import com.qqcs.smartHouse.utils.LogUtil;
import com.qqcs.smartHouse.utils.MD5Utils;
import com.qqcs.smartHouse.utils.SharePreferenceUtil;
import com.zhy.http.okhttp.OkHttpUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.helper.Logger;
import okhttp3.Call;
import okhttp3.MediaType;

/**
 * 萤石自定义接收器
 *
 */
public class EzvizReceiver extends BroadcastReceiver {
	private static final String TAG = "wang";

	@Override
	public void onReceive(Context context, Intent intent) {

	}


}
