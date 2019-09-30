package com.qqcs.smartHouse.models;

public class CurUserInfoBean {
    private String familyName;
    private String familyImg;
    private String unreadCount;
    private String appVersion;
    private String userRole;

    public String getFamilyName() {
        return familyName;
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }

    public String getFamilyImg() {
        return familyImg;
    }

    public void setFamilyImg(String familyImg) {
        this.familyImg = familyImg;
    }

    public String getUnreadCount() {
        return unreadCount;
    }

    public void setUnreadCount(String unreadCount) {
        this.unreadCount = unreadCount;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    public String getUserRole() {
        return userRole;
    }

    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }
}