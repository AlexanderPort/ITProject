package com.example.freeplayandroidclient;

import androidx.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Artist {
    private String artistId;
    private String artistName;

    public Artist(String artistId, String artistName) {
        this.artistId = artistId;
        this.artistName = artistName;
    }

    public Artist(Artist artist) {
        this.artistId = artist.getArtistId();
        this.artistName = artist.getArtistName();
    }

    public String getArtistId() {
        return artistId;
    }
    public String getArtistName() {
        return artistName;
    }
    public static Artist fromJSON(JSONObject json) throws JSONException {
        return new Artist(
                json.getString("artistId"),
                json.getString("artistName"));
    }
    public static List<Artist> fromJSON(JSONArray json) throws JSONException {
        List<Artist> artists = new ArrayList<>(json.length());
        for (int i = 0; i < json.length(); i++) {
            artists.add(fromJSON(json.getJSONObject(i)));
        }
        return artists;
    }

    @NonNull
    @Override
    public String toString() {
        return "Artist{" +
                "artistName='" + artistName + '\'' +
                '}';
    }
}

