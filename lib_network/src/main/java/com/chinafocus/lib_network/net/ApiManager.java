package com.chinafocus.lib_network.net;


import com.chinafocus.lib_network.net.base.Constants;
import com.chinafocus.lib_network.net.base.NetworkApi;

import okhttp3.Interceptor;

/**
 * @author
 * @date 2020/5/20
 * description：
 */
public class ApiManager extends NetworkApi {
    private static ApiManager sInstance;

    private ApiManager() {
    }

    /**
     * 一般这里是添加自定义的Header的Interceptor
     *
     * @return
     */
    @Override
    protected Interceptor getInterceptor() {
        return null;
    }

    public static ApiManager getInstance() {
        if (sInstance == null) {
            synchronized (ApiManager.class) {
                if (sInstance == null) {
                    sInstance = new ApiManager();
                }
            }
        }
        return sInstance;
    }

    public static <T> T getService(Class<T> service) {
        return getInstance().getRetrofit(service).create(service);
    }

    @Override
    public String getFormal() {
        return Constants.BASE_TEST_URL;
    }

    @Override
    public String getTest() {
        return Constants.BASE_TEST_URL;
    }
//    @Override
//    public String getTest() {
//        return Constants.BASE_FORMAL_URL;
//    }

}
