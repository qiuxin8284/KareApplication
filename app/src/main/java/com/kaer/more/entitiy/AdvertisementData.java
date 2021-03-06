package com.kaer.more.entitiy;

public class AdvertisementData {
    private String adId;//广告ID
    private String name;//名称
    private int mediaType;//1文本，2图片，3视频
    private int adType;//1商业广告，2公益广告
    private int duration;//时长
    private int liveCount;//播放次数
    private String liveTime;//播放时间
    private String location;//定位
    private String limits;//范围（单位km)
    private String content;//广告内容
    private String media;//媒体文件

    @Override
    public String toString() {
        return "AdvertisementData{" +
                "adId='" + adId + '\'' +
                ", name='" + name + '\'' +
                ", mediaType=" + mediaType +
                ", adType=" + adType +
                ", duration=" + duration +
                ", liveCount=" + liveCount +
                ", liveTime='" + liveTime + '\'' +
                ", location='" + location + '\'' +
                ", limits='" + limits + '\'' +
                ", content='" + content + '\'' +
                ", media='" + media + '\'' +
                ", bigImage='" + bigImage + '\'' +
                ", level=" + level +
                ", isOK=" + isOK +
                '}';
    }

    public String getBigImage() {
        return bigImage;
    }

    public void setBigImage(String bigImage) {
        this.bigImage = bigImage;
    }

    private String bigImage;//媒体文件
    private int level;//优先级
    private boolean isOK;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isOK() {
        return isOK;
    }

    public void setOK(boolean OK) {
        isOK = OK;
    }

    public String getAdId() {
        return adId;
    }

    public void setAdId(String adId) {
        this.adId = adId;
    }

    public int getMediaType() {
        return mediaType;
    }

    public void setMediaType(int mediaType) {
        this.mediaType = mediaType;
    }

    public int getAdType() {
        return adType;
    }

    public void setAdType(int adType) {
        this.adType = adType;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getLiveCount() {
        return liveCount;
    }

    public void setLiveCount(int liveCount) {
        this.liveCount = liveCount;
    }

    public String getLiveTime() {
        return liveTime;
    }

    public void setLiveTime(String liveTime) {
        this.liveTime = liveTime;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getLimits() {
        return limits;
    }

    public void setLimits(String limits) {
        this.limits = limits;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getMedia() {
        return media;
    }

    public void setMedia(String media) {
        this.media = media;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

}
