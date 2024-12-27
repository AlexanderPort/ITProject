package com.example.freeplayandroidclient;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.PorterDuff;

import androidx.annotation.NonNull;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class DatabaseHelper {
    private final Context context;
    private final String DATABASE_NAME = "database.db";
    private final String ALBUM = "create table if not exists album(" +
            "albumId varchar(36) not null primary key, albumName varchar(128) not null)";
    private final String ARTIST = "create table if not exists artist(" +
            "artistId varchar(36) not null primary key, artistName varchar(128) not null)";
    private final String TRACK = "create table if not exists track(" +
            "trackId varchar(36) not null primary key, trackName varchar(128) not null, " +
            "trackDataFormat varchar(5) not null, trackImageFormat varchar(5) not null)";
    private final String TRACK_ALBUM = "create table if not exists track_album(" +
            "trackId varchar(36) not null, albumId varchar(36) not null, " +
            "foreign key(trackId) references track(trackId), " +
            "foreign key(albumId) references album(albumId))";
    private final String TRACK_ARTIST = "create table if not exists track_artist(" +
            "trackId varchar(36) not null, artistId varchar(36) not null, " +
            "foreign key(trackId) references track(trackId), " +
            "foreign key(artistId) references artist(artistId))";
    private final String USER = "create table if not exists user(" +
            "userId varchar(36) not null, userName varchar(128) not null," +
            "userEmail varchar(32) not null, userPassword varchar(32) not null," +
            "userStatus integer not null default 0, primary key(userId))";

    public DatabaseHelper(Context context) {
        this.context = context;
        onCreate();
    }

    public void onCreate() {
        SQLiteDatabase db = getDatabase();
        //db.execSQL("drop table if exists user");
        //db.execSQL("drop table if exists track");
        //db.execSQL("drop table if exists album");
        //db.execSQL("drop table if exists artist");
        //db.execSQL("drop table if exists track_album");
        //db.execSQL("drop table if exists track_artist");
        db.execSQL(USER);
        db.execSQL(TRACK);
        db.execSQL(ALBUM);
        db.execSQL(ARTIST);
        db.execSQL(TRACK_ALBUM);
        db.execSQL(TRACK_ARTIST);
        db.close();
    }

    public void insertUser(User user) {
        try {
            SQLiteDatabase db = getDatabase();
            String query = String.format(
                    "INSERT INTO user VALUES ('%s', '%s', '%s', '%s', '%s');",
                    user.getUserId(), user.getUserName(), user.getUserEmail(),
                    user.getUserPassword(), user.getUserStatus() ? 1 : 0);
            db.execSQL(query); db.close();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public void updateUser(User user) {
        try {
            SQLiteDatabase db = getDatabase();
            String query = String.format(
                    "UPDATE user SET userId='%s', userName='%s', userEmail='%s', " +
                            "userPassword='%s', userStatus='%s' WHERE userId='%s'",
                    user.getUserId(), user.getUserName(), user.getUserEmail(),
                    user.getUserPassword(), user.getUserStatus() ? 1 : 0, user.getUserId());
            db.execSQL(query); db.close();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public User selectUser() {
        SQLiteDatabase db = getDatabase();
        Cursor cursor = db.rawQuery("select * from user", null);
        while (cursor.moveToNext()) {
            User user = new User();
            user.setUserId(cursor.getString(0));
            user.setUserName(cursor.getString(1));
            user.setUserEmail(cursor.getString(2));
            user.setUserPassword(cursor.getString(3));
            user.setUserStatus(cursor.getInt(4) == 1);
            return user;
        } cursor.close(); db.close();
        return null;
    }

    public void insertArtist(Artist artist) {
        try {
            SQLiteDatabase db = getDatabase();
            String query = String.format(
                    "INSERT INTO artist VALUES ('%s', '%s');",
                    artist.getArtistId(), artist.getArtistName());
            db.execSQL(query); db.close();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
    public void insertAlbum(Album album) {
        try {
            SQLiteDatabase db = getDatabase();
            String query = String.format(
                    "INSERT INTO album VALUES ('%s', '%s');",
                    album.getAlbumId(), album.getAlbumName());
            db.execSQL(query); db.close();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
    public void insertTrack(Track track) {
        try {
            SQLiteDatabase db = getDatabase();
            String query = String.format(
                    "INSERT INTO track VALUES ('%s', '%s', '%s', '%s');",
                    track.getTrackId(), track.getTrackName(),
                    track.getTrackDataFormat(), track.getTrackImageFormat());
            db.execSQL(query);
            for (Album album : track.getAlbums()) {
                query = String.format(
                        "INSERT INTO track_album VALUES ('%s', '%s');",
                        track.getTrackId(), album.getAlbumId());
                db.execSQL(query);
            }
            for (Artist artist : track.getArtists()) {
                query = String.format(
                        "INSERT INTO track_artist VALUES ('%s', '%s');",
                        track.getTrackId(), artist.getArtistId());
                db.execSQL(query);
            }
            db.close();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
    /*
    public void onUpgrade(int oldVersion, int newVersion) {
        SQLiteDatabase db = getDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + TABLE);
    }
    */
    public List<Track> selectAllTracks() {
        SQLiteDatabase db = getDatabase();
        String trackQuery = "select * from track";
        String albumQuery = "select album.albumId, album.albumName from track, album, track_album " +
                "where track.trackId=track_album.trackId and album.albumId=track_album.albumId and track.trackId=";
        String artistQuery = "select artist.artistId, artist.artistName from track, artist, track_artist " +
                "where track.trackId=track_artist.trackId and artist.artistId=track_artist.artistId and track.trackId=";
        ArrayList<Track> tracks = new ArrayList<>();
        Cursor trackCursor = db.rawQuery(trackQuery, null);
        while (trackCursor.moveToNext()) {
            String trackId = trackCursor.getString(0);
            String trackName = trackCursor.getString(1);
            String trackDataFormat = trackCursor.getString(2);
            String trackImageFormat = trackCursor.getString(3);
            Track track = new Track(trackId, trackName, trackDataFormat, trackImageFormat);

            trackId = String.format("'%s'", trackId);

            Cursor albumCursor = db.rawQuery(albumQuery + trackId, null);
            while (albumCursor.moveToNext()) {
                track.addAlbum(new Album(
                        albumCursor.getString(0),
                        albumCursor.getString(1)));
            } albumCursor.close();

            Cursor artistCursor = db.rawQuery(artistQuery + trackId, null);
            while (artistCursor.moveToNext()) {
                track.addArtist(new Artist(
                        artistCursor.getString(0),
                        artistCursor.getString(1)));
            } artistCursor.close();
            tracks.add(track);
        }
        trackCursor.close(); db.close();
        return tracks;
    }
    public SQLiteDatabase getDatabase() {
        return context.openOrCreateDatabase(DATABASE_NAME, Context.MODE_PRIVATE, null);
    }
}


