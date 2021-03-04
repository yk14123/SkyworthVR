package com.chinafocus.hvrskyworthvr.model.bean;

public class VideoDetail {
    /**
     * imgUrl : test-temp-cover/700X400/daoxiang.jpg
     * duration : 217
     * title : 稻香十里迎丰年
     * description : 刚刚种植下的水稻，从春天的灌溉到夏天的养护，到秋天收获的季节，带着满满的稻香扑面而来。
     * copyright :
     * videoUrl : test-temp-mobile/daoxiang.mp4
     * audioUrl : null
     * subtitle : test-temp-subtitle/daoxiang.ass
     * saleStartTime : null
     * saleEndTime : null
     * startTime : 2020-11-11T05:51:04.000+0000
     * endTime : 2022-12-12T00:02:08.000+0000
     * createTime : 2020-12-11T09:37:31.000+0000
     * updateTime : 2021-01-13T06:12:02.000+0000
     * id : 10106
     * nextId : 10107
     */

    private String imgUrl;
    private int duration;
    private String title;
    private String description;
    private String copyright;
    private String videoUrl;
    private Object audioUrl;
    private String subtitle;
    private Object saleStartTime;
    private Object saleEndTime;
    private String startTime;
    private String endTime;
    private String createTime;
    private String updateTime;
    private int id;
    private int nextId;
    private int nextType;

    public int getNextType() {
        return nextType;
    }

    public void setNextType(int nextType) {
        this.nextType = nextType;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCopyright() {
        return copyright;
    }

    public void setCopyright(String copyright) {
        this.copyright = copyright;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public Object getAudioUrl() {
        return audioUrl;
    }

    public void setAudioUrl(Object audioUrl) {
        this.audioUrl = audioUrl;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public Object getSaleStartTime() {
        return saleStartTime;
    }

    public void setSaleStartTime(Object saleStartTime) {
        this.saleStartTime = saleStartTime;
    }

    public Object getSaleEndTime() {
        return saleEndTime;
    }

    public void setSaleEndTime(Object saleEndTime) {
        this.saleEndTime = saleEndTime;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getNextId() {
        return nextId;
    }

    public void setNextId(int nextId) {
        this.nextId = nextId;
    }
}
