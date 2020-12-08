package com.chinafocus.hvrskyworthvr.net;

import com.chinafocus.hvrskyworthvr.model.bean.Banner;
import com.chinafocus.hvrskyworthvr.model.bean.DefaultCloudUrl;
import com.chinafocus.hvrskyworthvr.model.bean.VideoCategory;
import com.chinafocus.hvrskyworthvr.model.bean.VideoDetail;
import com.chinafocus.hvrskyworthvr.model.bean.VideoListData;
import com.chinafocus.lib_network.net.beans.BaseResponse;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    @POST("http://39.102.32.19:8001/api/v1/sysCloud/getDefaultCloudUrl")
    Observable<BaseResponse<DefaultCloudUrl>> getDefaultCloudUrl();

    @GET("store-api/index/slide")
    Observable<BaseResponse<List<Banner>>> getBanner();

    @GET("store-api/sys/classify/video")
    Observable<BaseResponse<List<VideoCategory>>> getVideoCateGory();

    @GET("store-api/panoramic/video/page")
    Observable<BaseResponse<VideoListData>> getVideoListData(@Query("category") int category, @Query("pageNo") int pageNo, @Query("pageSize") int pageSize, @Query("sortBy") int sortBy);

    @GET("store-api/panoramic/publish/page")
    Observable<BaseResponse<VideoListData>> getPublishListData(@Query("pageNo") int pageNo, @Query("pageSize") int pageSize);

    @GET("store-api/panoramic/{tag}/detail/{id}")
    Observable<BaseResponse<VideoDetail>> getVideoDetailData(@Path("tag") String tag, @Path("id") int id);
}
