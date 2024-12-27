package com.example.freeplayandroidclient;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Track {
    protected String trackId;
    protected String trackName;
    protected List<Album> albums;
    protected List<Artist> artists;
    protected String trackDataFormat;
    protected String trackImageFormat;
    public Track(String trackId, String trackName,
                 String trackDataFormat, String trackImageFormat) {
        this.trackId = trackId;
        this.trackName = trackName;
        this.albums = new ArrayList<>();
        this.artists = new ArrayList<>();
        this.trackDataFormat = trackDataFormat;
        this.trackImageFormat = trackImageFormat;
    }
    public Track(String trackId, String trackName,
                 String trackDataFormat, String trackImageFormat,
                 List<Album> albums, List<Artist> artists) {
        this.albums = albums;
        this.artists = artists;
        this.trackId = trackId;
        this.trackName = trackName;
        this.trackDataFormat = trackDataFormat;
        this.trackImageFormat = trackImageFormat;
    }
    public Track(Track track) {
        this.trackId = track.getTrackId();
        this.trackName = track.getTrackName();
        this.albums = track.getAlbums();
        this.artists = track.getArtists();
    }
    public String getTrackId() {
        return trackId;
    }
    public String getTrackName() {
        return trackName;
    }
    public List<Album> getAlbums() {
        return albums;
    }
    public List<Artist> getArtists() {
        return artists;
    }
    public void addAlbum(Album album) {
        albums.add(album);
    }
    public void addArtist(Artist artist) {
        artists.add(artist);
    }
    public String getTrackDataFormat() {
        return trackDataFormat;
    }
    public String getTrackImageFormat() {
        return trackImageFormat;
    }

    public static Track fromJSON(JSONObject json) throws JSONException {
        return new Track(
                json.getString("trackId"),
                json.getString("trackName"),
                json.getString("trackDataFormat"),
                json.getString("trackImageFormat"),
                Album.fromJSON(json.getJSONArray("albums")),
                Artist.fromJSON(json.getJSONArray("artists")));
    }
    public static List<Track> fromJSON(JSONArray json) throws JSONException {
        List<Track> tracks = new ArrayList<>();
        for (int i = 0; i < json.length(); i++) {
            tracks.add(Track.fromJSON(json.getJSONObject(i)));
        }
        return tracks;
    }

    @Override
    public String toString() {
        return "Track{" +
                "trackName=" + trackName + "}";
    }
}

