package com.example.karaokeparty.viewmodel;

import android.accounts.NetworkErrorException;
import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.karaokeparty.R;
import com.example.karaokeparty.model.SingerModel;
import com.example.karaokeparty.network.NetworkComponent;
import com.example.karaokeparty.repository.SingerRepository;
import com.example.karaokeparty.storage.InternalStorage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SingerViewModel extends AndroidViewModel {

    private static final String STORAGE_KEY = "Internal_Storage_Key";

    private MutableLiveData<ArrayList<SingerModel>> mSingerList = new MutableLiveData<>();

    public SingerViewModel(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<ArrayList<SingerModel>> getObservableSingerList() {
        return mSingerList;
    }

    public void loadData(final OnLoadedDataListener onLoadedDataListener) {
        SingerRepository.getInstance().getSingerList(new NetworkComponent.OnReceivedDataListener() {
            @Override
            public void onSuccess(List<SingerModel> singerList) {
                ArrayList<SingerModel> singerArrayList = new ArrayList<>(singerList);
                setSingerList(singerArrayList);
                Log.i("SingerViewModel", "Singer list was successfully loaded.");
                onLoadedDataListener.OnFinished();

                saveChanges();
            }

            @Override
            public void onFailure(NetworkErrorException e) {
                onLoadedDataListener.onFailed(e);
            }
        });
    }

    public ArrayList<SingerModel> getCachedData() {
        try {
            return (ArrayList<SingerModel>) InternalStorage.readObject(getApplication(), STORAGE_KEY);
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("MainActivity", getApplication()
                    .getResources().getString(R.string.file_read_error) + e);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            Log.d("MainActivity", getApplication()
                    .getResources().getString(R.string.file_read_error) + e);
        }

        return null; // Cached file couldn't be loaded
    }

    public void setSingerList(ArrayList<SingerModel> singerList) {
        mSingerList.setValue(singerList);
    }

    public void removeSinger(int singerPosition) {
        if (mSingerList.getValue() != null) {
            ArrayList<SingerModel> singerList = mSingerList.getValue();
            singerList.remove(singerPosition);

            saveChanges();
        }
    }

    public void moveSinger(int fromPosition, int toPosition) {
        if (mSingerList.getValue() != null) {
            ArrayList<SingerModel> singerList = mSingerList.getValue();

            if (fromPosition < toPosition) {
                for (int i = fromPosition; i < toPosition; i++) {
                    Collections.swap(singerList, i, i + 1);
                }
            } else {
                for (int i = fromPosition; i > toPosition; i--) {
                    Collections.swap(singerList, i, i - 1);
                }
            }

            saveChanges();
        }
    }

    public void addSinger(SingerModel singer) {
        if (mSingerList.getValue() != null) {
            ArrayList<SingerModel> singerList = mSingerList.getValue();
            singerList.add(singer);

            saveChanges();
        }
    }

    private void saveChanges() {
        // Store the list into internal storage
        try {
            InternalStorage.writeObject(getApplication(), STORAGE_KEY, mSingerList.getValue());
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("MainActivity", getApplication()
                    .getResources().getString(R.string.write_file_error) + e);
        }
    }

    public void loadData() {
        ArrayList<SingerModel> singerList = mSingerList.getValue();
        setSingerList(singerList);
        Log.i("SingerViewModel", "Singer list was successfully loaded.");

        saveChanges();
    }

    public interface OnLoadedDataListener {

        void OnFinished();

        void onFailed(Exception e);
    }
}
