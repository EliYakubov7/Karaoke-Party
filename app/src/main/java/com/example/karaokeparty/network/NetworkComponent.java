package com.example.karaokeparty.network;

import android.accounts.NetworkErrorException;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.karaokeparty.model.SingerResponse;
import com.example.karaokeparty.model.SingerModel;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NetworkComponent {

    public void getSingers(final OnReceivedDataListener callListener) {

        Call<List<SingerResponse>> call = RestClient.getSingerService().getSingers();
        call.enqueue(new Callback<List<SingerResponse>>() {
            @Override
            public void onResponse(@NonNull Call<List<SingerResponse>> call, @NonNull Response<List<SingerResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {

                    List<SingerModel> singerList = new ArrayList<>();

                    for (SingerResponse singerResponse : response.body()) {

                        SingerModel singerModel = new SingerModel(singerResponse.getSinger(),
                                singerResponse.getSong(),
                                singerResponse.getAlbum(),
                                singerResponse.getLyrics(),
                                singerResponse.getImage(),
                                singerResponse.getUrl());

                        singerList.add(singerModel);
                    }

                    // Pass the results back to the repository
                    callListener.onSuccess(singerList);
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<SingerResponse>> call, @NonNull Throwable t) {
                Log.d("NetworkComponent", "Data couldn't be loaded: " + t);
                callListener.onFailure(new NetworkErrorException(t));
            }
        });
    }


    public interface OnReceivedDataListener {

        void onSuccess(List<SingerModel> singerList);

        void onFailure(NetworkErrorException e);
    }

}