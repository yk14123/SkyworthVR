package com.chinafocus.hvrskyworthvr.net.edu;

import com.chinafocus.hvrskyworthvr.model.bean.VideoCategory;
import com.chinafocus.hvrskyworthvr.model.bean.VideoContentList;
import com.chinafocus.hvrskyworthvr.model.bean.VideoDetail;
import com.chinafocus.lib_network.net.beans.BaseResponse;

import java.util.List;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface EduApiService {

    @POST("farm360/edu/cstmClass")
    Observable<BaseResponse<List<VideoCategory>>> getVideoListCategory(@Body RequestBody body);

    @POST("farm360/edu/contentList")
    Observable<BaseResponse<List<VideoContentList>>> getVideoListFromCategory(@Body RequestBody body);

    @POST("farm360/edu/videoDtl")
    Observable<BaseResponse<VideoDetail>> getVideoDetailData(@Body RequestBody body);
}
