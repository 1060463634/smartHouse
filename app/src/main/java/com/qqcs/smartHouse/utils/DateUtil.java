package com.qqcs.smartHouse.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;


public class DateUtil {

	private final static String TAG = "DateUtil";

	public final static SimpleDateFormat datetimeSdf = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");
	public final static SimpleDateFormat dateSdf = new SimpleDateFormat(
			"yyyy-MM-dd");
	public final static SimpleDateFormat dateSdfSlash = new SimpleDateFormat(
			"yyyy/MM/dd");
	public final static SimpleDateFormat monthdaySdf = new SimpleDateFormat(
			"MM-dd");
	public final static SimpleDateFormat timeSfd = new SimpleDateFormat("HH:mm");

	/**
	 * 按传输时间返回时间的下一天
	 * 
	 * @param date
	 *            传输时间
	 * @param day
	 *            天数左右移动，0-返回当前天
	 * @return 下一天
	 */
	public static String getNextDay(Date date, int day) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
//		calendar.roll(Calendar.DAY_OF_YEAR, day);
		calendar.add(Calendar.DAY_OF_YEAR, day);
		return datetimeSdf.format(calendar.getTime());
	}

	public static Date getNextDate(Date date, int day) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.DAY_OF_YEAR, day);
//		calendar.roll(Calendar.DAY_OF_YEAR, day);
		return calendar.getTime();
	}

	/**
	 * 按传输时间返回时间的下一月
	 * 
	 * @param date
	 *            传输时间
	 * @param month
	 *            月数左右移动，0-返回当前月
	 * @return 下一月
	 */
	public static String getNextMonth(Date date, int month) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
//		calendar.roll(Calendar.MONTH, month);
		calendar.add(Calendar.MONTH, month);
		return datetimeSdf.format(calendar.getTime());
	}

	/**
	 * 按传输判断是否工作日
	 * 
	 * @param date
	 *            传输时间
	 * @return 是否工作日
	 */
	public static boolean isWorkDate(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int day = calendar.get(Calendar.DAY_OF_WEEK);
		if (!(day == Calendar.SUNDAY || day == Calendar.SATURDAY)) {
			return false;
		}
		return true;
	}

	/**
	 * 按传输时间返回下一周的这一天
	 * 
	 * @param
	 *            周数左右移动，0-返回当前周
	 * @return 下一周
	 */
	public static String getNextWeek(Date date, int week) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
