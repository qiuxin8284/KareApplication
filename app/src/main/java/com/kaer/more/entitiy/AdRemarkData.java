package com.kaer.more.entitiy;

public class AdRemarkData {
    private String adId;//广告ID
    private int allCount;//总播放次数
    private int allTime;//总播放时长

    public String getAdId() {
        return adId;
    }

    public void setAdId(String adId) {
        this.adId = adId;
    }

    public int getAllCount() {
        return allCount;
    }

    public void setAllCount(int allCount) {
        this.allCount = allCount;
    }

    public int getAllTime() {
        return allTime;
    }

    public void setAllTime(int allTime) {
        this.allTime = allTime;
    }

    @Override
    public String toString() {
        return "AdRemarkData{" +
                "adId=" + adId +
                ", allCount=" + allCount +
                ", allTime=" + allTime +
                '}';
    }
}
