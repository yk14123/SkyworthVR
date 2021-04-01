package com.chinafocus.hvrskyworthvr.model.bean;

public class VideoContentList {

    /**
     * classify : 7
     * imgUrl : cloudvr/img/cover/1610676888536.jpg
     * duration : 197
     * title : 张大千《华山云海图》
     * intro : 通过3D和全景技术重建《华山云海图》这一恢宏的山水画卷，以一只仙鹤为视线引导观众领略华山云海图的壮举景象，包括画中云海，山峦，小径和行人等元素的动态展现，以及用艺术技术手段着重展现原画金线勾勒、没骨设色等特点，让观者进入画里世界一起观赏张大千笔下的壮美华山。
     * 《华山云海图》是他的重彩金碧山水长卷，全图用金粉、朱砂、石青、石绿、蛤粉等矿物颜料绘成。华山自古以险峻奇丽著称，张大千的长卷中再现华山的连绵不断和雄伟气势，描绘了幽谷奇峰，其间白云缭绕，道观寺院如灵楼仙苑般令人神往，可谓“雄奇秀拔照人寰，尽收气象纤毫底”。此卷画在长近六米的洒金笺上，张大千自题了“西江月”一首并书跋两段，其中写道：“初师僧繇法，既而略加勾勒，遂似李将军矣。”可见这幅作品融会了唐朝张僧繇的“设色没骨山水”和李昭道的“金碧勾勒山水”，展现了华山雄奇秀拔、云海微茫的万千瑰丽景象。画面笔墨精妙，富丽堂皇，显示了张大千全面、深厚的传统功力，足见该作品之可贵。
     * id : 10020
     * menuVideoUrl : cloudvr/preview/1607658684490.MP4
     * type : 2
     */

    private String classify;
    private String className;
    private String classStyleColor;
    private String imgUrl;
    private int duration;
    private String title;
    private String intro;
    private int id;
    private String menuVideoUrl;
    private int type;

    public String getClassStyleColor() {
        return classStyleColor;
    }

    public void setClassStyleColor(String classStyleColor) {
        this.classStyleColor = classStyleColor;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

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

    public String getMenuVideoUrl() {
        return menuVideoUrl;
    }

    public void setMenuVideoUrl(String menuVideoUrl) {
        this.menuVideoUrl = menuVideoUrl;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
