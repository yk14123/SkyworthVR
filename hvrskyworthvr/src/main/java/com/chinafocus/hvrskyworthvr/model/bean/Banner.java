package com.chinafocus.hvrskyworthvr.model.bean;

public class Banner {

    /**
     * title : 红色力量根据地
     * intro : 延安一个熟悉的名字，它带着黄河母亲的血液孕育出了红色的生命，伟大的中国共产党在这片土地上开出了红色的花。
     * id : 10082
     * coverImg : cloudvr/img/cover/1606810184050.jpg
     * videoPath : cloudvr/preview/1606810143184.MP4
     * type : video
     */

    private String title;
    private String intro;
    private int id;
    private String coverImg;
    private String videoPath;
    private String type;

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

    public String getCoverImg() {
        return coverImg;
    }

    public void setCoverImg(String coverImg) {
        this.coverImg = coverImg;
    }

    public String getVideoPath() {
        return videoPath;
    }

    public void setVideoPath(String videoPath) {
        this.videoPath = videoPath;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
