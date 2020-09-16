package com.starostinvlad.fan.api.retro;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import okhttp3.Authenticator;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.starostinvlad.fan.utils.Utils.DOMAIN;
import static com.starostinvlad.fan.utils.Utils.PROXY;
import static com.starostinvlad.fan.utils.Utils.credential;

public class NetworkService {
    private static final String BASE_URL = "/api/v1/";
    private static NetworkService mInstance;
    private Retrofit mRetrofit;

    private NetworkService() {
        OkHttpClient client = null;
        if (PROXY != null) {
            Log.d("NEtworkService", "proxy: " + PROXY.address().toString());
            Authenticator proxyAuthenticator = (route, response) -> response.request().newBuilder()
                    .header("Proxy-Authorization", credential)
                    .build();
            client = new OkHttpClient.Builder().proxy(PROXY).proxyAuthenticator(proxyAuthenticator).build();
        } else
            client = new OkHttpClient.Builder().build();

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();
        mRetrofit = new Retrofit.Builder()
                .baseUrl(DOMAIN + BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
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