package com.chinafocus.hvrskyworthvr.net;

import com.chinafocus.hvrskyworthvr.model.bean.DefaultCloudUrl;
import com.chinafocus.hvrskyworthvr.model.bean.VideoCategory;
import com.chinafocus.hvrskyworthvr.model.bean.VideoContentList;
import com.chinafocus.hvrskyworthvr.model.bean.VideoDataInfo;
import com.chinafocus.hvrskyworthvr.model.bean.VideoDetail;
import com.chinafocus.hvrskyworthvr.model.bean.DeviceInfo;
import com.chinafocus.lib_network.net.beans.BaseResponse;

import java.util.List;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiMultiService {

    String APP_NO = "007";
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

    @POST("farm360/content/videoDtl")
    Observable<BaseResponse<VideoDetail>> getVideoDetailData(@Body RequestBody body);

    @POST("farm360/content/contentList")
    Observable<BaseResponse<List<VideoContentList>>> getVideoContentList(@Body RequestBody body);

    @POST("farm360/edu/contentList")
    Observable<BaseResponse<List<VideoContentList>>> getEduVideoContentList(@Body RequestBody body);
}
