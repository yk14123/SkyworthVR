package com.chinafocus.lib_network.net.interceptor;

import androidx.annotation.NonNull;

import com.chinafocus.lib_network.net.errorhandler.ExceptionHandle;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * @author
 * @date 2020/5/20
 * description：
 */
public class ResponseInterceptor implements Interceptor {
    @Override
    @NonNull
    public Response intercept(Chain chain) throws IOException {
        Response response = chain.proceed(chain.request());
        // TODO 如果是我们内部自定义错误，则单独抛异常，不走GSON转换
        ResponseBody body = response.body();
        MediaType mediaType = null;
        String readString = "";
        if (body != null) {

            mediaType = body.contentType();
            if (mediaType != null) {
                if (mediaType.type().equals("video") && mediaType.subtype().equals("mp4")) {
                    // 如果是视频文件，不处理
                    return response;
                }
            }

            readString = body.string();
            try {
                JSONObject jsonObject = new JSONObject(readString);
                Integer errCode = (Integer) jsonObject.get("errCode");
                String errMsg = (String) jsonObject.get("errMsg");
                if (errCode != 0) {
                    // 必须关闭，避免资源泄漏
                    ExceptionHandle.ServerException serverException = new ExceptionHandle.ServerException();
                    serverException.code = errCode;
                    serverException.message = errMsg;
                    throw serverException;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return response.newBuilder()
                .body(ResponseBody.create(mediaType, readString))
                .build();
    }
}
