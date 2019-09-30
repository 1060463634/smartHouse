package com.qqcs.smartHouse.application;

import android.os.Environment;


public class Constants {
    public static final String FILE_DIR = Environment
            .getExternalStorageDirectory().getAbsolutePath() + "/smartHouse/";

    /**
     * 网络请求常量
     */
    public static final String PASSWORD_ENCRYPT_SEED = "Wxyxj2019"; //密码加密种子
    public static final String HTTP_SERVER_DOMAIN = "http://47.98.198.201:8082";
    public static final String HTTP_URL_SEND_CODE = HTTP_SERVER_DOMAIN + "/app/auth/sendValidateCode";
    public static final String HTTP_URL_LOGIN = HTTP_SERVER_DOMAIN + "/app/auth/login";
    public static final String HTTP_UPLOAD_PICTURE = HTTP_SERVER_DOMAIN + "/file/upload/picture";
    public static final String HTTP_CREATE_FAMILY = HTTP_SERVER_DOMAIN + "/app/family/create";
    public static final String HTTP_LIST_FAMILY = HTTP_SERVER_DOMAIN + "/app/family/member/listByFamily";
    public static final String HTTP_ADD_FAMILY_MEMBER = HTTP_SERVER_DOMAIN + "/app/family/member/create";
    public static final String HTTP_DELETE_FAMILY_MEMBER = HTTP_SERVER_DOMAIN + "/app/family/member/delete";
    public static final String HTTP_GET_CURRENT_INFO = HTTP_SERVER_DOMAIN + "/app/user/getCurrentInfo";
    public static final String HTTP_GET_FAMILY_LIST = HTTP_SERVER_DOMAIN + "/app/family/list";
    public static final String HTTP_DELETE_FAMILY = HTTP_SERVER_DOMAIN + "/app/family/delete";
    public static final String HTTP_QUIT_FAMILY = HTTP_SERVER_DOMAIN + "/app/family/member/quit";


    /**
     * 普通常量
     */
    public static  String ROLE_LOAD = "0"; //户主
    public static  String ROLE_MANAGE = "1"; //管理员
    public static  String ROLE_NORMAL = "2"; //普通成员



}