//		calendar.roll(Calendar.WEEK_OF_YEAR, week);
		calendar.add(Calendar.WEEK_OF_YEAR, week);
		return datetimeSdf.format(calendar.getTime());
	}

	/**
	 * 按传输时间返每周工作日，过去工作日不返回
	 * 
	 * @throws Exception
	 * @return 工作日
	 */
	public static List<String> getWorkDay(Date date, int week) throws Exception {
		List<String> list = new ArrayList<String>();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
//		calendar.roll(Calendar.WEEK_OF_YEAR, week);
		calendar.add(Calendar.WEEK_OF_YEAR, week);
		if (week == 0) {
			int day = calendar.get(Calendar.DAY_OF_WEEK);
			// 是否工作日，是工作日的放入list集合放好
			while (!(day == Calendar.SUNDAY || day == Calendar.SATURDAY)) {
				list.add(datetimeSdf.format(calendar.getTime()));
//				calendar.roll(Calendar.DAY_OF_YEAR, 1);
				calendar.add(Calendar.DAY_OF_YEAR, 1);
				day = calendar.get(Calendar.DAY_OF_WEEK);
			}
		} else {
			// 获取下周一时间
//			calendar.roll(Calendar.WEEK_OF_YEAR, -1);
			calendar.add(Calendar.WEEK_OF_YEAR, -1);
			int weeks = calendar.get(Calendar.DAY_OF_WEEK);
			if (weeks > 2) {
				calendar.add(Calendar.DAY_OF_MONTH, -(weeks - 2) + 7);
			} else {
				calendar.add(Calendar.DAY_OF_MONTH, 2 - weeks + 7);
			}
			// 获取下周一至五工作日
			list = getWorkDay(calendar.getTime(), 0);
		}
		return list;
	}

	public static String[] weekName = { "周日", "周一", "周二", "周三", "周四", "周五",
			"周六" };

	public static int getMonthDays(int year, int month) {
		if (month > 12) {
			month = 1;
			year += 1;
		} else if (month < 1) {
			month = 12;
			year -= 1;
		}
		int[] arr = { 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };
		int days = 0;

		if ((year % 4 == 0 && year % 100 != 0) || year % 400 == 0) {
			arr[1] = 29; // 闰年2月29天
		}

		try {
			days = arr[month - 1];
		} catch (Exception e) {
			e.getStackTrace();
		}

		return days;
	}

	public static int getYear() {
		return Calendar.getInstance().get(Calendar.YEAR);
	}
	
	public static int getYear(int offset){
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.MONTH, offset);
		return calendar.get(Calendar.YEAR);
		
	}

	public static int getMonth() {
		return Calendar.getInstance().get(Calendar.MONTH) + 1;
	}
	
	public static int getMonth(int offset) {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.MONTH, offset);
		return calendar.get(Calendar.MONTH) + 1;
	}

	public static int getCurrentMonthDay() {
		return Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
	}
	
	public static int getCurrentMonthDay(int offset) {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.MONTH, offset);
		return calendar.get(Calendar.DAY_OF_MONTH) ;
	}

	public static int getWeekDay() {
		return Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
	}
	
	public static int getWeekDay(int offset) {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.MONTH, offset);
		return calendar.get(Calendar.DAY_OF_WEEK) ;
	}

	public static int getHour() {
		return Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
	}

	public static int getMinute() {
		return Calendar.getInstance().get(Calendar.MINUTE);
	}


	public static int[] getWeekSunday(int year, int month, int day, int pervious) {
		int[] time = new int[3];
		Calendar c = Calendar.getInstance();
		c.set(Calendar.YEAR, year);
		c.set(Calendar.MONTH, month);
		c.set(Calendar.DAY_OF_MONTH, day);
		c.add(Calendar.DAY_OF_MONTH, pervious);
		time[0] = c.get(Calendar.YEAR);
		time[1] = c.get(Calendar.MONTH) + 1;
		time[2] = c.get(Calendar.DAY_OF_MONTH);
		return time;

	}

	public static int getWeekDayFromDate(int year, int month) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(getDateFromString(year, month));
		int week_index = cal.get(Calendar.DAY_OF_WEEK);
		if(week_index == 1){
			return 6;
		}else if(week_index == 2){
			return 7;
		}else{
			week_index = cal.get(Calendar.DAY_OF_WEEK) - 2;
			if (week_index < 0) {
				week_index = 0;
			}
			return week_index;
		}
		
	}

	@SuppressLint("SimpleDateFormat")
	public static Date getDateFromString(int year, int month) {
		String dateString = year + "-" + (month > 9 ? month : ("0" + month))
				+ "-01";
		Date date = null;
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			date = sdf.parse(dateString);
		} catch (ParseException e) {
			System.out.println(e.getMessage());
		}
		return date;
	}

	/**
	 * Fomart String as {@value #DATE_FORAMT_STRING} to {@code Date}. If convert
	 * succeeed, return Date, else return null;
	 * 
	 * @param string
	 * @return {@code Date}
	 */
	public static Date string2TimeStamp(String string) {

		try {
			return (Date) datetimeSdf.parse(string);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	// change yyyy-MM-dd HH:mm:ss to millisecond

	public static long string2MSecTime(String string) {
		Date date = string2TimeStamp(string);
		if (date != null) {
			return date.getTime();
		} else {
			return -1;
		}
	}

	public static Date string2Date(String string) {

		try {
			return (dateSdf).parse(string);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static long string2YMDMSTime(String string){
		Date date = string2Date(string);
		if (date != null) {
			return date.getTime();
		} else {
			return -1;
		}
	}

	// 2015-10-23 10:30:31 => 2015-10-23
	public static long dateTime2date(long dateTime) {
		// System.setProperty("user.timezone", "Asia/Shanghai");
		// TimeZone tz = TimeZone.getTimeZone("Asia/Shanghai");
		// TimeZone.setDefault(tz);

		Date dateTime_date = new Date(dateTime);
		String date_str = DateUtil.data2STString(dateTime_date);
		Date date_date = DateUtil.string2Date(date_str);
		long date = date_date.getTime();
		// LogUtil.log(
		// TAG,
		// "INPUT: " + date_str + " OUTPUT: "
		// + DateUtil.data2STString(date_date));
		return date;
	}

	public static String toDouble(String s) {
		if (s.length() == 1) {
			s = "0" + s;
		}
		return s;
	}

	public static String date2String(Date date) {
		return dateSdf.format(date);
	}
	
	public static String date2StringSlash(Date date) {
		return dateSdfSlash.format(date);
	}

	public static String date2MDString(Date date) {
		return monthdaySdf.format(date);
	}

	public static String data2STString(Date date) {
		return datetimeSdf.format(date);
	}

	public static String data2HMString(Date date) {
		return timeSfd.format(date);
	}
	
	public static String date2Time12FormatString(Date date){
		int hour = date.getHours();
		int minute = date.getMinutes();
		String minStr = "";
		
		if(minute < 10){
			minStr = "0" + minute;
		}else{
			minStr = "" + minute;
		}
		if(hour > 12){
			hour = hour - 12;
		}
		
		if(hour == 10 || hour == 11 || hour == 12){
			return hour + ":" + minStr;
		}else{
			return "0" + hour + ":" + minStr;
		}
		
	}
	
	public static String getFormattedLogDate() {
		return datetimeSdf.format(new Date());
	}
	
}
