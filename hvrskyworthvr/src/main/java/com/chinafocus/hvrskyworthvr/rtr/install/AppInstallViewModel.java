package com.chinafocus.hvrskyworthvr.rtr.install;

import android.app.Application;
import android.os.Build;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.lifecycle.MutableLiveData;

import com.arialyy.annotations.Download;
import com.arialyy.aria.core.Aria;
import com.arialyy.aria.core.task.DownloadTask;
import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.FileUtils;
import com.chinafocus.huaweimdm.tools.MdmTools;
import com.chinafocus.hvrskyworthvr.global.ConfigManager;
import com.chinafocus.hvrskyworthvr.model.bean.AppVersionInfo;
import com.chinafocus.hvrskyworthvr.net.ApiMultiService;
import com.chinafocus.hvrskyworthvr.net.RequestBodyManager;
import com.chinafocus.lib_network.net.ApiManager;
import com.chinafocus.lib_network.net.base.BaseViewModel;
import com.chinafocus.lib_network.net.beans.BaseResponse;
import com.chinafocus.lib_network.net.errorhandler.ExceptionHandle;
import com.chinafocus.lib_network.net.errorhandler.HttpErrorHandler;
import com.chinafocus.lib_network.net.observer.BaseObserver;

import java.io.File;
import java.util.Optional;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class AppInstallViewModel extends BaseViewModel {

    private static boolean isAppUpdateRunning = false;
    private static boolean hasAppUpdate = false;

    private MutableLiveData<AppVersionInfo> mAppVersionInfoMutableLiveData = new MutableLiveData<>();

    private MutableLiveData<Integer> mTaskRunning = new MutableLiveData<>();
    private MutableLiveData<Void> mTaskComplete = new MutableLiveData<>();
    private MutableLiveData<Void> mTaskFail = new MutableLiveData<>();
    private MutableLiveData<Void> mNetWorkError = new MutableLiveData<>();
    private MutableLiveData<Void> mVersionLatest = new MutableLiveData<>();
    private MutableLiveData<Void> mTaskResume = new MutableLiveData<>();
    private MutableLiveData<Void> mTaskUpdateRunning = new MutableLiveData<>();

    private long mTaskId = -1;
    private String mUrl;

    private String mTaskCompletePath;
    private Observable<BaseResponse<AppVersionInfo>> mAppVersionObservable;

    public AppInstallViewModel(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<AppVersionInfo> getAppVersionInfoMutableLiveData() {
        return mAppVersionInfoMutableLiveData;
    }

    public MutableLiveData<Void> getTaskUpdateRunning() {
        return mTaskUpdateRunning;
    }

    public MutableLiveData<Integer> getTaskRunning() {
        return mTaskRunning;
    }

    public MutableLiveData<Void> getTaskComplete() {
        return mTaskComplete;
    }

    public MutableLiveData<Void> getTaskFail() {
        return mTaskFail;
    }

    public MutableLiveData<Void> getNetWorkError() {
        return mNetWorkError;
    }

    public MutableLiveData<Void> getVersionLatest() {
        return mVersionLatest;
    }

    public MutableLiveData<Void> getTaskResume() {
        return mTaskResume;
    }

    public void unRegister() {
        Aria.download(this).unRegister();
    }

    public void register() {
        Aria.download(this).register();
    }

    private Observable<BaseResponse<AppVersionInfo>> getAppVersionObservable() {
        if (mAppVersionObservable == null) {
            mAppVersionObservable = ApiManager
                    .getService(ApiMultiService.class)
                    .checkAppVersionAndUpdate(RequestBodyManager.getCheckAppVersionRequestBody())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .onErrorResumeNext(new HttpErrorHandler<>());
        }
        return mAppVersionObservable;
    }

    public void checkAppVersionAndUpdate() {
        if (isAppUpdateRunning()) {
            mTaskUpdateRunning.postValue(null);
            return;
        }

        isAppUpdateRunning = true;
        getAppVersionObservable()
                .subscribe(new BaseObserver<AppVersionInfo>() {
                    @Override
                    public void onSuccess(AppVersionInfo appVersionInfo) {
                        if (appVersionInfo != null) {
                            int localAppVersionCode = AppUtils.getAppVersionCode();
                            if (appVersionInfo.getDefaultVersionCode() > localAppVersionCode && !TextUtils.isEmpty(appVersionInfo.getVersionUrl())) {
                                mUrl = ConfigManager.getInstance().getDefaultUrl() + appVersionInfo.getVersionUrl();
                                mAppVersionInfoMutableLiveData.postValue(appVersionInfo);
                                hasAppUpdate = true;
                            } else {
                                hasAppUpdate = false;
                                isAppUpdateRunning = false;
                                mVersionLatest.postValue(null);
                            }
                        }
                    }

                    @Override
                    public void onFailure(ExceptionHandle.ResponseThrowable e) {
                        mNetWorkError.postValue(null);
                        mTaskRunning.postValue(0);
                        mTaskFail.postValue(null);
                        isAppUpdateRunning = false;
                    }
                });
    }

    public void restartDownLoad() {
        retryDownLoadApp();
    }

    public boolean isAppUpdateRunning() {
        return isAppUpdateRunning;
    }

    public boolean hasUpdate() {
        return hasAppUpdate;
    }

    private void retryDownLoadApp() {
        isAppUpdateRunning = true;
        getAppVersionObservable()
                .subscribe(new BaseObserver<AppVersionInfo>() {

                    @Override
                    public void onSuccess(AppVersionInfo appVersionInfo) {
                        if (appVersionInfo != null) {
                            int localAppVersionCode = AppUtils.getAppVersionCode();
                            if (appVersionInfo.getDefaultVersionCode() > localAppVersionCode && !TextUtils.isEmpty(appVersionInfo.getVersionUrl())) {
                                mUrl = ConfigManager.getInstance().getDefaultUrl() + appVersionInfo.getVersionUrl();
                                hasAppUpdate = true;
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                    downLoadApk();
                                }
                            } else {
                                isAppUpdateRunning = false;
                                hasAppUpdate = false;
                            }
                        }
                    }

                    @Override
                    public void onFailure(ExceptionHandle.ResponseThrowable e) {
                        mNetWorkError.postValue(null);
                        mTaskRunning.postValue(0);
                        mTaskFail.postValue(null);
                        isAppUpdateRunning = false;
                    }
                });
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    private void downLoadApk() {
        if (TextUtils.isEmpty(mUrl)) {
            return;
        }

        File appInstallFile = getApplication().getApplicationContext().getExternalFilesDir("ApkInstall");

        Observable
                .fromArray(
                        Optional
                                .ofNullable(appInstallFile)
                                .flatMap(file -> Optional.ofNullable(file.listFiles()))
                                .orElse(new File[]{})
                )
                .doOnNext(File::delete)
                .subscribe();

        //创建并启动下载
        mTaskId = Aria.download(this)
                .load(mUrl)     //读取下载地址
                .setFilePath(new File(appInstallFile, "update.apk").getAbsolutePath()) //设置文件保存的完整路径
                .resetState()
                .ignoreFilePathOccupy()
                .create();
    }

    public void pauseDownLoad() {
        Aria
                .download(this)
                .load(mTaskId)
                .stop();
    }

    private void resumeDownLoad() {
        Aria
                .download(this)
                .load(mTaskId)
                .resume();
    }

    public void checkNetWorkAndResumeDownLoad() {
        getAppVersionObservable()
                .subscribe(new BaseObserver<AppVersionInfo>() {
                    @Override
                    public void onSuccess(AppVersionInfo appVersionInfo) {
                        resumeDownLoad();
                        mTaskResume.postValue(null);
                    }

                    @Override
                    public void onFailure(ExceptionHandle.ResponseThrowable e) {
                        mNetWorkError.postValue(null);
                    }
                });
    }

    public void cancelDownLoad() {
        if (mTaskId != -1) {
            Aria
                    .download(this)
                    .load(mTaskId)
                    .cancel(false);
        }
        mTaskId = -1;
    }

    public void removeDownLoad() {
        if (mTaskId != -1) {
            Aria
                    .download(this)
                    .load(mTaskId)
                    .cancel(true);
        }
        mTaskId = -1;
    }

    //在这里处理任务执行中的状态，如进度进度条的刷新
    @Download.onTaskRunning
    protected void running(DownloadTask task) {
        if (!TextUtils.isEmpty(mUrl) && mUrl.equals(task.getKey())) {
            mTaskRunning.postValue(task.getPercent());
        }
    }

    @Download.onTaskComplete
    protected void taskComplete(DownloadTask task) {
        if (checkTaskUrl(task)) {
            mTaskRunning.postValue(100);
            mTaskComplete.postValue(null);
            mTaskCompletePath = task.getFilePath();
            isAppUpdateRunning = false;
            installApp();
        }
    }

    @Download.onTaskFail
    protected void taskFail(DownloadTask task) {
        if (checkTaskUrl(task)) {
            mTaskRunning.postValue(0);
            mTaskFail.postValue(null);
            isAppUpdateRunning = false;
        }
    }

    public void installApp() {
        if (!TextUtils.isEmpty(mTaskCompletePath)) {
            AppUtils.installApp(mTaskCompletePath);
//            MdmTools.getInstance().installPackage(getApplication().getApplicationContext(), mTaskCompletePath);
        }
    }

    private boolean checkTaskUrl(DownloadTask task) {
        return !TextUtils.isEmpty(mUrl) && task != null && mUrl.equals(task.getKey());
    }
}
