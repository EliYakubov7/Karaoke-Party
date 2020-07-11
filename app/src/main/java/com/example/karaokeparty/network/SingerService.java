package com.example.karaokeparty.network;


import com.example.karaokeparty.model.SingerResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface SingerService {

    String BASE_URL = "YOUR_BASE_URL";

    // String EXTENSION = "androidexam.json";

    @GET("YOUR_EXTENSION_OF_URL")
    Call<List<SingerResponse>> getSingers();
}
