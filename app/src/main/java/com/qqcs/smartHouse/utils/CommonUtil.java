package com.qqcs.smartHouse.utils;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.qqcs.smartHouse.application.SP_Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.content.Context.BATTERY_SERVICE;
import static android.content.Context.TELEPHONY_SERVICE;

public class CommonUtil {
	public final static SimpleDateFormat dateSdf = new SimpleDateFormat("yyyy-MM-dd");
	public final static SimpleDateFormat timeSfd = new SimpleDateFormat("HH:mm", Locale.CHINESE);

	public static String date2String(Date date) {
		return dateSdf.format(date);
	}
	public static String time2String(Date date) {
		return timeSfd.format(date);
	}

	public static int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}
	//通过uri获取图片bitmap
	public static Bitmap getBitmapFromUri(Uri uri, Context mContext, int zoom)
	{
		BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
		bitmapOptions.inSampleSize = zoom;
		try
		{
			// 读取uri所在的图片
			Bitmap bitmap = BitmapFactory.decodeStream
					(mContext.getContentResolver().openInputStream(uri), null , bitmapOptions);
			return bitmap;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}



	public static String str2HexStr(String str)
	{

		char[] chars = "0123456789ABCDEF".toCharArray();
		StringBuilder sb = new StringBuilder("");
		byte[] bs = str.getBytes();
		int bit;

		for (int i = 0; i < bs.length; i++)
		{
			bit = (bs[i] & 0x0f0) >> 4;
			sb.append(chars[bit]);
			bit = bs[i] & 0x0f;
			sb.append(chars[bit]);
		}

		for (int i = 0; i < 32 - str.length(); i++)
		{
			sb.append("00");
		}


		return sb.toString().trim();
	}


	public static String get00Str(int num)
	{

		StringBuilder sb = new StringBuilder("");


		for (int i = 0; i < num; i++)
		{
			sb.append("00");
		}

		return sb.toString().trim();
	}

	public static String getRandomStr(int num)
	{

		StringBuilder sb = new StringBuilder("");

		Random random = new Random();
		for (int i = 0; i < num; i++)
		{

			int r = random.nextInt(9);
			sb.append(r + "" + r);
		}

		return sb.toString().trim();
	}

	public static String getTimeHex()
	{
		Date date = new Date();
		String dateStr = DateUtil.data2STString(date);
		String dateHex1 = dateStr.replace("-","");
		String dateHex2 = dateHex1.replace(":","").replace(" ","");
		String dateHex3 = dateHex2.substring(dateHex2.length()- 12);

		return dateHex3;
	}

	public static String getHexSum(String str)
	{

		int sum = 0;
		for (int i = 0; i < str.length()/2; i++)
		{
			String sub = str.substring(i * 2,i * 2 + 2);
			int subDe =  hex2decimal(sub);
			sum = sum + subDe;
		}

		String result = demical2Hex(sum);
		if(result.length() >= 4 ){
			return str.substring(result.length()-4, result.length());
		}else{
			StringBuilder sb = new StringBuilder("");
			for (int i = 0; i < 4 - result.length(); i++)
			{
				sb.append("0");
			}

			return sb.toString().trim() + result;
		}
	}

	/**
	 * 16进制转10进制
	 *
	 * @param hex
	 * @return
	 */
	public static int hex2decimal(String hex) {
		return Integer.parseInt(hex, 16);
	}

	/**
	 * 10进制转16进制
	 * @param i
	 * @return
	 */
	public static String demical2Hex(int i) {
		String s = Integer.toHexString(i);
		return s;
	}


	public static String toDouble(String one){
		String str = one;
		if(str.length() == 1){
			str = "0" + str;
		}
		return str;
	}



	/**
	 * 判断是否安装APK的方法
	 */
	public static boolean isApkInstalled(Context context, String packageName) {
		try {
			context.getPackageManager().getApplicationInfo(packageName, PackageManager.GET_UNINSTALLED_PACKAGES);
			return true;
		} catch (PackageManager.NameNotFoundException e) {
			return false;
		}
	}

	/**
	 * 获取当前程序的版本号
	 */
	public static int getVersionCode(Context context){
		int localVersion = 0;
		try {
			PackageInfo packageInfo = context.getApplicationContext()
					.getPackageManager()
					.getPackageInfo(context.getPackageName(), 0);
			localVersion = packageInfo.versionCode;
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}
		return localVersion;
	}

	/**
	 * 获取当前程序的版本号
	 */
	public static String getVersionName(Context context){
		String versionName = "";
		try {
			PackageInfo packageInfo = context.getApplicationContext()
					.getPackageManager()
					.getPackageInfo(context.getPackageName(), 0);
			versionName = packageInfo.versionName;
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}
		return versionName;
	}

	/**
	 * 手机号验证
	 *
	 * @param
	 * @return 验证通过返回true
	 */
	public static boolean checkMobile(String phone) {
		if (TextUtils.isEmpty(phone)) {
			return false;
		}
		if (phone.length() != 11) {
			return false;
		}
		Pattern pattern = Pattern.compile("^[1][3,4,5,7,8][0-9]{9}$");

		Matcher matcher = pattern.matcher(phone);

		if (matcher.matches()) {
			return true;
		}
		return false;
	}






	public static boolean hasSdcard() {
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			return true;
		} else {
			return false;
		}
	}

	public static String getDeviceSn(Context context) {
		TelephonyManager TelephonyMgr = (TelephonyManager)
				context.getSystemService(TELEPHONY_SERVICE);
		String szImei = TelephonyMgr.getDeviceId();
		return szImei;
	}

	public static String getUUID(Context context) {
		final TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

		final String tmDevice, tmSerial, tmPhone, androidId;
		tmDevice = "" + tm.getDeviceId();
		tmSerial = "" + tm.getSimSerialNumber();
		androidId = "" + android.provider.Settings.
				Secure.getString(context.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);

		UUID deviceUuid = new UUID(androidId.hashCode(), ((long)tmDevice.hashCode() << 32) | tmSerial.hashCode());
		String uniqueId = deviceUuid.toString();
		return uniqueId;
	}






	private static int getBatteryLevel(Context context) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			BatteryManager batteryManager = (BatteryManager) context.getSystemService(BATTERY_SERVICE);
			return batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
		} else {
			Intent intent = new ContextWrapper(context).
					registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
			return (intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) * 100) /
					intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
		}
	}


	public static JSONObject getRequstJson(Context context) {
		String lng = (String) SharePreferenceUtil.
				get(context, SP_Constants.LOCATION_LNG,"");
		String lat = (String) SharePreferenceUtil.
				get(context, SP_Constants.LOCATION_LAT,"");
		String city = (String) SharePreferenceUtil.
				get(context, SP_Constants.LOCATION_CITY,"");


		JSONObject object = new JSONObject();
		JSONObject deviceObject = new JSONObject();

		try {
			deviceObject.put("client", "android");
			deviceObject.put("d_brand", Build.BRAND +"");
			deviceObject.put("d_model", android.os.Build.MODEL +"");
			deviceObject.put("os_version", android.os.Build.VERSION.RELEASE);
			deviceObject.put("network_type", NetworkUtil.getNetworkType(context));
			deviceObject.put("lng", lng);
			deviceObject.put("lat", lat);
			deviceObject.put("app_version", getVersionName(context));
			deviceObject.put("city_code", city);
			deviceObject.put("electricity", getBatteryLevel(context));
			deviceObject.put("screen", ScreenUtil.getInstance()
					.getScreenWidth(context)+"*" + ScreenUtil.getInstance().getScreenHeight(context));
			deviceObject.put("uuid", getDeviceSn(context));
			object.put("device",deviceObject);
			object.put("rest_version","1.0");

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return object;
	}

}