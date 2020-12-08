package com.chinafocus.hvrskyworthvr.exo.widget;

/**
 * @author
 * @date 2020/9/19
 * description：
 */
public interface IVideoInfoChangeListener {

    void onVideoInfoChange(String format, String videoUrl, String subTitleUrl);

    void onVideoRatioChange(String ratio);

    void onVideoLangChange(String lang);
}
