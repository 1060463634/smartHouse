package com.qqcs.smartHouse.utils;


import android.text.TextUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InputCheckUtil {
    /**
     * 验证手机格式
     */
    public static boolean isMobileNO(String mobiles) {
        /*
		 * 移动：134、135、136、137、138、139、147、150、151、152、157、158、159、182、187、188
		 * 联通：130、131、132、152、155、156、185、186 电信：133、153、180、189、（1349卫通）
		 * 总结起来就是第一位必定为1，第二位必定为3或4或5或8，其他位置的可以为0-9
		 */
        String telRegex = "[1][34578]\\d{9}";// "[1]"代表第1位为数字1，"[3458]"代表第二位可以为3、4、5、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。
        if (TextUtils.isEmpty(mobiles))
            return false;
        else
            return mobiles.matches(telRegex);
    }

    public static boolean checkUserId(String user) {
        boolean flag = false;
        if (TextUtils.isEmpty(user))
            return false;
        try {
            String check = "^[a-zA-z][a-zA-Z0-9_-]*$";
            Pattern regex = Pattern.compile(check);
            Matcher matcher = regex.matcher(user);
            flag = matcher.matches();
        } catch (Exception e) {
            flag = false;
        }
        return flag;
    }

    /**
     * 验证姓名
     */
    public static boolean checkName(String name) {
        boolean flag = false;
        if (TextUtils.isEmpty(name))
            return false;
        try {
            String check = "^[\u4e00-\u9fa5a-zA-Z0-9_-]*$";
            Pattern regex = Pattern.compile(check);
            Matcher matcher = regex.matcher(name);
            flag = matcher.matches();
        } catch (Exception e) {
            flag = false;
        }
        return flag;
    }

    /**
     * 验证密码
     */
    public static boolean checkPassword(String password) {
        boolean flag = false;
        if (TextUtils.isEmpty(password))
            return false;
        try {
            String check = "^[a-zA-Z0-9_-]*$";
            Pattern regex = Pattern.compile(check);
            Matcher matcher = regex.matcher(password);
            flag = matcher.matches();
        } catch (Exception e) {
            flag = false;
        }
        return flag;
    }

    /**
     * 验证邮箱
     *
     * @param email
     * @return
     */
    public static boolean checkEmail(String email) {
        boolean flag = false;
        if (TextUtils.isEmpty(email))
            return false;
        try {
            String check = "^([a-z0-9A-Z]+[-|_|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
            Pattern regex = Pattern.compile(check);
            Matcher matcher = regex.matcher(email);
            flag = matcher.matches();
        } catch (Exception e) {
            flag = false;
        }
        return flag;
    }

    /**
     * 验证手机号码
     *
     * @param mobiles
     * @return
     */
    public static boolean checkMobileNumber(String mobileNumber) {
        boolean flag = false;
        if (TextUtils.isEmpty(mobileNumber))
            return false;
        try {
            Pattern regex = Pattern
                    .compile("^(((13[0-9])|(15([0-3]|[5-9]))|(17[0-9])|(18[0-9])|(14[0-9]))\\d{8})|(0\\d{2}-\\d{8})|(0\\d{3}-\\d{7})$");
            Matcher matcher = regex.matcher(mobileNumber);
            flag = matcher.matches();
        } catch (Exception e) {
            flag = false;
        }
        return flag;
    }

    /**
     * 验证手机号码或固话
     *
     * @param mobiles
     * @return
     */
    public static boolean checkPhoneNumber(String phoneNumber) {
        boolean flag = false;
        String expression = "(^(0[0-9]{2,3})?([0-9]{7,8})+(\\-[0-9]{1,4})?$)|(^(0[0-9]{2,3}\\-)?([0-9]{7,8})+(\\-[0-9]{1,4})?$)|(^(13[0-9]|14[5|7|9]|15[0|1|2|3|5|6|7|8|9]|17[0|5|6|7|8|9]|18[0-9])\\d{8}$)";
        CharSequence inputStr = phoneNumber;
        Pattern pattern = Pattern.compile(expression);
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches()) {
            flag = true;
        }
        return flag;
    }
}
