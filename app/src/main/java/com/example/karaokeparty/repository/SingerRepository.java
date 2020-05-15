package com.example.karaokeparty.repository;

import com.example.karaokeparty.network.NetworkComponent;

public class SingerRepository {

    private static SingerRepository instance;
    private NetworkComponent networkComponent;

    private SingerRepository() {
        networkComponent = new NetworkComponent();
    }

    public static SingerRepository getInstance() {

        if (instance == null) {
            instance = new SingerRepository();
        }

        return instance;
    }

    public void getSingerList (NetworkComponent.OnReceivedDataListener callListener) {
        networkComponent.getSingers(callListener);
    }

}
