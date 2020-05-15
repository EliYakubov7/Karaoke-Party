package com.example.karaokeparty.model;


import java.io.Serializable;

public class SingerModel implements Serializable {

    private String nameOfSinger;
    private String nameOfSong;
    private String album;
    private String lyrics;
    private String currentImagePath;

    private String urlYoutube;

    public SingerModel(String nameOfSinger, String nameOfSong, String album, String lyrics, String currentImagePath, String urlYoutube) {
        this.nameOfSinger = nameOfSinger;
        this.nameOfSong = nameOfSong;
        this.album = album;
        this.lyrics = lyrics;
        this.currentImagePath = currentImagePath;
        this.urlYoutube = urlYoutube;
    }

    public String getNameOfSinger() {
        return nameOfSinger;
    }

    public String getNameOfSong() {
        return nameOfSong;
    }

    public String getAlbum() {
        return album;
    }

    public String getLyrics() {
        return lyrics;
    }

    public String getCurrentImagePath() {
        return currentImagePath;
    }

    public String getUrlYoutube() {
        return urlYoutube;
    }
}
