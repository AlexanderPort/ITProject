package com.example.freeplayandroidclient;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Response;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class TrackRecyclerView extends RecyclerView implements
        MediaPlayer.OnPreparedListener,
        MediaPlayer.OnCompletionListener {
    private API api;
    private TrackAdapter trackAdapter;
    private List<TrackAdapter.Track> tracks;
    private void init() {
        tracks = new ArrayList<>();
        api = new API(getContext());
        setAdapter(new TrackAdapter());
        setLayoutManager(new LinearLayoutManager(getContext()));
    }
    public TrackRecyclerView(@NonNull Context context) {
        super(context);
        init();
    }
    public TrackRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }
    public TrackRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }
    public void setAdapter(TrackAdapter trackAdapter) {
        super.setAdapter(trackAdapter);
        this.trackAdapter = trackAdapter;
    }
    public void addTrack(String trackId, String trackName,
                         String trackDataFormat, String trackImageFormat,
                         List<Album> albums, List<Artist> artists) {

        TrackAdapter.Track.OnClickListener onClickListener =
                new TrackAdapter.Track.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Context context = getContext();
                        if (view.getId() == R.id.play) {
                            if (context instanceof Base) {
                                ((Base) context).setCurrentTrack(
                                        trackId, trackName, trackDataFormat,
                                        trackImageFormat, albums, artists);
                                ((Base) context).runCurrentTrack();
                            }
                        } else if (view.getId() == R.id.actions) {
                            Intent intent = new Intent(context, ActionsActivity.class);
                            intent.putExtra("trackId", trackId);
                            intent.putExtra("trackName", trackName);
                            intent.putExtra("trackDataFormat", trackDataFormat);
                            intent.putExtra("trackImageFormat", trackImageFormat);
                            intent.putExtra("albumId", albums.get(0).getAlbumId());
                            intent.putExtra("albumName", albums.get(0).getAlbumName());
                            intent.putExtra("artistId", artists.get(0).getArtistId());
                            intent.putExtra("artistName", artists.get(0).getArtistName());
                            getContext().startActivity(intent);
                        }
                    }
                };
        TrackAdapter.Track track = new TrackAdapter.Track(
                trackId, trackName, trackDataFormat,
                trackImageFormat, albums, artists);
        track.setOnClickListener(onClickListener);
        File file = new File(String.format("%s/images/%s.jpg",
                getContext().getFilesDir(), trackId));
        if (file.exists()) {
            Bitmap bitmap = BitmapFactory.decodeFile(file.getPath());
            bitmap = Bitmap.createScaledBitmap(bitmap,
                    200, 200, false);
            track.setThumbnail(bitmap);
        } else {
            Response.Listener<Bitmap> listener = new Response.Listener<Bitmap>() {
                @Override
                public void onResponse(Bitmap response) { track.setThumbnail(response); }
            };
            api.getTrackImage(trackId, 200, 200, listener);
        }
        tracks.add(track);
    }
    public void addTrack(Track track) {
        addTrack(track.getTrackId(), track.getTrackName(), track.getTrackDataFormat(),
                track.getTrackImageFormat(), track.getAlbums(), track.getArtists());
    }
    public void addTracks(List<Track> tracks) {
        for (Track track : tracks) addTrack(track);
        trackAdapter.setItems(this.tracks);
        trackAdapter.notifyDataSetChanged();
    }
    public void addTracks(JSONArray response) throws JSONException {
        addTracks(Track.fromJSON(response));
        trackAdapter.notifyDataSetChanged();
    }
    @Override
    public void onCompletion(MediaPlayer mp) {
        if (tracks.size() > 0) {
            TrackAdapter.Track track = trackAdapter.getNextTrack();
            Context context = getContext();
            if (context instanceof Base) {
                ((Base) context).setCurrentTrack(track);
                ((Base) context).runCurrentTrack();
                trackAdapter.increaseNextTrackIndex();
            }
        }
    }
    @Override
    public void onPrepared(MediaPlayer mp) {

    }

    public void clear() {
        tracks.clear();
        trackAdapter.clearItems();
    }
}

