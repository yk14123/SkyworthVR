package com.chinafocus.hvrskyworthvr.model.bean;

public class VideoDataInfo {
    /**
     * classify : 16
     * imgUrl : test-temp-cover/700X400/qiaohuadan.jpg
     * duration : 277
     * title : 俏花旦
     * intro : 中国杂技团（待定）	中国杂技团《俏花旦》，是一支集体空竹表演，其最特别的是在杂技中融入京剧艺术元素，通过京剧服饰、音乐、动作、身段等运用，体现了国粹的大气和典雅华丽。演员身着京剧服饰，表演技巧新颖巧妙，动作惊险高难又不失轻松愉快，文活武演妩媚阳刚，是目前国际杂技表演中最具代表性、最具知名度的表演之一。
     * id : 10080
     */

    private String classify;
    private String imgUrl;
    private int duration;
    private String title;
    private String intro;
    private int id;

    public String getClassify() {
        return classify;
    }

    public void setClassify(String classify) {
        this.classify = classify;
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

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
