package com.chinafocus.hvrskyworthvr.download;

import androidx.annotation.NonNull;

public class DownLoadHolder implements Cloneable {
    private boolean isEncrypted;
    private boolean shouldDownload;
    private String downLoadUrl;
    private String outputPath;
    private String finalPath;
    private String title;
    private long downloadFileRange;
    private long localTempFileLength;

    private String imageUrl;
    private long duration;

    // 当前下载任务的位置
    private int pos;
    // 已完成  等待下载  等待继续下载  下载速度
    private String currentStatus;
    // 进度
    private int progress;

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public String getCurrentStatus() {
        return currentStatus;
    }

    public void setCurrentStatus(String currentStatus) {
        this.currentStatus = currentStatus;
    }

    public int getPos() {
        return pos;
    }

    public void setPos(int pos) {
        this.pos = pos;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public long getLocalTempFileLength() {
        return localTempFileLength;
    }

    public void setLocalTempFileLength(long localTempFileLength) {
        this.localTempFileLength = localTempFileLength;
    }

    public long getDownloadFileRange() {
        return downloadFileRange;
    }

    public void setDownloadFileRange(long downloadFileRange) {
        this.downloadFileRange = downloadFileRange;
    }

    public String getFinalPath() {
        return finalPath;
    }

    public void setFinalPath(String finalPath) {
        this.finalPath = finalPath;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isShouldDownload() {
        return shouldDownload;
    }

    public void setShouldDownload(boolean shouldDownload) {
        this.shouldDownload = shouldDownload;
    }

    public String getDownLoadUrl() {
        return downLoadUrl;
    }

    public void setDownLoadUrl(String downLoadUrl) {
        this.downLoadUrl = downLoadUrl;
    }

    public boolean isEncrypted() {
        return isEncrypted;
    }

    public void setEncrypted(boolean encrypted) {
        isEncrypted = encrypted;
    }

    public String getOutputPath() {
        return outputPath;
    }

    public void setOutputPath(String outputPath) {
        this.outputPath = outputPath;
    }

    @Override
    public String toString() {
        return "DownLoadHolder{" +
                "title='" + title + '\'' +
                '}';
    }

    @NonNull
    @Override
    public DownLoadHolder clone() throws CloneNotSupportedException {
        return (DownLoadHolder) super.clone();
    }
}
