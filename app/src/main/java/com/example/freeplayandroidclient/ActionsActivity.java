package com.example.freeplayandroidclient;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ActionsActivity extends Base {
    private Track track;
    private LinearLayout shareView;
    private LinearLayout downloadView;
    private TextView trackNameView;
    private TextView artistNameView;
    private LinearLayout addInAlbumView;
    private ImageView thumbnailView;
    private LinearLayout goToArtistView;
    private LinearLayout addInFavouritesView;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actions);
        shareView = (LinearLayout) findViewById(R.id.share);
        downloadView = (LinearLayout) findViewById(R.id.download);
        trackNameView = (TextView) findViewById(R.id.trackName);
        thumbnailView = (ImageView) findViewById(R.id.thumbnail);
        artistNameView = (TextView) findViewById(R.id.artistName);
        addInAlbumView = (LinearLayout) findViewById(R.id.addInAlbum);
        goToArtistView = (LinearLayout) findViewById(R.id.goToArtist);
        addInFavouritesView = (LinearLayout) findViewById(R.id.addInFavourites);

        shareView.setOnClickListener(this);
        downloadView.setOnClickListener(this);
        goToArtistView.setOnClickListener(this);

        Intent intent = getIntent();
        track = new Track(
                intent.getStringExtra("trackId"),
                intent.getStringExtra("trackName"),
                intent.getStringExtra("trackDataFormat"),
                intent.getStringExtra("trackImageFormat"));
        track.addAlbum(new Album(
                intent.getStringExtra("albumId"),
                intent.getStringExtra("albumName")));
        track.addArtist(new Artist(
                intent.getStringExtra("artistId"),
                intent.getStringExtra("artistName")));

        trackNameView.setText(track.getTrackName());
        artistNameView.setText(track.getArtists().get(0).getArtistName());

        databaseHelper = new DatabaseHelper(getBaseContext());

        File file = new File(String.format("%s/images/%s.jpg", getFilesDir(), track.getTrackId()));
        if (file.exists()) {
            Bitmap bitmap = BitmapFactory.decodeFile(file.getPath());
            thumbnailView.setImageBitmap(bitmap);
        } else {
            Response.Listener<Bitmap> listener = new Response.Listener<Bitmap>() {
                @Override
                public void onResponse(Bitmap response) {
                    thumbnailView.setImageBitmap(response);
                }
            };
            api.getTrackImage(track.getTrackId(), 650, 650, listener);
        }
        onPrepared(mediaPlayer);
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        if (view.getId() == downloadView.getId()) {
            Response.Listener<byte[]> listener = new Response.Listener<byte[]>() {
                @Override
                public void onResponse(byte[] response) {
                    saveTrack(response, track.getTrackId());
                }
            };
            Response.ErrorListener errorListener = new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                }
            };
            api.getTrackData(track.getTrackId(), listener, errorListener);
            saveThumbnail(track.getTrackId(), 650, 650);
        } else if (view.getId() == goToArtistView.getId()) {
            Intent intent = new Intent(ActionsActivity.this, ArtistActivity.class);
            intent.putExtra("artistId", track.getArtists().get(0).getArtistId());
            intent.putExtra("artistName", track.getArtists().get(0).getArtistName());
            startActivity(intent);
        } else if (view.getId() == shareView.getId()) {
            if (track == null) return;
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            File file = new File(getFilesDir() + "/tracks",
                    track.getTrackId() + "." + track.getTrackDataFormat());
            if (file.exists()) {
                Uri uri = FileProvider.getUriForFile(getApplicationContext(),
                        "com.example.freeplayandroidclient.provider", file);
                shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
                shareIntent.setType("audio/*");
            } else {
                String url = api.getTrackDataURL(track.getTrackId());
                shareIntent.putExtra(Intent.EXTRA_TEXT, url);
                shareIntent.setType("text/*");
            }
            startActivity(Intent.createChooser(shareIntent, track.getTrackName()));
        }
    }
    public void saveTrack(byte[] bytes, String filename) {
        Toast.makeText(this, "Downloading track...", Toast.LENGTH_LONG).show();
        Album album = track.getAlbums().get(0);
        Artist artist = track.getArtists().get(0);
        databaseHelper.insertArtist(artist);
        databaseHelper.insertAlbum(album);
        databaseHelper.insertTrack(track);
        File directory = new File(getFilesDir() + "/tracks/");
        if (!directory.exists()) { boolean status = directory.mkdir(); }
        filename = getFilesDir() + "/tracks/" + filename + "." + track.getTrackDataFormat();
        try {
            File file = new File(filename);
            if (!file.exists()) { boolean status = file.createNewFile(); }
            else { Toast.makeText(this, "Track already saved", Toast.LENGTH_LONG).show(); }
            FileOutputStream fos = new FileOutputStream(filename);
            fos.write(bytes); fos.flush(); fos.close();
            Toast.makeText(this, "Track successfully saved", Toast.LENGTH_LONG).show();
        } catch (IOException exception) {
            exception.printStackTrace();
            Toast.makeText(this, "Error", Toast.LENGTH_LONG).show();
        }
    }
    public void saveThumbnail(String trackId, int maxWidth, int maxHeight) {
        Response.Listener<Bitmap> listener = new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap response) {
                File directory = new File(getFilesDir() + "/images/");
                if (!directory.exists()) { boolean status = directory.mkdir(); }
                String filename = String.format("%s/images/%s.jpg", getFilesDir(), trackId);
                try (FileOutputStream out = new FileOutputStream(filename)) {
                    response.compress(Bitmap.CompressFormat.JPEG, 100, out);
                } catch (IOException exception) {
                    exception.printStackTrace();
                }
            }
        };
        api.getTrackImage(trackId, 650, 650, listener);
    }
}
