package com.example.freeplayandroidclient;

import androidx.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Album {
    private String albumId;
    private String albumName;
    private List<Artist> artists;

    public Album(String albumId, String albumName) {
        this.albumId = albumId;
        this.albumName = albumName;
        this.artists = new ArrayList<>();
    }
    public String getAlbumId() {
        return albumId;
    }
    public String getAlbumName() {
        return albumName;
    }
    public List<Artist> getArtists() {
        return artists;
    }
    public static Album fromJSON(JSONObject json) throws JSONException {
        return new Album(
                json.getString("albumId"),
                json.getString("albumName"));
    }
    public static List<Album> fromJSON(JSONArray json) throws JSONException {
        List<Album> albums = new ArrayList<>(json.length());
        for (int i = 0; i < json.length(); i++) {
            albums.add(fromJSON(json.getJSONObject(i)));
        }
        return albums;
    }
    @NonNull
    @Override
    public String toString() {
        return "Album{" +
                "albumName='" + albumName + '\'' +
                '}';
    }
}

