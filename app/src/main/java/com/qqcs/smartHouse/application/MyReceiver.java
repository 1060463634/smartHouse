package com.qqcs.smartHouse.application;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.view.View;

import com.qqcs.smartHouse.activity.HomeDetailActivity;
import com.qqcs.smartHouse.activity.LoginActivity;
import com.qqcs.smartHouse.activity.MainActivity;
import com.qqcs.smartHouse.models.EventBusBean;
import com.qqcs.smartHouse.network.MyStringCallback;
import com.qqcs.smartHouse.utils.ActivityManagerUtil;
import com.qqcs.smartHouse.utils.CommonUtil;
import com.qqcs.smartHouse.utils.LogUtil;
import com.qqcs.smartHouse.utils.MD5Utils;
import com.qqcs.smartHouse.utils.SharePreferenceUtil;
import com.qqcs.smartHouse.utils.ToastUtil;
import com.qqcs.smartHouse.widgets.MyAlertDialog;
import com.zhy.http.okhttp.OkHttpUtils;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.helper.Logger;
import okhttp3.Call;
import okhttp3.MediaType;

/**
 * 自定义接收器
 * <p>
 * 如果不定义这个 Receiver，则：
 * 1) 默认用户会打开主界面
 * 2) 接收不到自定义消息
 */
public class MyReceiver extends BroadcastReceiver {
    private static final String TAG = "wang";

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            Bundle bundle = intent.getExtras();
            Logger.d(TAG, "[MyReceiver] onReceive - " + intent.getAction() + ", extras: " + printBundle(bundle));

            if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
                String regId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
                LogUtil.d(TAG, "[MyReceiver] 接收Registration Id : " + regId);
                SharePreferenceUtil.put(context, SP_Constants.REGISTRATION_ID, regId);
                sendRegistrationId(context, regId);

            } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
                LogUtil.d(TAG, "[MyReceiver] 接收到推送下来的自定义消息: " + bundle.getString(JPushInterface.EXTRA_MESSAGE));
                String json = bundle.getString(JPushInterface.EXTRA_MESSAGE);
                processCustomMessage(context, json);

            } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
                LogUtil.d("[MyReceiver] 接收到推送下来的通知 " + bundle.toString());
                String json = bundle.getString(JPushInterface.EXTRA_EXTRA);
                String msg = bundle.getString(JPushInterface.EXTRA_ALERT);
                processCustomNotice( json,msg);


            } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
                LogUtil.d(TAG, "[MyReceiver] 用户点击打开了通知");
