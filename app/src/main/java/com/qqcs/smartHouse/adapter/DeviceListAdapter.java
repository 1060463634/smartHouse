package com.qqcs.smartHouse.adapter;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ezviz.stream.EZStreamClientManager;
import com.mcxtzhang.swipemenulib.SwipeMenuLayout;
import com.qqcs.smartHouse.R;
import com.qqcs.smartHouse.activity.AirConditionControlActivity;
import com.qqcs.smartHouse.activity.DeviceEditActivity;
import com.qqcs.smartHouse.activity.DoorLockActivity;
import com.qqcs.smartHouse.activity.OpenWindowActivity;
import com.qqcs.smartHouse.activity.TvControlActivity;
import com.qqcs.smartHouse.application.Constants;
import com.qqcs.smartHouse.application.OnMultiClickListener;
import com.qqcs.smartHouse.application.SP_Constants;
import com.qqcs.smartHouse.fragment.HomeFragment;
import com.qqcs.smartHouse.models.AccessTokenBean;
import com.qqcs.smartHouse.models.DeviceBean;
import com.qqcs.smartHouse.models.EventBusBean;
import com.qqcs.smartHouse.models.RoomBean;
import com.qqcs.smartHouse.network.MyStringCallback;
import com.qqcs.smartHouse.utils.CommonUtil;
import com.qqcs.smartHouse.utils.EZUtils;
import com.qqcs.smartHouse.utils.LogUtil;
import com.qqcs.smartHouse.utils.MD5Utils;
import com.qqcs.smartHouse.utils.SharePreferenceUtil;
import com.qqcs.smartHouse.utils.ToastUtil;
import com.videogo.constant.IntentConsts;
import com.videogo.exception.BaseException;
import com.videogo.openapi.EZOpenSDK;
import com.videogo.openapi.EzvizAPI;
import com.videogo.openapi.bean.EZCameraInfo;
import com.videogo.openapi.bean.EZDeviceInfo;
import com.videogo.remoteplayback.list.EZPlayBackListActivity;
import com.videogo.remoteplayback.list.RemoteListContant;
import com.videogo.ui.cameralist.EZCameraListActivity;
import com.videogo.ui.cameralist.SelectCameraDialog;
import com.videogo.ui.realplay.EZRealPlayActivity;
import com.videogo.util.DateTimeUtil;
import com.zhy.http.okhttp.OkHttpUtils;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.MediaType;

import static com.ezviz.stream.EZError.EZ_OK;
import static com.qqcs.smartHouse.application.MyApplication.getApplication;


public class DeviceListAdapter extends BaseAdapter{

    private Context mContext;
    private HomeFragment mFragment;
    private List<DeviceBean> mlists;
    private LayoutInflater mInflater;
    private MediaPlayer mediaPlayer;
    private static final float BEEP_VOLUME = 0.10f;

    public DeviceListAdapter(Context context,HomeFragment fragment, List<DeviceBean> lists) {
        mContext = context;
        mFragment = fragment;
        mInflater = LayoutInflater.from(mContext);
        mlists = lists;
        initBeepSound();
    }

    public void refreshData(List<DeviceBean> lists) {
        mlists = lists;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mlists.size();
    }
    
