package com.chinafocus.hvrskyworthvr.model.bean;

import java.util.List;

public class VideoDetail {

    /**
     * title : 寻觅神圣的王权遗迹
     * intro : 吴哥窟，这座被中国佛学古籍称之为“桑香佛舍”的柬埔寨遗迹，由苏利耶跋摩二世时为供奉毗湿奴而建，不仅是吴哥古迹最精华的一笔，也是柬埔寨早期建筑风格的代表。
     * id : 10090
     * coverImg : cloudvr/img/cover/1606875427806.jpg
     * videoPath : cloudvr/preview/1606875372293.MP4
     * duration : 186
     * sounds : 1
     * captions : 1
     * startTime : 1604340951000
     * endTime : 2549001600000
     * files : [{"type":1,"filePath":"cloudvr/vr/1606875372293.MP4","duration":186,"resolution":0,"language":0,"videoType":3,"dimension":"","bitrate":0},{"type":2,"filePath":"cloudvr/bs/1606875372293.MP4","duration":186,"resolution":0,"language":0,"videoType":2,"dimension":"","bitrate":0},{"type":3,"filePath":"cloudvr/screen/1606875372293.MP4","duration":17,"resolution":0,"language":0,"videoType":2,"dimension":"","bitrate":0},{"type":9,"filePath":"cloudvr/cover/bs/1606875372293/wugeku.bs","duration":0,"resolution":0,"language":0,"videoType":2,"dimension":"3840X1920","bitrate":0},{"type":4,"filePath":"cloudvr/preview/1606875372293.MP4","duration":30,"resolution":0,"language":0,"videoType":3,"dimension":"","bitrate":0},{"type":5,"filePath":"cloudvr/audio/1606875372293_CN.WAV","duration":0,"resolution":0,"language":1,"videoType":2,"dimension":"","bitrate":0},{"type":6,"filePath":"cloudvr/caption/1606875372293_J.ASS","duration":0,"resolution":0,"language":1,"videoType":2,"dimension":"","bitrate":0},{"type":1,"filePath":"test-temp-mobile/wugeku.mp4","duration":216,"resolution":0,"language":1,"videoType":3,"dimension":"1920X960","bitrate":8000}]
     * classifys : [{"cid":1,"nameCn":"世界风景","nameEn":"Science and Technology"}]
     * tags : ["政治"]
     * platform : 3
     */

    private String title;
    private String intro;
    private int id;
    private String coverImg;
    private String videoPath;
    private int duration;
    private int sounds;
    private int captions;
    private long startTime;
    private long endTime;
    private int platform;
    private List<FilesBean> files;
    private List<ClassifysBean> classifys;
    private List<String> tags;

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

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getSounds() {
        return sounds;
    }

    public void setSounds(int sounds) {
        this.sounds = sounds;
    }

    public int getCaptions() {
        return captions;
    }

    public void setCaptions(int captions) {
        this.captions = captions;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public int getPlatform() {
        return platform;
    }

    public void setPlatform(int platform) {
        this.platform = platform;
    }

    public List<FilesBean> getFiles() {
        return files;
    }

    public void setFiles(List<FilesBean> files) {
        this.files = files;
    }

    public List<ClassifysBean> getClassifys() {
        return classifys;
    }

    public void setClassifys(List<ClassifysBean> classifys) {
        this.classifys = classifys;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public static class FilesBean {
        /**
         * type : 1
         * filePath : cloudvr/vr/1606875372293.MP4
         * duration : 186
         * resolution : 0
         * language : 0
         * videoType : 3
         * dimension :
         * bitrate : 0
         */

        private int type;
        private String filePath;
        private int duration;
        private int resolution;
        private int language;
        private int videoType;
        private String dimension;
        private int bitrate;

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public String getFilePath() {
            return filePath;
        }

        public void setFilePath(String filePath) {
            this.filePath = filePath;
        }

        public int getDuration() {
            return duration;
        }

        public void setDuration(int duration) {
            this.duration = duration;
        }

        public int getResolution() {
            return resolution;
        }

        public void setResolution(int resolution) {
            this.resolution = resolution;
        }

        public int getLanguage() {
            return language;
        }

        public void setLanguage(int language) {
            this.language = language;
        }

        public int getVideoType() {
            return videoType;
        }

        public void setVideoType(int videoType) {
            this.videoType = videoType;
        }

        public String getDimension() {
            return dimension;
        }

        public void setDimension(String dimension) {
            this.dimension = dimension;
        }

        public int getBitrate() {
            return bitrate;
        }

        public void setBitrate(int bitrate) {
            this.bitrate = bitrate;
        }
    }

    public static class ClassifysBean {
        /**
         * cid : 1
         * nameCn : 世界风景
         * nameEn : Science and Technology
         */

        private int cid;
        private String nameCn;
        private String nameEn;

        public int getCid() {
            return cid;
        }

        public void setCid(int cid) {
            this.cid = cid;
        }

        public String getNameCn() {
            return nameCn;
        }

        public void setNameCn(String nameCn) {
            this.nameCn = nameCn;
        }

        public String getNameEn() {
            return nameEn;
        }

        public void setNameEn(String nameEn) {
            this.nameEn = nameEn;
        }
    }
}
