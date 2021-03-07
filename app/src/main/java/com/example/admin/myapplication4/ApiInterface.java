package com.example.admin.myapplication4;

import java.util.List;
import java.util.Map;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Query;

public interface ApiInterface {

    @GET("signUp.php")
    Call<User> performSignUp(@Query("user_name") String user_name,@Query("user_password") String user_password,@Query("user_email") String user_email,@Query("token") String token);

    @GET("login.php")
    Call<User> performLogin(@Query("user_name") String user_name,@Query("user_password") String user_password);

    @GET("friend.php")
    Call<User> friend(@Query("login_user_name") String login_user_name,@Query("friend") String friend);

    @GET("findme.php")
    Call<List<User>> findme(@Query("login_user_name") String login_user_name);

    @GET("addFriend.php")
    Call<List<User>> addfriend(@Query("login_user_name") String login_user_name);

    @Multipart
    @POST("profile.php")
    Call<User> uploadProfile(@Header("Authorization") String authorization, @PartMap Map<String, RequestBody> map, @Part("login_user_message") RequestBody message, @Part("login_user_name") RequestBody login_user_name);

    @FormUrlEncoded
    @POST("profile.php")
    Call<User> without(@Field("login_user_message") String message, @Field("login_user_name") String login_user_name);

    @FormUrlEncoded
    @POST("profile.php")
    Call<User> uploadAgain(@Field("login_user_message") String message, @Field("login_user_name") String login_user_name,@Field("checkAgain") String checkAgain);

    @FormUrlEncoded
    @POST("getFriend.php")
    Call<User> getFriend(@Field("user_name") String user_name);

    @FormUrlEncoded
    @POST("email.php")
    Call<User> emailConfirm(@Field("email") String email);

    @Multipart
    @POST("tmp_image_fcm.php")
    Call<User> fcm_image(@Header("Authorization") String authorization, @PartMap Map<String, RequestBody> map,@Part("login_user_name") RequestBody login_user_name);

    @FormUrlEncoded
    @POST("find_password.php")
    Call<User> findpassword(@Field("email") String email,@Field("id") String id);

}
