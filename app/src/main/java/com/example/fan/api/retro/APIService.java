package com.example.fan.api.retro;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface APIService {
    @GET("episodes?limit=20")
    Call<FANAPI> getSerials(@Query("offset") int page);

    @GET("profile/tape")
    Call<FANAPI> getProfile(@Query("offset") int page, @Query("token") String token);

    @GET("profile/viewed")
    Call<List<Viewed>> getViewed(@Query("token") String token);

    @POST("auth/social")
    Call<Token> getToken(@Query("code") String code);

    @POST("auth")
    Call<ResponseBody> getToken(@Query("email") String email, @Query("password") String pass);

    @POST("profile/viewed/{id}/")
    Call<PutViewed> putViewed(@Path("id") int id, @Query("checked") boolean check);

    @GET("search")
    Call<List<SearchResult>> getSearch(@Query("query") String query);
}