//				//打开自定义的Activity
//				Intent i = new Intent(context, TestActivity.class);
//				i.putExtras(bundle);
//				//i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//				i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP );
//				context.startActivity(i);

            } else if (JPushInterface.ACTION_RICHPUSH_CALLBACK.equals(intent.getAction())) {
                Logger.d(TAG, "[MyReceiver] 用户收到到RICH PUSH CALLBACK: " + bundle.getString(JPushInterface.EXTRA_EXTRA));
                //在这里根据 JPushInterface.EXTRA_EXTRA 的内容处理代码，比如打开新的Activity， 打开一个网页等..

            } else if (JPushInterface.ACTION_CONNECTION_CHANGE.equals(intent.getAction())) {
                boolean connected = intent.getBooleanExtra(JPushInterface.EXTRA_CONNECTION_CHANGE, false);
                Logger.w(TAG, "[MyReceiver]" + intent.getAction() + " connected state change to " + connected);
            } else {
                Logger.d(TAG, "[MyReceiver] Unhandled intent - " + intent.getAction());
            }
        } catch (Exception e) {
            e.printStackTrace();

        }

    }

    // 打印所有的 intent extra 数据
    private static String printBundle(Bundle bundle) {
        StringBuilder sb = new StringBuilder();
        for (String key : bundle.keySet()) {
            if (key.equals(JPushInterface.EXTRA_NOTIFICATION_ID)) {
                sb.append("\nkey:" + key + ", value:" + bundle.getInt(key));
            } else if (key.equals(JPushInterface.EXTRA_CONNECTION_CHANGE)) {
                sb.append("\nkey:" + key + ", value:" + bundle.getBoolean(key));
            } else if (key.equals(JPushInterface.EXTRA_EXTRA)) {
                if (TextUtils.isEmpty(bundle.getString(JPushInterface.EXTRA_EXTRA))) {
                    Logger.i(TAG, "This message has no Extra data");
                    continue;
                }

                try {
                    JSONObject json = new JSONObject(bundle.getString(JPushInterface.EXTRA_EXTRA));
                    Iterator<String> it = json.keys();

                    while (it.hasNext()) {
                        String myKey = it.next();
                        sb.append("\nkey:" + key + ", value: [" +
                                myKey + " - " + json.optString(myKey) + "]");
                    }
                } catch (JSONException e) {
                    Logger.e(TAG, "Get message extra JSON error!");
                }

            } else {
                sb.append("\nkey:" + key + ", value:" + bundle.get(key));
            }
        }
        return sb.toString();
    }



    private void processCustomMessage(Context context, String json) {
        // {"familyId":43,"isReply":true,"option":3,"sendTime":1570871365805}

        JSONObject jsonObject = null;

        try {
            jsonObject = new JSONObject(json);
            String familyId = jsonObject.getString("familyId");
            String option = jsonObject.getString("option");
            String currentFamilyId = (String) SharePreferenceUtil.
                    get(context, SP_Constants.CURRENT_FAMILY_ID, "");
            if (familyId.equalsIgnoreCase(currentFamilyId)) {

                EventBus.getDefault().post(new EventBusBean(option));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }


		/*if (MainActivity.isForeground) {
			String message = bundle.getString(JPushInterface.EXTRA_MESSAGE);
			String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
			Intent msgIntent = new Intent(MainActivity.MESSAGE_RECEIVED_ACTION);
			msgIntent.putExtra(MainActivity.KEY_MESSAGE, message);
			if (!ExampleUtil.isEmpty(extras)) {
				try {
					JSONObject extraJson = new JSONObject(extras);
					if (extraJson.length() > 0) {
						msgIntent.putExtra(MainActivity.KEY_EXTRAS, extras);
					}
				} catch (JSONException e) {

				}

			}
			LocalBroadcastManager.getInstance(context).sendBroadcast(msgIntent);
		}*/
    }

    private void processCustomNotice(String json,String msg) {
        // { requestId = 65 ;type = 2}

        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(json);
            String type = jsonObject.getJSONObject("payload").getString("type");
            //收到家庭申请
            if (type.equalsIgnoreCase("1")) {
                String requestId = jsonObject.getJSONObject("payload").getString("requestId");
                showConfirmDialog(requestId,msg);
                //收到申请批复
            }else if(type.equalsIgnoreCase("2")){
                EventBus.getDefault().post(new EventBusBean(EventBusBean.ACCEPT_BY_HOST));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void sendRegistrationId(Context context, String id) {
        String accessToken = (String) SharePreferenceUtil.
                get(context, SP_Constants.ACCESS_TOKEN, "");

        String timestamp = System.currentTimeMillis() + "";
        JSONObject object = CommonUtil.getRequstJson(context);
        JSONObject dataObject = new JSONObject();

        try {
            dataObject.put("registrationId", id);
            dataObject.put("timestamp", timestamp);
            object.put("data", dataObject);
            object.put("sign", MD5Utils.md5s(id + timestamp));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        OkHttpUtils
                .postString()
                .tag(this)
                .url(Constants.HTTP_SEND_REGISTION_ID)
                .addHeader("access-token", accessToken)
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .content(object.toString())
                .build()
                .execute(new MyStringCallback<Object>(context, Object.class, false, false) {

                    @Override
                    public void onError(Call call, Exception e, int i) {
                    }

                    @Override
                    public void onSuccess(Object data) {
                    }

                    @Override
                    public void onFailure(String message) {

                    }
                });
    }


    private void showConfirmDialog(final String requestId,String msg){

        final Context context = ActivityManagerUtil.getInstance().getLastActivity();
        final MyAlertDialog dialog = new MyAlertDialog(context);
        dialog.show();
        dialog.setTitle("审批");
        dialog.setText(msg);
        dialog.setPositiveText("同意");
        dialog.setNagtiveText("拒绝");

        dialog.setPositiveListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

                acceptFamilyMember(context,requestId,"1");
            }
        });

        dialog.setNegativeListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                acceptFamilyMember(context,requestId,"2");
            }
        });


    }

    private void acceptFamilyMember(final Context context, String requestId, String result){
        String accessToken = (String) SharePreferenceUtil.
                get(context, SP_Constants.ACCESS_TOKEN,"");
        String timestamp = System.currentTimeMillis() + "";
        JSONObject object = CommonUtil.getRequstJson(context);
        JSONObject dataObject = new JSONObject();

        try {
            dataObject.put("requestId", requestId);
            dataObject.put("result", result);
            dataObject.put("timestamp", timestamp);

            object.put("data", dataObject);
            object.put("sign", MD5Utils.md5s(requestId + result + timestamp));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        OkHttpUtils
                .postString()
                .tag(this)
                .url(Constants.HTTP_ACCEPT_MEMBER)
                .addHeader("access-token",accessToken)
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .content(object.toString())
                .build()
                .execute(new MyStringCallback<Object>(context, Object.class, true, false) {

                    @Override
                    public void onSuccess(Object data) {
                    }


                    @Override
                    public void onFailure(String message) {
                        ToastUtil.showToast(context, message);
                    }
                });
    }

}
