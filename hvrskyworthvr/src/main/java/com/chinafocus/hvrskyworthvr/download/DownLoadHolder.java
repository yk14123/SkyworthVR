package com.chinafocus.hvrskyworthvr.download;

import androidx.annotation.NonNull;

class DownLoadHolder implements Cloneable {
    private boolean isEncrypted;
    private boolean shouldDownload;
    private String downLoadUrl;
    private String outputPath;
    private String finalPath;
    private String title;
    private long downloadFileRange;
    private long fileLength;

    public long getFileLength() {
        return fileLength;
    }

    public void setFileLength(long fileLength) {
        this.fileLength = fileLength;
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
