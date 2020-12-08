package com.chinafocus.hvrskyworthvr.model.bean;

import java.util.List;

public class VideoListData {

    /**
     * list : [{"title":"寻觅神圣的王权遗迹","intro":"吴哥窟，这座被中国佛学古籍称之为\u201c桑香佛舍\u201d的柬埔寨遗迹，由苏利耶跋摩二世时为供奉毗湿奴而建，不仅是吴哥古迹最精华的一笔，也是柬埔寨早期建筑风格的代表。","id":10090,"coverImg":"cloudvr/img/cover/1606875427806.jpg","videoPath":"cloudvr/preview/1606875372293.MP4","duration":186},{"title":"探访地球最美的伤痕","intro":"地球，一个古老而又神秘的星球，它不断地运动着，大陆间的变化碰撞，碰撞出一个又一个的地理奇迹，其中之一就是这里，科罗拉多大峡谷","id":10092,"coverImg":"cloudvr/img/cover/1606880213578.jpg","videoPath":"cloudvr/preview/1606880091123.MP4","duration":219},{"title":"足踏日本 寻觅日出扶桑 ","intro":"日本，这个象征着\u201c日出\u201d的岛国，用自身独特的历史与文明描摹着\u201c月有云遮，花有风吹\u201d的美学意境，在太阳升起的地方书写\u201c扶桑东更东\u201d的奇迹。","id":10074,"coverImg":"cloudvr/img/cover/1606442943893.jpg","videoPath":"cloudvr/preview/1606442895344.MP4","duration":189},{"title":"伦敦文化之旅","intro":"伦敦，一座莎士比亚演过戏、披头士唱过歌、哈利·波特施过魔法的城市。如果你厌倦了伦敦，也就厌倦了生活。\n","id":10076,"coverImg":"cloudvr/img/cover/1606456140237.jpg","videoPath":"cloudvr/preview/1606456100986.MP4","duration":153},{"title":"美国首都之行","intro":"夜幕的白宫屋檐下泛起暖色的灯光，苍穹下的纪念碑与远处盛开的樱花树为舞，南北战争的硝烟簇拥林肯纪念堂的雕像垂名青史。如今繁华的华盛顿特区，还在上演着属于美国的永恒奇迹。","id":10073,"coverImg":"cloudvr/img/cover/1606385484505.jpg","videoPath":"cloudvr/preview/1606385436898.MP4","duration":250},{"title":"游走巴黎 流动的盛宴","intro":"\u201c假如你有幸年轻时在巴黎生活过，那么你此后一生中，不论去到哪里，她都与你同在，因为巴黎是一席流动的盛宴。\u201d\u2014\u2014海明威","id":10066,"coverImg":"cloudvr/img/cover/1606283798305.jpg","videoPath":"cloudvr/preview/1606283727724.MP4","duration":248},{"title":"一眼千年 走进埃及文明","intro":"\u201c人类惧怕时间，而时间惧怕金字塔。\u201d数千年以来，古埃及数文明犹如尼罗河畔一颗璀璨的明珠，接受着时间无穷无尽的考验。","id":10054,"coverImg":"cloudvr/img/cover/1606204973007.jpg","videoPath":"cloudvr/preview/1606204715107.MP4","duration":149}]
     * total : 1
     */

    private int total;
    private List<ListBean> list;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<ListBean> getList() {
        return list;
    }

    public void setList(List<ListBean> list) {
        this.list = list;
    }

    public static class ListBean {
        /**
         * title : 寻觅神圣的王权遗迹
         * intro : 吴哥窟，这座被中国佛学古籍称之为“桑香佛舍”的柬埔寨遗迹，由苏利耶跋摩二世时为供奉毗湿奴而建，不仅是吴哥古迹最精华的一笔，也是柬埔寨早期建筑风格的代表。
         * id : 10090
         * coverImg : cloudvr/img/cover/1606875427806.jpg
         * videoPath : cloudvr/preview/1606875372293.MP4
         * duration : 186
         */

        private String title;
        private String intro;
        private int id;
        private String coverImg;
        private String videoPath;
        private int duration;

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
    }
}
