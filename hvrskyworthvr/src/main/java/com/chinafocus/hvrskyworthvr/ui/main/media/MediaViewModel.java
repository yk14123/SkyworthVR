package com.chinafocus.hvrskyworthvr.ui.main.media;

import android.app.Application;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.arialyy.annotations.Download;
import com.arialyy.aria.core.Aria;
import com.arialyy.aria.core.task.DownloadTask;
import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.SPUtils;
import com.chinafocus.hvrskyworthvr.global.ConfigManager;
import com.chinafocus.hvrskyworthvr.model.bean.VideoDetail;
import com.chinafocus.hvrskyworthvr.net.ApiMultiService;
import com.chinafocus.hvrskyworthvr.net.RequestBodyManager;
import com.chinafocus.hvrskyworthvr.util.LocalLogUtils;
import com.chinafocus.lib_network.net.ApiManager;
import com.chinafocus.lib_network.net.base.BaseViewModel;
import com.chinafocus.lib_network.net.errorhandler.ExceptionHandle;
import com.chinafocus.lib_network.net.errorhandler.HttpErrorHandler;
import com.chinafocus.lib_network.net.observer.BaseObserver;
import com.google.gson.Gson;

import java.io.File;

import io.reactivex.schedulers.Schedulers;

public class MediaViewModel extends BaseViewModel {

    public MutableLiveData<VideoDetail> videoDetailMutableLiveData = new MutableLiveData<>();

    public MediaViewModel(@NonNull Application application) {
        super(application);
    }

    public void getVideoDetailDataFromLocal(int tag, int id) {
        String obj = SPUtils.getInstance().getString(tag + ";" + id);
        if (!TextUtils.isEmpty(obj)) {
            videoDetailMutableLiveData.postValue(new Gson().fromJson(obj, VideoDetail.class));
        } else {
            LocalLogUtils.e("MediaViewModel", " ******[严重错误]******  本地缓存内容为null ***** 对应null的tag和id为 >>> " + tag + ";" + id);
        }
    }

    public void saveVideoDetailDataFromNet(int tag, int id) {
        ApiManager
                .getService(ApiMultiService.class)
                .getVideoDetailData(RequestBodyManager.getVideoDetailDataRequestBody(tag, id))
                .subscribeOn(Schedulers.io())
                .onErrorResumeNext(new HttpErrorHandler<>())
                .subscribe(new BaseObserver<VideoDetail>() {
                    @Override
                    public void onSuccess(VideoDetail videoDetail) {
                        SPUtils.getInstance().put(tag + ";" + id, new Gson().toJson(videoDetail));

                        String subtitle = videoDetail.getSubtitle();
                        if (!TextUtils.isEmpty(subtitle)) {
                            String[] split = subtitle.split("/");
                            String fileName = split[split.length - 1];
                            if (fileName.toLowerCase().endsWith("ass")) {
                                downLoadSubTitle(ConfigManager.getInstance().getDefaultUrl() + subtitle, fileName);
                            }
                        }

//                        if (videoDetail.getTitle().equals("圆明园的毁灭")) {
//                            String videoUrl = videoDetail.getVideoUrl();
//                            String[] split2 = videoUrl.split("/");
//                            String videoFileName = split2[split2.length - 1];
//                            downLoadSubTitle(ConfigManager.getInstance().getDefaultUrl() + videoUrl, videoFileName);
//
//                            Log.e("MyLog", " 》》》 " + ConfigManager.getInstance().getDefaultUrl() + videoUrl);
//                        }
                    }

                    @Override
                    public void onFailure(ExceptionHandle.ResponseThrowable e) {

                    }
                });

    }

    public void downLoadSubTitle(String url, String fileName) {
        File subtitle = getApplication().getApplicationContext().getExternalFilesDir("subtitle");
        File file = new File(subtitle, fileName);

        File fileTemp = new File(subtitle, "temp");
        if (!fileTemp.exists()) {
            fileTemp.mkdir();
        }

        if (!file.exists()) {
//            long taskKey = SPUtils.getInstance().getLong("taskKey");
//            if (taskKey == -1) {
            Aria.download(this)
                    .load(url)     //读取下载地址
                    .setFilePath(new File(fileTemp, fileName).getAbsolutePath()) //设置文件保存的完整路径
                    .resetState()
                    .create();   //创建并启动下载
//            } else {
//                Aria.download(this)
//                        .load(taskKey)     //读取下载地址
//                        .reTry();
//
//                SPUtils.getInstance().put("taskKey", -1L);
//            }
        }
    }

    //在这里处理任务执行中的状态，如进度进度条的刷新
    @Download.onTaskRunning
    protected void running(DownloadTask task) {
//        if(task.getKey().eques(url)){
//		....
//            可以通过url判断是否是指定任务的回调
//        }
//        int p = task.getPercent();	//任务进度百分比
//        String speed = task.getConvertSpeed();	//转换单位后的下载速度，单位转换需要在配置文件中打开
//        String speed1 = task.getSpeed(); //原始byte长度速度

//        Log.e("MyLog", " running >>> " + task.getTaskName());
        Log.e("MyLog", " running Name >>> " + task.getTaskName() + " running Percent >>> " + task.getPercent());
    }

    @Download.onTaskComplete
    protected void taskComplete(DownloadTask task) {
//        //在这里处理任务完成的状态
//        Log.e("MyLog", " taskComplete >>> " + task.getTaskName());
//
//        String filePath = task.getFilePath();
//        Log.e("MyLog", " taskComplete getFilePath >>> " + filePath);
//
//        File subtitle = getApplication().getApplicationContext().getExternalFilesDir("subtitle");
//        int i = filePath.lastIndexOf("/");
//        String fileName = filePath.substring(++i);
//        Log.e("MyLog", " fileName >> " + fileName);
//
////        String[] split = filePath.split("/");
////        String fileName = split[split.length - 1];
//        File dest = new File(subtitle, fileName);
//        File file = new File(filePath);
//        file.renameTo(dest);
    }

    @Download.onTaskFail
    protected void taskFail(DownloadTask task) {
        //在这里处理任务完成的状态
//        Log.e("MyLog", " taskFail >>> " + task.getTaskName());
//        SPUtils.getInstance().put("taskKey", task.getEntity().getId());
    }

    public void unRegister() {
        Aria.download(this).unRegister();
    }

    public void register() {
        Aria.download(this).register();
    }

    public void testInstallApk() {
        File file = new File(getApplication().getApplicationContext().getExternalFilesDir("ApkInstall"), "a.apk");
        if (file.exists()) {
            AppUtils.installApp(file.getAbsolutePath());
        }
    }
}
