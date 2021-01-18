package com.utt.API_Interface;

import com.utt.model.Pothole;
import com.utt.model.ResponseData;
import com.utt.model.mUser;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface Pothole_Interface {
    @GET("/api/v1/pothole")
    Call<Pothole> getAllPothole();

//    @FormUrlEncoded
//    @POST("/api/v1/pothole")
//    Call<RequestBody> postAddPothole(
//            @Field("name") String name,
//            @Field("email") String email,
//            @Field("latitude") Double latitude,
//            @Field("longitude") Double longitude,
//            @Field("image") String image,
//            @Field("note") String note
//    );

    @Multipart
    @POST("/api/v1/pothole")
    Call<ResponseData> postAddPotholee(
            @Part("name") String name,
            @Part("email") String email,
            @Part("latitude") Double latitude,
            @Part("longitude") Double longitude,
            @Part("image") String image,
            @Part("note") String note

    );
}
