package com.example.karaokeparty.network;


import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

class RestClient {

    private static SingerService singerService;

    // Build OkHttpClient
    private static OkHttpClient httpClient = new OkHttpClient.Builder()
            .connectTimeout(40, TimeUnit.SECONDS)
            .build();


    public static SingerService getSingerService() {
        if (singerService == null) {
            // Build a Retrofit client, then create singer service
            Retrofit retrofit = new Retrofit.Builder().baseUrl(SingerService.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(httpClient)
                    .build();
            singerService = retrofit.create(SingerService.class);
        }
        return singerService;
    }
}
