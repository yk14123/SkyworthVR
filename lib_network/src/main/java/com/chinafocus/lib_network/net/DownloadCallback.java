package com.chinafocus.lib_network.net;

public interface DownloadCallback {
    void onStart();
    void onProgress(int p);
    void onFinish(String path);
    void onError(String msg);
}