    @Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}
   

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
    	ViewHolder viewHolder;
        if (convertView == null) {
        	viewHolder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.item_device_list, null);
            viewHolder.device_layout = convertView.findViewById(R.id.layout);
            viewHolder.device_connect_img = convertView.findViewById(R.id.connect_img);
            viewHolder.device_type_img = convertView.findViewById(R.id.device_type_img);
            viewHolder.device_name_tv = convertView.findViewById(R.id.device_name_tv);
            viewHolder.device_learn = convertView.findViewById(R.id.learn_btn);
            viewHolder.device_edit = convertView.findViewById(R.id.edit_btn);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        DeviceBean bean = mlists.get(position);
        viewHolder.device_connect_img.setEnabled(bean.isOnline());
        viewHolder.device_name_tv.setText(bean.getDeviceName());

        int imgId = getImgByDeviceType(bean);
        viewHolder.device_type_img.setImageResource(imgId);
        setItemListenerByInfo(viewHolder,mlists.get(position),convertView);

        return convertView;
    }

    private void setItemListenerByInfo(ViewHolder viewHolder,final DeviceBean bean,View convertView){

        final View finalCloseView = convertView;

        switch (bean.getTypeCode()) {
            case "Camera":
            case "Doorbell":
                viewHolder.device_learn.setVisibility(View.VISIBLE);
                viewHolder.device_learn.setText("回放");

                //type 1: 播放； 2：回放； 3：设置
                viewHolder.device_layout.setOnClickListener(new OnMultiClickListener() {
                    @Override
                    public void onMultiClick(View v) {
                        getAccessToken(bean.getDeviceSerial(),1,null);
                    }
                }) ;

                viewHolder.device_learn.setOnClickListener(new OnMultiClickListener() {
                    @Override
                    public void onMultiClick(View v) {
                        getAccessToken(bean.getDeviceSerial(),2,null);
                    }
                });

                viewHolder.device_edit.setOnClickListener(new OnMultiClickListener() {
                    @Override
                    public void onMultiClick(View v) {
                        getAccessToken(bean.getDeviceSerial(),3,bean);
                        ((SwipeMenuLayout)(finalCloseView)).quickClose();// 关闭侧滑菜单

                    }
                });

                break;
            case "SwitchInfo":
                viewHolder.device_learn.setVisibility(View.INVISIBLE);
                viewHolder.device_layout.setOnClickListener(new OnMultiClickListener() {
                    @Override
                    public void onMultiClick(View v) {
                        playBeepSoundAndVibrate();
                        openSwitch(bean);
                    }
                }) ;
                viewHolder.device_edit.setOnClickListener(new OnMultiClickListener() {
                    @Override
                    public void onMultiClick(View v) {
                        Intent intent = new Intent(mContext, DeviceEditActivity.class);
                        intent.putExtra("deviceId",bean.getDeviceId());
                        intent.putExtra("propId",bean.getPropId());
                        intent.putExtra("deviceName",bean.getDeviceName());
                        intent.putExtra("roomId",mFragment.getCurrentRoomId());
                        mContext.startActivity(intent);
                        ((SwipeMenuLayout)(finalCloseView)).quickClose();// 关闭侧滑菜单

                    }
                });

                break;
            case "LightingControl":
                viewHolder.device_learn.setVisibility(View.INVISIBLE);

                viewHolder.device_layout.setOnClickListener(new OnMultiClickListener() {
                    @Override
                    public void onMultiClick(View v) {
                        Intent intent = new Intent(mContext, OpenWindowActivity.class);
                        intent.putExtra("deviceId",bean.getDeviceId());
                        intent.putExtra("propId",bean.getPropId());
                        intent.putExtra("subType",bean.getSubType());
                        intent.putExtra("titleName",bean.getDeviceName());
                        mContext.startActivity(intent);
                    }
                }) ;
                viewHolder.device_edit.setOnClickListener(new OnMultiClickListener() {
                    @Override
                    public void onMultiClick(View v) {
                        Intent intent = new Intent(mContext, DeviceEditActivity.class);
                        intent.putExtra("deviceId",bean.getDeviceId());
                        intent.putExtra("propId",bean.getPropId());
                        intent.putExtra("deviceName",bean.getDeviceName());
                        intent.putExtra("roomId",mFragment.getCurrentRoomId());
                        mContext.startActivity(intent);
                        ((SwipeMenuLayout)(finalCloseView)).quickClose();// 关闭侧滑菜单

                    }
                });

                break;
            case "DoorLock":
                viewHolder.device_learn.setVisibility(View.INVISIBLE);
                viewHolder.device_layout.setOnClickListener(new OnMultiClickListener() {
                    @Override
                    public void onMultiClick(View v) {
                        Intent intent = new Intent(mContext, DoorLockActivity.class);
                        intent.putExtra("deviceId",bean.getDeviceId());
                        intent.putExtra("propId",bean.getPropId());
                        intent.putExtra("titleName",bean.getDeviceName());
                        mContext.startActivity(intent);
                    }
                }) ;
                viewHolder.device_edit.setOnClickListener(new OnMultiClickListener() {
                    @Override
                    public void onMultiClick(View v) {
                        Intent intent = new Intent(mContext, DeviceEditActivity.class);
                        intent.putExtra("deviceId",bean.getDeviceId());
                        intent.putExtra("propId",bean.getPropId());
                        intent.putExtra("deviceName",bean.getDeviceName());
                        intent.putExtra("roomId",mFragment.getCurrentRoomId());
                        mContext.startActivity(intent);
                        ((SwipeMenuLayout)(finalCloseView)).quickClose();// 关闭侧滑菜单

                    }
                });

                break;
            case "RemoteController":
                viewHolder.device_learn.setVisibility(View.VISIBLE);
                viewHolder.device_learn.setText("红外学习");


                viewHolder.device_layout.setOnClickListener(new OnMultiClickListener() {
                    @Override
                    public void onMultiClick(View v) {
                        if(bean.getSubType().equalsIgnoreCase("AirRemoteController")){
                            Intent intent = new Intent(mContext, AirConditionControlActivity.class);
                            intent.putExtra("deviceId",bean.getDeviceId());
                            intent.putExtra("propId",bean.getPropId());
                            intent.putExtra("commandType","F0");
                            intent.putExtra("titleName",bean.getDeviceName());
                            mContext.startActivity(intent);
                        } else if(bean.getSubType().equalsIgnoreCase("TvRemoteController")){
                            Intent intent = new Intent(mContext, TvControlActivity.class);
                            intent.putExtra("deviceId",bean.getDeviceId());
                            intent.putExtra("propId",bean.getPropId());
                            intent.putExtra("commandType","F0");
                            intent.putExtra("titleName",bean.getDeviceName());
                            mContext.startActivity(intent);
                        }
                    }
                }) ;

                viewHolder.device_learn.setOnClickListener(new OnMultiClickListener() {
                    @Override
                    public void onMultiClick(View v) {
                        if(bean.getSubType().equalsIgnoreCase("AirRemoteController")){
                            Intent intent = new Intent(mContext, AirConditionControlActivity.class);
                            intent.putExtra("deviceId",bean.getDeviceId());
                            intent.putExtra("propId",bean.getPropId());
                            intent.putExtra("commandType","F1");
                            intent.putExtra("titleName",bean.getDeviceName());
                            mContext.startActivity(intent);
                        } else if(bean.getSubType().equalsIgnoreCase("TvRemoteController")){
                            Intent intent = new Intent(mContext, TvControlActivity.class);
                            intent.putExtra("deviceId",bean.getDeviceId());
                            intent.putExtra("propId",bean.getPropId());
                            intent.putExtra("commandType","F1");
                            intent.putExtra("titleName",bean.getDeviceName());
                            mContext.startActivity(intent);
                        }
                    }
                });

                viewHolder.device_edit.setOnClickListener(new OnMultiClickListener() {
                    @Override
                    public void onMultiClick(View v) {
                        Intent intent = new Intent(mContext, DeviceEditActivity.class);
                        intent.putExtra("deviceId",bean.getDeviceId());
                        intent.putExtra("propId",bean.getPropId());
                        intent.putExtra("deviceName",bean.getDeviceName());
                        intent.putExtra("roomId",mFragment.getCurrentRoomId());
                        mContext.startActivity(intent);
                        ((SwipeMenuLayout)(finalCloseView)).quickClose();// 关闭侧滑菜单

                    }
                });

                break;
        }


    }

    private int getImgByDeviceType(DeviceBean bean){

        int imgId = R.drawable.ic_remote_control;
        switch (bean.getTypeCode()){
            case "Camera":
                imgId = R.drawable.ic_camera_on;
                break;
            case "Doorbell":
                imgId = R.drawable.ic_doorbell;
                break;
            case "SwitchInfo":
                if(bean.getStatus().equalsIgnoreCase("0")){
                    imgId = R.drawable.ic_switch_off;
                }else {
                    imgId = R.drawable.ic_switch_on;
                }
                break;
            case "LightingControl":
                // subType = 2: 窗帘, 4: 推窗器
                if(bean.getSubType().equalsIgnoreCase("2")){
                    imgId = R.drawable.ic_chuanlian;
                } else if(bean.getSubType().equalsIgnoreCase("4")){
                    imgId = R.drawable.ic_window;
                }
                break;
            case "DoorLock":
                imgId = R.drawable.ic_door_lock;
                break;
            case "RemoteController":
                if(bean.getSubType().equalsIgnoreCase("AirRemoteController")){
                    imgId = R.drawable.ic_airconditioner;
                } else if(bean.getSubType().equalsIgnoreCase("TvRemoteController")){
                    imgId = R.drawable.ic_television;
                }
                break;

        }

        return imgId;

    }


    String TAG = "wang";



    private void playCamera(String accessToken, final String deviceSerial,
                            final int type,final DeviceBean bean){
        EZOpenSDK.getInstance().setAccessToken(accessToken);

        new AsyncTask<String,Void,String>(){

            @Override
            protected String doInBackground(String... strings) {
                EZDeviceInfo deviceInfo = null;
                try {
                    deviceInfo = EZOpenSDK.getInstance().getDeviceInfo(deviceSerial);
                } catch (BaseException e) {
                    e.printStackTrace();
                }

                if (deviceInfo == null){
                    return "没找到相应的设备";
                }

                if (deviceInfo.getCameraNum() <= 0 || deviceInfo.getCameraInfoList() == null || deviceInfo.getCameraInfoList().size() <= 0) {
                    return "cameralist is null or cameralist size is 0";
                }
                if (deviceInfo.getCameraNum() != 0 && deviceInfo.getCameraInfoList() != null && deviceInfo.getCameraInfoList().size() != 0) {
                    final EZCameraInfo cameraInfo = EZUtils.getCameraInfoFromDevice(deviceInfo, 0);
                    if (cameraInfo == null) {
                        return "cameraInfo is null";
                    }
                    int ret = EZStreamClientManager.create(getApplication().getApplicationContext()).clearTokens();
                    if (EZ_OK == ret){
                        Log.i(TAG, "clearTokens: ok");
                    }else{
                        Log.e(TAG, "clearTokens: fail");
                    }

                    if(type == 2){
                        Intent intent = new Intent(mContext, EZPlayBackListActivity.class);
                        intent.putExtra(RemoteListContant.QUERY_DATE_INTENT_KEY, DateTimeUtil.getNow());
                        intent.putExtra(IntentConsts.EXTRA_CAMERA_INFO, cameraInfo);
                        mContext.startActivity(intent);

                    }else if(type == 1){
                        Intent intent = new Intent(mContext, EZRealPlayActivity.class);
                        intent.putExtra(IntentConsts.EXTRA_CAMERA_INFO, cameraInfo);
                        intent.putExtra(IntentConsts.EXTRA_DEVICE_INFO, deviceInfo);
                        mContext.startActivity(intent);

                    }else if(type == 3){
                        Intent intent = new Intent(mContext,
                                com.qqcs.smartHouse.activity.EZDeviceSettingActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putParcelable(IntentConsts.EXTRA_DEVICE_INFO,deviceInfo);
                        intent.putExtra("Bundle",bundle);

                        intent.putExtra("deviceId",bean.getDeviceId());
                        intent.putExtra("propId",bean.getPropId());
                        intent.putExtra("deviceName",bean.getDeviceName());
                        intent.putExtra("roomId",mFragment.getCurrentRoomId());
                        mContext.startActivity(intent);


                    }
                }
                return null;
            }

            @Override
            protected void onPostExecute(String msg) {
                if(!TextUtils.isEmpty(msg)){
                    ToastUtil.showToast(mContext,msg);
                }
            }


        }.execute();

    }



    private class ViewHolder {
        RelativeLayout device_layout;
        ImageView device_connect_img;
        ImageView device_type_img;
        TextView device_name_tv;
        Button device_learn;
        Button device_edit;

    }


    private void openSwitch(DeviceBean bean) {

        String accessToken = (String) SharePreferenceUtil.
                get(mContext, SP_Constants.ACCESS_TOKEN,"");

        String propValue;
        if(bean.getStatus().equalsIgnoreCase("0")){
            propValue = "1";
        }else {
            propValue = "0";
        }

        String timestamp = System.currentTimeMillis() + "";
        JSONObject object = CommonUtil.getRequstJson(mContext);
        JSONObject dataObject = new JSONObject();

        try {
            dataObject.put("propId", bean.getPropId());
            dataObject.put("propValue", propValue);
            dataObject.put("timestamp", timestamp);

            object.put("data", dataObject);
            object.put("sign", MD5Utils.md5s(bean.getPropId() + propValue + timestamp));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        OkHttpUtils
                .postString()
                .tag(this)
                .url(Constants.HTTP_DEVICE_COMMAND)
                .addHeader("access-token",accessToken)
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .content(object.toString())
                .build()
                .execute(new MyStringCallback<Object>(mContext,
                        Object.class, true, false) {
                    @Override
                    public void onSuccess(Object data) {
                        ToastUtil.showToast(mContext,"发送成功");
                        final Timer t = new Timer();
                        t.schedule(new TimerTask() {
                            public void run() {
                                EventBus.getDefault().post(new EventBusBean(EventBusBean.REFRESH_HOME));

                                t.cancel();
                            }
                        }, Constants.REFRESH_TIME);
                    }

                    @Override
                    public void onFailure(String message) {
                        ToastUtil.showToast(mContext, message);
                    }
                });
    }

    private void getAccessToken(final String deviceSerial, final int type, final DeviceBean bean) {

        String accessToken = (String) SharePreferenceUtil.
                get(mContext, SP_Constants.ACCESS_TOKEN,"");

        String timestamp = System.currentTimeMillis() + "";
        JSONObject object = CommonUtil.getRequstJson(mContext);
        JSONObject dataObject = new JSONObject();

        try {
            dataObject.put("timestamp", timestamp);
            object.put("data", dataObject);
            object.put("sign", MD5Utils.md5s(timestamp));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        OkHttpUtils
                .postString()
                .tag(this)
                .url(Constants.HTTP_GET_ACCESSTOKEN)
                .addHeader("access-token",accessToken)
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .content(object.toString())
                .build()
                .execute(new MyStringCallback<AccessTokenBean>(mContext,
                        AccessTokenBean.class, true, false) {
                    @Override
                    public void onSuccess(AccessTokenBean data) {
                        playCamera(data.getAccessToken(),deviceSerial,type,bean);

                    }

                    @Override
                    public void onFailure(String message) {
                        ToastUtil.showToast(mContext, message);
                    }
                });
    }

    private void initBeepSound() {
        if ( mediaPlayer == null) {
            // The volume on STREAM_SYSTEM is not adjustable, and users found it
            // too loud,
            // so we now play on the music stream.
            ((Activity)mContext).setVolumeControlStream(AudioManager.STREAM_MUSIC);
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setOnCompletionListener(beepListener);

            AssetFileDescriptor file = mContext.getResources().openRawResourceFd(
                    R.raw.beep);
            try {
                mediaPlayer.setDataSource(file.getFileDescriptor(),
                        file.getStartOffset(), file.getLength());
                file.close();
                mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
                mediaPlayer.prepare();
            } catch (IOException e) {
                mediaPlayer = null;
            }
        }
    }
    private void playBeepSoundAndVibrate() {
        if ( mediaPlayer != null) {
            mediaPlayer.start();
        }

    }

    /**
     * When the beep has finished playing, rewind to queue up another one.
     */
    private final MediaPlayer.OnCompletionListener beepListener = new MediaPlayer.OnCompletionListener() {
        public void onCompletion(MediaPlayer mediaPlayer) {
            mediaPlayer.seekTo(0);
        }
    };

}
