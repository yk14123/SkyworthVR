package com.chinafocus.hvrskyworthvr.net;

import com.chinafocus.hvrskyworthvr.model.bean.DefaultCloudUrl;
import com.chinafocus.hvrskyworthvr.model.multibean.DeviceInfo;
import com.chinafocus.lib_network.net.beans.BaseResponse;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiMultiService {

    String appNo = "003";

//    UserInfoBean userInfoBean = new UserInfoBean("18808086666", "1");
//    RequestBody body = FormBody.create(MediaType.parse("application/json; charset=utf-8"), new Gson().toJson(userInfoBean));
//    //接口对象实例调用相关接口，获得Observable对象
//    Observable<BaseModel> observable = api.updateUserInfo3(body);

    @POST("farm360/sysCloud/defaultCloudUrl")
    Observable<BaseResponse<DefaultCloudUrl>> getDefaultCloudUrl(@Body RequestBody body);

    @POST("farm360/cstmHwAlias/hwAlias")
    Observable<BaseResponse<Object>> initDeviceInfo(@Body RequestBody body);

    @POST("farm360/cstmHwAlias/getHwAlias")
    Observable<BaseResponse<DeviceInfo>> getDeviceInfo(@Body RequestBody body);

    @POST("farm360/cstmHwAlias/editHwAlias")
    Observable<BaseResponse<Object>> alterDeviceAlias(@Body RequestBody body);


//    @GET("store-api/index/slide")
//    Observable<BaseResponse<List<Banner>>> getBanner();
//
//    @GET("store-api/sys/classify/video")
//    Observable<BaseResponse<List<VideoCategory>>> getVideoCateGory();
//
//    @GET("store-api/panoramic/video/page")
//    Observable<BaseResponse<VideoListData>> getVideoListData(@Query("category") int category, @Query("pageNo") int pageNo, @Query("pageSize") int pageSize, @Query("sortBy") int sortBy);
//
//    @GET("store-api/panoramic/publish/page")
//    Observable<BaseResponse<VideoListData>> getPublishListData(@Query("pageNo") int pageNo, @Query("pageSize") int pageSize);
//
//    @GET("store-api/panoramic/{tag}/detail/{id}")
//    Observable<BaseResponse<VideoDetail>> getVideoDetailData(@Path("tag") String tag, @Path("id") int id);
}
