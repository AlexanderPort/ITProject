package com.example.freeplayandroidclient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Playlist {
    private String userId;
    private String playlistId;
    private String playlistName;

    public Playlist() {}

    public Playlist(String userId, String playlistId, String playlistName) {
        this.userId = userId;
        this.playlistId = playlistId;
        this.playlistName = playlistName;
    }

    public String getUserId() {
        return userId;
    }
    public String getPlaylistId() {
        return playlistId;
    }
    public String getPlaylistName() {
        return playlistName;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
    public void setPlaylistId(String playlistId) {
        this.playlistId = playlistId;
    }
    public void setPlaylistName(String playlistName) {
        this.playlistName = playlistName;
    }

    public static Playlist fromJSON(JSONObject json) throws JSONException {
        return new Playlist(
                json.getString("userId"),
                json.getString("playlistId"),
                json.getString("playlistName"));
    }

    public static List<Playlist> fromJSON(JSONArray json) throws JSONException {
        List<Playlist> playlists = new ArrayList<>();
        for (int i = 0; i < json.length(); i++) {
            playlists.add(Playlist.fromJSON(json.getJSONObject(i)));
        }
        return playlists;
    }
}

