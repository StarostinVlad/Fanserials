package com.example.fan.api.retro;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.fan.utils.Utils.DOMAIN;

public class NetworkService {
    private static final String BASE_URL = "/api/v1/";
    private static NetworkService mInstance;
    private Retrofit mRetrofit;

    private NetworkService() {
        mRetrofit = new Retrofit.Builder()
//                .baseUrl(RemoteConfig.read(RemoteConfig.DOMAIN) + BASE_URL)
                .baseUrl(DOMAIN + BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public static NetworkService getInstance() {
        if (mInstance == null) {
            mInstance = new NetworkService();
        }
        return mInstance;
    }

    public APIService getSerials() {
        return mRetrofit.create(APIService.class);
    }

    public APIService getProfile() {
        return mRetrofit.create(APIService.class);
    }

    public APIService getToken() {
        return mRetrofit.create(APIService.class);
    }
}