package com.example.freeplayandroidclient;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class Base extends AppCompatActivity implements
        View.OnClickListener,
        MediaPlayer.OnPreparedListener,
        SeekBar.OnSeekBarChangeListener,
        MediaPlayer.OnCompletionListener {
    protected API api;
    protected ImageView next;
    protected SeekBar seekbar;
    protected ImageView control;
    protected LinearLayout homeLayout;
    protected LinearLayout trackLayout;
    protected LinearLayout searchLayout;
    protected LinearLayout profileLayout;
    protected LinearLayout downloadsLayout;
    protected DatabaseHelper databaseHelper;
    protected static User currentUser;
    protected static Track currentTrack;
    private TextView currentTrackView;
    protected static Thread thread;
    private static float[] menuAlpha = new float[] {1.0f, 0.4f, 0.4f, 0.4f};

    protected static boolean playing = false;
    protected static Handler handler = new Handler();
    protected static MediaPlayer mediaPlayer = new MediaPlayer();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); setTitle("YouMusic");
        if (mediaPlayer == null) mediaPlayer = new MediaPlayer();
        databaseHelper = new DatabaseHelper(this);
        currentUser = databaseHelper.selectUser();
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnCompletionListener(this);
        api = new API(getBaseContext());

    }
    @Override
    protected void onStart() {
        super.onStart();

    }
    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        next = (ImageView) findViewById(R.id.next);
        seekbar = (SeekBar) findViewById(R.id.seekBar);
        control = (ImageView) findViewById(R.id.control);
        trackLayout = (LinearLayout) findViewById(R.id.track);
        homeLayout = (LinearLayout) findViewById(R.id.homeLayout);
        profileLayout = (LinearLayout) findViewById(R.id.profileLayout);
        searchLayout = (LinearLayout) findViewById(R.id.searchLayout);
        currentTrackView = (TextView) findViewById(R.id.currentTrack);
        downloadsLayout = (LinearLayout) findViewById(R.id.downloadsLayout);

        next.setOnClickListener(this);
        control.setOnClickListener(this);
        homeLayout.setOnClickListener(this);
        searchLayout.setOnClickListener(this);
        profileLayout.setOnClickListener(this);
        downloadsLayout.setOnClickListener(this);
        seekbar.setOnSeekBarChangeListener(this);
        currentTrackView.setOnClickListener(this);

        homeLayout.setAlpha(menuAlpha[0]);
        searchLayout.setAlpha(menuAlpha[1]);
        profileLayout.setAlpha(menuAlpha[2]);
        downloadsLayout.setAlpha(menuAlpha[3]);

        trackLayout.setVisibility(View.INVISIBLE);
    }
    @Override
    public void onClick(View view) {
        if (view.getId() == homeLayout.getId()) {
            menuAlpha = new float[] {1.0f, 0.4f, 0.4f, 0.4f};
            Intent intent = new Intent(Base.this, MainActivity.class);
            startActivity(intent);
        } else if (view.getId() == searchLayout.getId()) {
            menuAlpha = new float[] {0.4f, 1.0f, 0.4f, 0.4f};
            Intent intent = new Intent(Base.this, SearchActivity.class);
            startActivity(intent);
        } else if (view.getId() == profileLayout.getId()) {
            menuAlpha = new float[] {0.4f, 0.4f, 1.0f, 0.4f};
            Intent intent = new Intent(Base.this, ProfileActivity.class);
            startActivity(intent);
        } else if (view.getId() == downloadsLayout.getId()) {
            menuAlpha = new float[] {0.4f, 0.4f, 0.4f, 1.0f};
            Intent intent = new Intent(Base.this, DownloadsActivity.class);
            startActivity(intent);
        } else if (view.getId() == R.id.control) {
            playing = !playing;
            if (playing) {
                ((ImageView)view).setImageResource(R.mipmap.pause);
                if (mediaPlayer != null) mediaPlayer.start();
            } else {
                ((ImageView)view).setImageResource(R.mipmap.play);
                if (mediaPlayer != null) mediaPlayer.pause();
            }
        } else if (view.getId() == R.id.next) {
            onCompletion(mediaPlayer);
        } else if (view.getId() == R.id.currentTrack) {
            Intent intent = new Intent(Base.this, ActionsActivity.class);
            intent.putExtra("trackId", currentTrack.getTrackId());
            intent.putExtra("trackName", currentTrack.getTrackName());
            intent.putExtra("albumId", currentTrack.getAlbums().get(0).getAlbumId());
            intent.putExtra("albumName", currentTrack.getAlbums().get(0).getAlbumName());
            intent.putExtra("artistId", currentTrack.getArtists().get(0).getArtistId());
            intent.putExtra("artistName", currentTrack.getArtists().get(0).getArtistName());
            startActivity(intent);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseMediaPlayer();
    }
    protected void releaseMediaPlayer() {
        if (mediaPlayer != null) {
            try {
                mediaPlayer.release();
                mediaPlayer = null;
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }
    @Override
    protected void onPause() {
        super.onPause();

    }
    @Override
    protected void onStop() {
        super.onStop();

    }
    @Override
    protected void onResume() {
        super.onResume();

    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();


    }
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (fromUser) {
            if (mediaPlayer == null) mediaPlayer = new MediaPlayer();
            double time = mediaPlayer.getDuration() * ((double)progress / seekBar.getMax());
            mediaPlayer.seekTo((int)time);
        }
    }
    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }
    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
    @Override
    public void onCompletion(MediaPlayer mp) {
        try {
            if (thread != null) thread.join();
        } catch (InterruptedException exception) {
            exception.printStackTrace();
        }
    }
    @Override
    public void onPrepared(MediaPlayer mp) {
        if (currentTrack == null) return;
        playing = true;
        trackLayout.getLayoutParams().height = LinearLayout.LayoutParams.MATCH_PARENT;
        trackLayout.requestLayout();
        setCurrentTrack(currentTrack);
        trackLayout.setVisibility(View.VISIBLE);
        control.setImageResource(R.mipmap.pause);
        seekbar.setMax(mediaPlayer.getDuration());
        mediaPlayer.start();
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                if (mediaPlayer == null) return;
                double time = mediaPlayer.getCurrentPosition();
                seekbar.setProgress((int) time);
                if (time == mediaPlayer.getDuration()) return;
                handler.postDelayed(this, 100);
            }
        });
        handler.postDelayed(thread, 100);
    }
    public void setCurrentTrack(@Nullable Track track) {
        currentTrack = track; if (currentTrack == null) return;
        String text = track.getTrackName() + " by " + track.getArtists().get(0).getArtistName();
        if (text.length() > 40) text = text.substring(0, 40) + "...";
        currentTrackView.setText(text);
    }
    public void setCurrentTrack(
            String trackId, String trackName,
            String trackDataFormat, String trackImageFormat,
            List<Album> albums, List<Artist> artists) {
        setCurrentTrack(new Track(trackId, trackName,
                trackDataFormat, trackImageFormat, albums, artists));
    }
    public void runCurrentTrack() {
        if (currentTrack == null) return;
        if (mediaPlayer == null) return; mediaPlayer.reset();
        try {
            File file = new File(String.format(
                    "%s/tracks/%s." + currentTrack.getTrackDataFormat(),
                    getFilesDir(), currentTrack.getTrackId()));
            if (file.exists()) {
                mediaPlayer.setDataSource(file.getPath());
            } else {
                mediaPlayer.setDataSource(api.getTrackDataURL(
                        currentTrack.getTrackId()));
            }
            mediaPlayer.prepare();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }
}

