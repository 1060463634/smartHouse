package com.qqcs.smartHouse.models;

import java.util.List;

public class LoginBean {
    private String firstAddTime;
    private String lastAddTime;
    private String uid;           // 用户id
    private String accessToken;
    private String mobile;
    private String nickName;
    private String userRole;     //0:户主，1:管理员， 2:普通成员
    private boolean hasFamily;

    private List<FamilyInfoBean> familyInfo;

    public String getFirstAddTime() {
        return firstAddTime;
    }

    public void setFirstAddTime(String firstAddTime) {
        this.firstAddTime = firstAddTime;
    }

    public String getLastAddTime() {
        return lastAddTime;
    }

    public void setLastAddTime(String lastAddTime) {
        this.lastAddTime = lastAddTime;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public boolean isHasFamily() {
        return hasFamily;
    }

    public void setHasFamily(boolean hasFamily) {
        this.hasFamily = hasFamily;
    }

    public String getUserRole() {
        return userRole;
    }

    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }

    public List<FamilyInfoBean> getFamilyInfo() {
        return familyInfo;
    }

    public void setFamilyInfo(List<FamilyInfoBean> familyInfo) {
        this.familyInfo = familyInfo;
    }
}