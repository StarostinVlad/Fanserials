package com.example.fan.api.retro;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface APIService {
    @GET("episodes?limit=20")
    Call<FANAPI> getSerials(@Query("offset") int page);
}