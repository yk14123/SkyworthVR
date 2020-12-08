package com.chinafocus.lib_network.net.observer;

import com.chinafocus.lib_network.net.beans.BaseResponse;
import com.chinafocus.lib_network.net.errorhandler.ExceptionHandle;

import io.reactivex.observers.DisposableObserver;

public abstract class BaseObserver<T> extends DisposableObserver<BaseResponse<T>> {

    @Override
    public void onNext(BaseResponse<T> t) {
        if (t.getErrCode() == 0 || t.getErrCode() == 200) {
            onSuccess(t.getData());
        } else {
            onServiceMessage(t.getErrMsg());
        }
    }

    @Override
    public void onError(Throwable e) {
        onFailure((ExceptionHandle.ResponseThrowable) e);
    }

    @Override
    public void onComplete() {

    }

    protected void onServiceMessage(String errMsg) {
    }

    public abstract void onSuccess(T t);

    public abstract void onFailure(ExceptionHandle.ResponseThrowable e);
}
