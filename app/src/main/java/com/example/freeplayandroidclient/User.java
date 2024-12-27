package com.example.freeplayandroidclient;

import android.content.Intent;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class User {
    private String userId;
    private String userName;
    private String userEmail;
    private Boolean userStatus;
    private String userPassword;
    private List<Artist> artists = new ArrayList<>();
    private List<Playlist> playlists = new ArrayList<>();

    public User() {}

    public String getUserId() {
        return userId;
    }
    public String getUserName() {
        return userName;
    }
    public String getUserEmail() {
        return userEmail;
    }
    public Boolean getUserStatus() {
        return userStatus;
    }
    public String getUserPassword() {
        return userPassword;
    }
    public List<Artist> getArtists() {
        return artists;
    }
    public List<Playlist> getPlaylists() {
        return playlists;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }
    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }
    public void setUserStatus(Boolean userStatus) {
        this.userStatus = userStatus;
    }
    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }
    public void setArtists(List<Artist> artists) {
        this.artists = artists;
    }
    public void setPlaylists(List<Playlist> playlists) {
        this.playlists = playlists;
    }

    public static User fromJSON(JSONObject json) throws JSONException {
        User user = new User();
        user.setUserStatus(true);
        user.setUserId(json.getString("userId"));
        user.setUserName(json.getString("userName"));
        user.setUserEmail(json.getString("userEmail"));
        user.setUserPassword(json.getString("userPassword"));
        user.setArtists(Artist.fromJSON(json.getJSONArray("artists")));
        user.setPlaylists(Playlist.fromJSON(json.getJSONArray("playlists")));
        return user;
    }
}
