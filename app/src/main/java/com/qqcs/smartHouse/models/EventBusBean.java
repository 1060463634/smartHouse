package com.qqcs.smartHouse.models;

public class EventBusBean {
    //切换家庭时间
    public static final String FAMILY_ID_CHANGED = "familyidChanged";
    //
    public static final String REFRESH_HOME = "1";
    //
    public static final String REFRESH_PROPT = "2";
    //
    public static final String REFRESH_HOME_AND_PROPT = "3";

    //收到户主同意申请
    public static final String ACCEPT_BY_HOST = "accept_by_host";


    private String type;

    public EventBusBean(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
