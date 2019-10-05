package com.qqcs.smartHouse.models;

import java.util.List;

public class RoomListBean {
    private String familyId;
    private String familyName;
    private String familyImg;
    private List<RoomBean> rooms;

    public String getFamilyId() {
        return familyId;
    }

    public void setFamilyId(String familyId) {
        this.familyId = familyId;
    }

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

    public List<RoomBean> getRooms() {
        return rooms;
    }

    public void setRooms(List<RoomBean> rooms) {
        this.rooms = rooms;
    }
}