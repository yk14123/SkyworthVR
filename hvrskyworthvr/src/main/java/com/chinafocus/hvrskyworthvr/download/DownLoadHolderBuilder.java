package com.chinafocus.hvrskyworthvr.download;

import android.graphics.Color;

import com.chinafocus.hvrskyworthvr.global.ConfigManager;
import com.chinafocus.hvrskyworthvr.model.bean.VideoContentList;
import com.chinafocus.hvrskyworthvr.model.bean.VideoDetail;

import java.io.File;

import io.reactivex.Observable;

import static com.chinafocus.hvrskyworthvr.download.VideoType.PRE_VIDEO;
import static com.chinafocus.hvrskyworthvr.download.VideoType.REAL_VIDEO;

class DownLoadHolderBuilder {

    private String mVideoDownloadUrl;
    private String mVideoSimpleName;
    private String mVideoFullName;
    // 默认加密
    private boolean isEncrypted = true;

    private String outputRootPath;
    private String finalRootPath;

    private String mTitle;
    private String imageUrl;
    private long duration;

    private VideoType mVideoType;

    public <T> DownLoadHolderBuilder(T data) {
        if (data instanceof VideoDetail) {
            mTitle = "正片 ： " + ((VideoDetail) data).getTitle();
            mVideoDownloadUrl = ConfigManager.getInstance().getDefaultUrl() + ((VideoDetail) data).getVideoUrl();
            outputRootPath = ConfigManager.getInstance().getRealVideoTempFilePath();
            finalRootPath = ConfigManager.getInstance().getRealVideoFilePath();
            imageUrl = ConfigManager.getInstance().getDefaultUrl() + ((VideoDetail) data).getImgUrl();
            duration = ((VideoDetail) data).getDuration();
            mVideoType = REAL_VIDEO;
        } else if (data instanceof VideoContentList) {
            mTitle = "预览 ： " + ((VideoContentList) data).getTitle();
            mVideoDownloadUrl = ConfigManager.getInstance().getDefaultUrl() + ((VideoContentList) data).getMenuVideoUrl();
            outputRootPath = ConfigManager.getInstance().getPreVideoTempFilePath();
            finalRootPath = ConfigManager.getInstance().getPreVideoFilePath();
            imageUrl = ConfigManager.getInstance().getDefaultUrl() + ((VideoContentList) data).getImgUrl();
            duration = ((VideoContentList) data).getDuration();
            mVideoType = PRE_VIDEO;
        }
    }

    public DownLoadHolderBuilder setEncrypted(boolean isEncrypted) {
        this.isEncrypted = isEncrypted;
        return this;
    }

    private String getOutputPath() {
        return new File(outputRootPath, mVideoFullName).getAbsolutePath();
    }

    private String getFinalPath() {
        return new File(finalRootPath, mVideoFullName).getAbsolutePath();
    }

    private boolean isShouldDownload() {
        File file1 = new File(finalRootPath, mVideoSimpleName + ".mp4");
        File file2 = new File(finalRootPath, mVideoSimpleName + ".chinafocus");
        return !(file1.exists() || file2.exists());
    }

    private void parseVideoName() {
        Observable
                .just(mVideoDownloadUrl)
                .map(s -> s.substring(s.lastIndexOf("/") + 1))
                .map(s -> s.substring(0, s.indexOf(".")))
                .doOnNext(s -> {
                    mVideoSimpleName = s;
                    if (isEncrypted) {
                        mVideoFullName = mVideoSimpleName + ".chinafocus";
                    } else {
                        mVideoFullName = mVideoSimpleName + ".mp4";
                    }
                })
                .subscribe();
    }

    public DownLoadHolder build() {
        parseVideoName();

        DownLoadHolder downLoadHolder = new DownLoadHolder();
        downLoadHolder.setDownLoadUrl(mVideoDownloadUrl);
        downLoadHolder.setEncrypted(isEncrypted);
        downLoadHolder.setTitle(mTitle);
        downLoadHolder.setOutputPath(getOutputPath());
        downLoadHolder.setFinalPath(getFinalPath());

        downLoadHolder.setDuration(duration);
        downLoadHolder.setImageUrl(imageUrl);

        downLoadHolder.setVideoType(mVideoType);

        downLoadHolder.setProgressingColor(Color.parseColor("#FF14C27B"));
        downLoadHolder.setCurrentStatus("等待下载");
        downLoadHolder.setCurrentStatusColor(Color.parseColor("#FFA0A0A3"));

        if (isShouldDownload()) {
            downLoadHolder.setShouldDownload(true);
        }
        return downLoadHolder;
    }
}