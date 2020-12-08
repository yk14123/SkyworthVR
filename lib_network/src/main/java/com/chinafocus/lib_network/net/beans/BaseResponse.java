package com.chinafocus.lib_network.net.beans;


import com.google.gson.annotations.SerializedName;

/**
 * @author
 * @date 2020/5/20
 * description：
 */
public class BaseResponse<T> {
    @SerializedName("code")
    private int errCode;
    @SerializedName("message")
    private String errMsg;
    private T data;

    public int getErrCode() {
        return errCode;
    }

    public void setErrCode(int errCode) {
        this.errCode = errCode;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
