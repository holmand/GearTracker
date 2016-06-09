package com.daveholman.geartracker.models;

import java.util.ArrayList;

/**
 * Created by dholman on 6/6/16.
 */
public class UserGearData {

    public UserGearData() {
        // empty default constructor, necessary for Firebase to be able to deserialize
    }

    private String updateDate;
    private ArrayList<GearData> gearDataList;

    public String getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(String updateDate) {
        this.updateDate = updateDate;
    }


    public ArrayList<GearData> getGearDataList() {
        return gearDataList;
    }

    public void setGearDataList(ArrayList<GearData> gearDataList) {
        this.gearDataList = gearDataList;
    }

}

