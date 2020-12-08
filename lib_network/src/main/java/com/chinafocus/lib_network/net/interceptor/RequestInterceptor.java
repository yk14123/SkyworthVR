package com.chinafocus.lib_network.net.interceptor;

import androidx.annotation.NonNull;

import com.chinafocus.lib_network.net.base.Constants;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @author
 * @date 2020/5/20
 * description：
 */
public class RequestInterceptor implements Interceptor {
    @Override
    @NonNull
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Request headRequest = request.newBuilder()
                .addHeader("appId","5greadertree")
//                .addHeader("clientId", Constants.BASE_CLIENT_ID)
//                .addHeader("clientId","yangke")
                .addHeader("lang", "cn")
                .build();
        return chain.proceed(headRequest);
    }
}
