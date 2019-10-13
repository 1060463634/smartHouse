package com.qqcs.smartHouse.adapter;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Vibrator;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ezviz.stream.EZStreamClientManager;
import com.qqcs.smartHouse.R;
import com.qqcs.smartHouse.activity.AirConditionControlActivity;
import com.qqcs.smartHouse.activity.TvControlActivity;
import com.qqcs.smartHouse.application.Constants;
import com.qqcs.smartHouse.application.OnMultiClickListener;
import com.qqcs.smartHouse.application.SP_Constants;
import com.qqcs.smartHouse.models.DeviceBean;
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
import com.videogo.ui.realplay.EZRealPlayActivity;
import com.zhy.http.okhttp.OkHttpUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ezviz.ezopensdk.demo.SdkInitParams;
import ezviz.ezopensdk.demo.SdkInitTool;
import okhttp3.MediaType;

import static com.ezviz.stream.EZError.EZ_OK;
import static com.qqcs.smartHouse.application.MyApplication.getApplication;


public class DeviceListAdapter extends BaseAdapter {

    private Context mContext;
    private List<DeviceBean> mlists;
    private LayoutInflater mInflater;
    private MediaPlayer mediaPlayer;
    private static final float BEEP_VOLUME = 0.10f;

    public DeviceListAdapter(Context context, List<DeviceBean> lists) {
        mContext = context;
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
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        DeviceBean bean = mlists.get(position);
        viewHolder.device_connect_img.setEnabled(bean.isOnline());
        viewHolder.device_name_tv.setText(bean.getDeviceName());

        int imgId = getImgByDeviceType(bean);
        viewHolder.device_type_img.setImageResource(imgId);

        viewHolder.device_layout.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                controlDevice(mlists.get(position));
            }
        }) ;


        return convertView;
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
                imgId = R.drawable.ic_chuanlian;
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
    /**
     * 通过调用服务接口判断AppKey和AccessToken且有效
     * @return 是否依旧有效
     */
    private boolean checkAppKeyAndAccessToken() {
        boolean isValid = false;
        try {
            EzvizAPI.getInstance().getUserName();
            isValid = true;
        } catch (BaseException e) {
            e.printStackTrace();
            Log.e(TAG, "Error code is " + e.getErrorCode());
            int errCode = e.getErrorCode();
            String errMsg;
            switch (errCode){
                case 400031:
                    errMsg = "无网络连接";
                    break;
                default:
                    errMsg = "无效AppKey或AccessToken，请重新登录";
                    break;
            }
            ToastUtil.showToast(mContext,errMsg);
        }
        return isValid;
    }


    private void EZRealPlay() throws BaseException {
        List<EZDeviceInfo> result = null;
        result = EZOpenSDK.getInstance().getDeviceList(0, 20);

        final EZDeviceInfo deviceInfo = result.get(0);

        if (deviceInfo.getCameraNum() <= 0 || deviceInfo.getCameraInfoList() == null || deviceInfo.getCameraInfoList().size() <= 0) {
            LogUtil.d(TAG, "cameralist is null or cameralist size is 0");
            return;
        }
        if (deviceInfo.getCameraNum() == 1 && deviceInfo.getCameraInfoList() != null && deviceInfo.getCameraInfoList().size() == 1) {
            LogUtil.d(TAG, "cameralist have one camera");
            final EZCameraInfo cameraInfo = EZUtils.getCameraInfoFromDevice(deviceInfo, 0);
            if (cameraInfo == null) {
                return;
            }
            int ret = EZStreamClientManager.create(getApplication().getApplicationContext()).clearTokens();
            if (EZ_OK == ret){
                Log.i(TAG, "clearTokens: ok");
            }else{
                Log.e(TAG, "clearTokens: fail");
            }
            Intent intent = new Intent(mContext, EZRealPlayActivity.class);
            intent.putExtra(IntentConsts.EXTRA_CAMERA_INFO, cameraInfo);
            intent.putExtra(IntentConsts.EXTRA_DEVICE_INFO, deviceInfo);
            mContext.startActivity(intent);
            return;
        }


    }



    private void playCamera(){
        EZOpenSDK.getInstance().setAccessToken("at.bwef8xdq7yhh6qzf1q7qslyedzmh38cx-8380qes95g-1v2rxv7-iv1vy8kmq");

        if(EzvizAPI.getInstance().isLogin()) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    if (checkAppKeyAndAccessToken()){

                        try {
                            EZRealPlay();
                        } catch (BaseException e) {
                            e.printStackTrace();
                        }

                    }
                }
            }).start();
        }



    }

    private void controlDevice(DeviceBean bean){
        Intent intent;
        switch (bean.getTypeCode()) {
            case "Camera":
                playCamera();
                break;
            case "Doorbell":

                break;
            case "SwitchInfo":
                playBeepSoundAndVibrate();
                openSwitch(bean);
                break;
            case "LightingControl":

                break;
            case "DoorLock":

                break;
            case "RemoteController":
                if(bean.getSubType().equalsIgnoreCase("AirRemoteController")){

                    intent = new Intent(mContext, AirConditionControlActivity.class);
                    intent.putExtra("deviceId",bean.getDeviceId());
                    intent.putExtra("propId",bean.getPropId());
                    mContext.startActivity(intent);
                } else if(bean.getSubType().equalsIgnoreCase("TvRemoteController")){
                    intent = new Intent(mContext, TvControlActivity.class);
                    intent.putExtra("deviceId",bean.getDeviceId());
                    intent.putExtra("propId",bean.getPropId());
                    mContext.startActivity(intent);
                }

                break;
        }

    }

    private class ViewHolder {
        RelativeLayout device_layout;
        ImageView device_connect_img;
        ImageView device_type_img;
        TextView device_name_tv;

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
