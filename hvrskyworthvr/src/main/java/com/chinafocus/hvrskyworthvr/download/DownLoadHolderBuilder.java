package com.chinafocus.hvrskyworthvr.download;

import android.content.Context;

import com.chinafocus.hvrskyworthvr.global.ConfigManager;
import com.chinafocus.hvrskyworthvr.model.bean.VideoContentList;
import com.chinafocus.hvrskyworthvr.model.bean.VideoDetail;

import java.io.File;

import io.reactivex.Observable;

class DownLoadHolderBuilder {

    private Context mContext;
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

    public <T> DownLoadHolderBuilder(T data) {
        if (data instanceof VideoDetail) {
            mTitle = "预览影片 :" + ((VideoDetail) data).getTitle();
            mVideoDownloadUrl = ConfigManager.getInstance().getDefaultUrl() + ((VideoDetail) data).getVideoUrl();
            outputRootPath = "Videos/temp";
            finalRootPath = "Videos";
            imageUrl = ConfigManager.getInstance().getDefaultUrl() + ((VideoDetail) data).getImgUrl();
            duration = ((VideoDetail) data).getDuration();
        } else if (data instanceof VideoContentList) {
            mTitle = "正式影片 :" + ((VideoContentList) data).getTitle();
            mVideoDownloadUrl = ConfigManager.getInstance().getDefaultUrl() + ((VideoContentList) data).getMenuVideoUrl();
            outputRootPath = "preview/temp";
            finalRootPath = "preview";
            imageUrl = ConfigManager.getInstance().getDefaultUrl() + ((VideoContentList) data).getImgUrl();
            duration = ((VideoContentList) data).getDuration();
        }
    }

    public DownLoadHolderBuilder setContext(Context context) {
        this.mContext = context;
        return this;
    }

    public DownLoadHolderBuilder setEncrypted(boolean isEncrypted) {
        this.isEncrypted = isEncrypted;
        return this;
    }

    private String getOutputPath() {
        return new File(mContext.getExternalFilesDir(outputRootPath), mVideoFullName).getAbsolutePath();
    }

    private String getFinalPath() {
        return new File(mContext.getExternalFilesDir(finalRootPath), mVideoFullName).getAbsolutePath();
    }

    private boolean isShouldDownload() {
        File file1 = new File(mContext.getExternalFilesDir(finalRootPath), mVideoSimpleName + ".mp4");
        File file2 = new File(mContext.getExternalFilesDir(finalRootPath), mVideoSimpleName + ".chinafocus");
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

        if (isShouldDownload()) {
            downLoadHolder.setShouldDownload(true);
        }
        return downLoadHolder;
    }
}