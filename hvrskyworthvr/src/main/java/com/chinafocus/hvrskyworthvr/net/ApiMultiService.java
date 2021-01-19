package com.chinafocus.hvrskyworthvr.net;

import com.chinafocus.hvrskyworthvr.model.bean.Banner;
import com.chinafocus.hvrskyworthvr.model.bean.DefaultCloudUrl;
import com.chinafocus.hvrskyworthvr.model.bean.PublishDataList;
import com.chinafocus.hvrskyworthvr.model.bean.VideoCategory;
import com.chinafocus.hvrskyworthvr.model.bean.VideoDetail;
import com.chinafocus.hvrskyworthvr.model.bean.VideoListData;
import com.chinafocus.hvrskyworthvr.model.multibean.DeviceInfo;
import com.chinafocus.lib_network.net.beans.BaseResponse;

import java.util.List;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiMultiService {

    String appNo = "003";

    @POST("farm360/sysCloud/defaultCloudUrl")
    Observable<BaseResponse<DefaultCloudUrl>> getDefaultCloudUrl(@Body RequestBody body);

    @POST("farm360/cstmHwAlias/hwAlias")
    Observable<BaseResponse<Object>> initDeviceInfo(@Body RequestBody body);

    @POST("farm360/cstmHwAlias/getHwAlias")
    Observable<BaseResponse<DeviceInfo>> getDeviceInfoName(@Body RequestBody body);

    @POST("farm360/cstmHwAlias/editHwAlias")
    Observable<BaseResponse<Object>> postSetDeviceAlias(@Body RequestBody body);

    @POST("farm360/content/viewSlides")
    Observable<BaseResponse<List<Banner>>> getPublishBanner(@Body RequestBody body);

    @POST("farm360/content/videoSlides")
    Observable<BaseResponse<List<Banner>>> getVideoBanner(@Body RequestBody body);

    @POST("farm360/content/viewList")
    Observable<BaseResponse<List<PublishDataList>>> getPublishListData(@Body RequestBody body);

    @POST("farm360/content/myClassify")
    Observable<BaseResponse<List<VideoCategory>>> getVideoCateGory(@Body RequestBody body);

    @POST("farm360/content/videoList")
    Observable<BaseResponse<VideoListData>> getVideoListData(@Body RequestBody body);

    @POST("farm360/content/videoDtl")
    Observable<BaseResponse<VideoDetail>> getVideoDetailData(@Body RequestBody body);
}
