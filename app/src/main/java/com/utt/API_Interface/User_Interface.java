package com.utt.API_Interface;

import com.utt.model.mUser;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface User_Interface {
    //get all user
    @GET("/api/v1/employees/read.php")
    Call<mUser> getAllUser();


}
