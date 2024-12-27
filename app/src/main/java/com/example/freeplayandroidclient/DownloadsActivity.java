package com.example.freeplayandroidclient;


import android.media.MediaPlayer;
import android.os.Bundle;

public class DownloadsActivity extends Base {
    private TrackRecyclerView trackRecyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        databaseHelper = new DatabaseHelper(getBaseContext());
        trackRecyclerView = findViewById(R.id.trackRecyclerView);
        trackRecyclerView.addTracks(databaseHelper.selectAllTracks());
        onPrepared(mediaPlayer);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
    @Override
    public void onPrepared(MediaPlayer mp) {
        super.onPrepared(mp);
        trackRecyclerView.onPrepared(mp);
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        super.onCompletion(mp);
        trackRecyclerView.onCompletion(mp);
    }
}
