package com.kaer.more.entitiy;

import java.util.ArrayList;

public class AdvertisementListData {
    private ArrayList<AdvertisementData> adList;
    private boolean isOK;

    public ArrayList<AdvertisementData> getAdList() {
        return adList;
    }

    public void setAdList(ArrayList<AdvertisementData> adList) {
        this.adList = adList;
    }

    public boolean isOK() {
        return isOK;
    }

    public void setOK(boolean OK) {
        isOK = OK;
    }

    @Override
    public String toString() {
        return "AdvertisementListData{" +
                "adList=" + adList +
                ", isOK=" + isOK +
                '}';
    }
}
