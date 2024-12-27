package com.example.freeplayandroidclient;

import android.app.IntentService;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.JobIntentService;

import java.io.IOException;

public class MediaPlayerService extends Service implements
        MediaPlayer.OnInfoListener,
        MediaPlayer.OnErrorListener,
        MediaPlayer.OnPreparedListener,
        MediaPlayer.OnCompletionListener,
        MediaPlayer.OnSeekCompleteListener,
        MediaPlayer.OnBufferingUpdateListener,
        AudioManager.OnAudioFocusChangeListener {
    private int resumePosition;
    private MediaPlayer mediaPlayer;
    private AudioManager audioManager;

    public enum ACTIONS {
        ACTION_START,
        ACTION_STOP,
        ACTION_SET_DATASOURCE
    }

    private final IBinder binder = new MediaPlayerBinder();
    public class MediaPlayerBinder extends Binder {
        public MediaPlayerService getService() {
            return MediaPlayerService.this;
        }
    }
    public class MediaPlayerHandler extends Handler {
        private final MediaPlayerService service;
        public MediaPlayerHandler(MediaPlayerService service, Looper looper) {
            super(looper);
            this.service = service;
        }
        @Override
        public void handleMessage(@NonNull Message message) {
            super.handleMessage(message);
            if (message.what == ACTIONS.ACTION_START.ordinal()) {
                Bundle bundle = message.getData();
                String dataSource = bundle.getString("dataSource");
                if (dataSource == null) service.startMediaPlayer();
                else service.setDataSourceAndStart(dataSource);
            } else if (message.what == ACTIONS.ACTION_STOP.ordinal()) {
                service.stopMediaPlayer();
            }
        }
    }

    private void createMediaPlayer() {
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnInfoListener(this);
        mediaPlayer.setOnErrorListener(this);
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setOnSeekCompleteListener(this);
        mediaPlayer.setOnBufferingUpdateListener(this);
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
    }

    public void startMediaPlayer() {
        if (mediaPlayer == null) return;
        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.start();
        }
    }
    public void resetMediaPlayer() {
        if (mediaPlayer == null) return;
        mediaPlayer.reset();
    }
    public void stopMediaPlayer() {
        if (mediaPlayer == null) return;
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
    }
    public void pauseMediaPlayer() {
        if (mediaPlayer == null) return;
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            resumePosition = mediaPlayer.getCurrentPosition();
        }
    }
    public void resumeMediaPlayer() {
        if (mediaPlayer == null) return;
        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.seekTo(resumePosition);
            mediaPlayer.start();
        }
    }
    public void seekTo(int resumePosition) {
        this.resumePosition = resumePosition;
        resumeMediaPlayer();
    }
    public int getDuration() {
        return mediaPlayer.getDuration();
    }
    public int getCurrentPosition() {
        return mediaPlayer.getCurrentPosition();
    }
    public void setDataSourceAndStart(String dataSource) {
        mediaPlayer.reset();
        try {
            mediaPlayer.setDataSource(dataSource);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        mediaPlayer.prepareAsync();
    }

    @Nullable
    @Override
    @RequiresApi(api = Build.VERSION_CODES.M)
    public IBinder onBind(Intent intent) {
        createMediaPlayer();
        return binder;
    }

    @Override
    public boolean onInfo(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }
    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.start();
    }
    @Override
    public void onCompletion(MediaPlayer mp) {
        stopMediaPlayer();
        stopSelf();
    }
    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {

    }
    @Override
    public void onAudioFocusChange(int focusState) {
        switch (focusState) {
            case AudioManager.AUDIOFOCUS_GAIN:
                if (mediaPlayer == null) createMediaPlayer();
                else if (!mediaPlayer.isPlaying()) mediaPlayer.start();
                mediaPlayer.setVolume(1.0f, 1.0f); break;
            case AudioManager.AUDIOFOCUS_LOSS:
                if (mediaPlayer.isPlaying()) mediaPlayer.stop();
                mediaPlayer.release(); mediaPlayer = null; break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                if (mediaPlayer.isPlaying()) mediaPlayer.pause(); break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                if (mediaPlayer.isPlaying()) mediaPlayer.setVolume(
                        0.1f, 0.1f); break;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean requestAudioFocus() {
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        int result = audioManager.requestAudioFocus(
                this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            return true;
        }
        return false;
    }

    private boolean removeAudioFocus() {
        return AudioManager.AUDIOFOCUS_REQUEST_GRANTED ==
                audioManager.abandonAudioFocus(this);
    }

    @Override
    public void onSeekComplete(MediaPlayer mp) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            startMediaPlayer();
            mediaPlayer.release();
        }
        removeAudioFocus();
    }
}

