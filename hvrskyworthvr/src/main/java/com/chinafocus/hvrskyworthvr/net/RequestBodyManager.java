package com.chinafocus.hvrskyworthvr.net;

import com.chinafocus.hvrskyworthvr.model.DeviceInfoManager;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.RequestBody;

public class RequestBodyManager {

    private static RequestBody createRequestBody(JSONObject jsonObject) {
        return FormBody
                .create(
                        MediaType.parse("application/json; charset=utf-8"),
                        jsonObject.toString());
    }

    public static RequestBody getRequestAliasBody(String newName) {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("alias", newName);
            jsonObject.put("appNo", DeviceInfoManager.getInstance().getAppNo());
            jsonObject.put("uniqueId", DeviceInfoManager.getInstance().getDeviceUUID());
            jsonObject.put("userNo", DeviceInfoManager.getInstance().getDeviceAccountId());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return createRequestBody(jsonObject);
    }

    public static RequestBody getVideoDetailDataRequestBody(int tag, int id) {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("appNo", DeviceInfoManager.getInstance().getAppNo());
            jsonObject.put("id", id);
            jsonObject.put("type", tag);
            jsonObject.put("uniqueId", DeviceInfoManager.getInstance().getDeviceUUID());
            jsonObject.put("userNo", DeviceInfoManager.getInstance().getDeviceAccountId());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return createRequestBody(jsonObject);
    }

    public static RequestBody getVideoListRequestBody(int category) {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("appNo", DeviceInfoManager.getInstance().getAppNo());
            jsonObject.put("uniqueId", DeviceInfoManager.getInstance().getDeviceUUID());
            jsonObject.put("userNo", DeviceInfoManager.getInstance().getDeviceAccountId());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return createRequestBody(jsonObject);
    }

    public static RequestBody getCategoryRequestBody() {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("appNo", DeviceInfoManager.getInstance().getAppNo());
            jsonObject.put("contentType", 1);
            jsonObject.put("uniqueId", DeviceInfoManager.getInstance().getDeviceUUID());
            jsonObject.put("userNo", DeviceInfoManager.getInstance().getDeviceAccountId());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return createRequestBody(jsonObject);
    }

    public static RequestBody getDefaultRequestBody() {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("alias", DeviceInfoManager.getInstance().getDeviceAlias());
            jsonObject.put("appNo", DeviceInfoManager.getInstance().getAppNo());
            jsonObject.put("uniqueId", DeviceInfoManager.getInstance().getDeviceUUID());
            jsonObject.put("userNo", DeviceInfoManager.getInstance().getDeviceAccountId());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return createRequestBody(jsonObject);
    }
}
