package com.example.karaokeparty.network;


import com.example.karaokeparty.model.SingerResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface SingerService {

    String BASE_URL = "https://api.npoint.io/";

    // String EXTENSION = "androidexam.json";

    @GET("1ec72bec9856f026479d")
    Call<List<SingerResponse>> getSingers();
}
