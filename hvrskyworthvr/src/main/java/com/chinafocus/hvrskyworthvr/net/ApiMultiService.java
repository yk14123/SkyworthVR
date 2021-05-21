package com.chinafocus.hvrskyworthvr.net;

import com.chinafocus.hvrskyworthvr.model.bean.AppVersionInfo;
import com.chinafocus.hvrskyworthvr.model.bean.DefaultCloudUrl;
import com.chinafocus.hvrskyworthvr.model.bean.DeviceInfo;
import com.chinafocus.hvrskyworthvr.model.bean.VideoCategory;
import com.chinafocus.hvrskyworthvr.model.bean.VideoContentList;
import com.chinafocus.hvrskyworthvr.model.bean.VideoDataInfo;
import com.chinafocus.hvrskyworthvr.model.bean.VideoDetail;
import com.chinafocus.lib_network.net.beans.BaseResponse;

import java.util.List;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

public interface ApiMultiService {

    String ABOUT_USER_PROTOCOL = "farm360/farmhouse360/user-agreement.html";
    String ABOUT_PRIVACY_PROTOCOL = "farm360/farmhouse360/privacy-agreement.html";
    String ABOUT_US_PROTOCOL = "farm360/farmhouse360/about-us.html";

    @POST("farm360/sysCloud/defaultCloudUrl")
    Observable<BaseResponse<DefaultCloudUrl>> getDefaultCloudUrl(@Body RequestBody body);

    @POST("farm360/cstmHwAlias/hwAlias")
    Observable<BaseResponse<Object>> initDeviceInfo(@Body RequestBody body);

    @POST("farm360/cstmHwAlias/getHwAlias")
    Observable<BaseResponse<DeviceInfo>> getDeviceInfoName(@Body RequestBody body);

    @POST("farm360/cstmHwAlias/editHwAlias")
    Observable<BaseResponse<Object>> postSetDeviceAlias(@Body RequestBody body);

    @POST("farm360/content/viewSlides")
    Observable<BaseResponse<List<VideoDataInfo>>> getPublishBanner(@Body RequestBody body);

    @POST("farm360/content/videoSlides")
    Observable<BaseResponse<List<VideoDataInfo>>> getVideoBanner(@Body RequestBody body);

    @POST("farm360/content/viewList")
    Observable<BaseResponse<List<VideoDataInfo>>> getPublishListData(@Body RequestBody body);

    @POST("farm360/content/myClassify")
    Observable<BaseResponse<List<VideoCategory>>> getVideoCategory(@Body RequestBody body);

    @POST("farm360/content/videoList")
    Observable<BaseResponse<List<VideoDataInfo>>> getVideoListData(@Body RequestBody body);

    @POST("farm360/edu/videoDtl")
    Observable<BaseResponse<VideoDetail>> getVideoDetailData(@Body RequestBody body);

    @POST("farm360/content/contentList")
    Observable<BaseResponse<List<VideoContentList>>> getVideoContentList(@Body RequestBody body);

    @POST("farm360/edu/contentList")
    Observable<BaseResponse<List<VideoContentList>>> getEduVideoContentList(@Body RequestBody body);

    @POST("farm360/version/latestVersion2")
    Observable<BaseResponse<AppVersionInfo>> checkAppVersionAndUpdate(@Body RequestBody body);

    @GET
    @Streaming
    Observable<Response<ResponseBody>> executeDownload(@Header("Range") String range, @Url() String url);
}
