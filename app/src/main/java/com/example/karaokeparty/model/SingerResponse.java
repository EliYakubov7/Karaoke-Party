package com.example.karaokeparty.model;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SingerResponse {

    @SerializedName("singer")
    @Expose
    private String singer;

    @SerializedName("song")
    @Expose
    private String song;

    @SerializedName("album")
    @Expose
    private String album;

    @SerializedName("lyrics")
    @Expose
    private String lyrics;

    @SerializedName("image")
    @Expose
    private String image;

    @SerializedName("url")
    @Expose
    private String url;

    public String getSinger() {
        return singer;
    }

    public void setSinger(String singer) {
        this.singer = singer;
    }

    public String getSong() {
        return song;
    }

    public void setSong(String song) {
        this.song = song;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getLyrics() {
        return lyrics;
    }

    public void setLyrics(String lyrics) {
        this.lyrics = lyrics;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}