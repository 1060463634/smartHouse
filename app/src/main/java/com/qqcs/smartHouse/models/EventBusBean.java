package com.qqcs.smartHouse.models;

public class EventBusBean {
    //
    public static final String FAMILY_ID_CHANGED = "familyidChanged";
    //
    public static final String REFRESH_HOME = "refreshHome";
    //
    public static final String REFRESH_PROPT = "refreshPropt";
    //
    public static final String REFRESH_HOME_AND_PROPT = "refreshHomeAndProp";



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
