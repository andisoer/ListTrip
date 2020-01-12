package com.tugas.listtrip.api;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ApiEndPoints {

    @FormUrlEncoded
    @POST("insert_user.php")
    Call<ResponseBody> register_user(@Field("name")String name,
                                     @Field("email")String email,
                                     @Field("phone")String phone,
                                     @Field("address")String address,
                                     @Field("password_user")String password);

    @FormUrlEncoded
    @POST("select_destination.php")
    Call<ResponseBody> select_destination(@Field("auth")String auth);

    @FormUrlEncoded
    @POST("select_detail_destination.php")
    Call<ResponseBody> select_detail_destination(@Field("id_destination")String id_destination);

    @FormUrlEncoded
    @POST("login.php")
    Call<ResponseBody> login(@Field("email")String email,
                             @Field("password_user")String password_user);
    @FormUrlEncoded
    @POST("update_profile.php")
    Call<ResponseBody> edit_profil(
            @Field("id_user")String id_user,
            @Field("name")String name,
            @Field("email")String email,
            @Field("address")String address,
            @Field("phone")String phone);

    @FormUrlEncoded
    @POST("insert_wishlist.php")
    Call<ResponseBody> insert_wishlist(@Field("id_user")String id_user,
                                       @Field("id_destination")String id_destination);

    @FormUrlEncoded
    @POST("select_wishlist.php")
    Call<ResponseBody> select_wishlist(@Field("id_user")String id_user);

    @FormUrlEncoded
    @POST("select_check_wishlist.php")
    Call<ResponseBody> select_check_wishlist(@Field("id_user")String id_user,
                                             @Field("id_destination")String id_destination);

    @FormUrlEncoded
    @POST("delete_wishlist.php")
    Call<ResponseBody> delete_wishlist(@Field("id_user")String id_user,
                                       @Field("id_destination")String id_destination);

    @FormUrlEncoded
    @POST("update_view.php")
    Call<ResponseBody> update_view(@Field("id_destination")String id_destination);

    @FormUrlEncoded
    @POST("select_rating_user.php")
    Call<ResponseBody> select_rating_user(@Field("id_user")String id_user,
                                     @Field("id_destination")String id_destination);

    @FormUrlEncoded
    @POST("update_rating.php")
    Call<ResponseBody> update_rating(@Field("id_user")String id_user,
                                     @Field("id_destination")String id_destination,
                                     @Field("rating")String rating);
    @FormUrlEncoded
    @POST("select_rating.php")
    Call<ResponseBody> select_rating_destination(@Field("id_destination")String id_destination);

}
