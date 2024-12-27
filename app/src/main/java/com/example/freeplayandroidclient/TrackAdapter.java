package com.example.freeplayandroidclient;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;


public class TrackAdapter extends RecyclerView.Adapter<TrackAdapter.ViewHolder> {
    private List<Track> tracks;
    private int nextTrackIndex = 0;
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView trackName;
        private final TextView artistName;
        private final ImageButton play;
        private final ImageView thumbnail;
        private final ImageButton actions;
        public ViewHolder(View view) {
            super(view);
            play = (ImageButton) view.findViewById(R.id.play);
            trackName = (TextView) view.findViewById(R.id.title);
            artistName = (TextView) view.findViewById(R.id.author);
            actions = (ImageButton) view.findViewById(R.id.actions);
            thumbnail = (ImageView) view.findViewById(R.id.thumbnail);
        }
        public ImageButton getPlay() {
            return play;
        }
        public ImageButton getActions() {
            return actions;
        }
        public TextView getTrackName() {
            return trackName;
        }
        public ImageView getThumbnail() {
            return thumbnail;
        }
        public TextView getArtistName() {
            return artistName;
        }
        public void setThumbnail(Bitmap bitmap) {
            thumbnail.setImageBitmap(bitmap);
        }
    }
    public static class Track extends com.example.freeplayandroidclient.Track {
        private Bitmap thumbnail;
        private ViewHolder viewHolder;
        private OnClickListener onClickListener;
        public Track(String trackId, String trackName,
                     String trackDataFormat, String trackImageFormat,
                     List<Album> albums, List<Artist> artists) {
            super(trackId, trackName, trackDataFormat,
                    trackImageFormat, albums, artists);
        }
        public Track(com.example.freeplayandroidclient.Track track) {
            super(track);
        }
        public Bitmap getThumbnail() {
            return thumbnail;
        }
        public ViewHolder getViewHolder() {
            return viewHolder;
        }
        public OnClickListener getOnClickListener() {
            return onClickListener;
        }
        public void setViewHolder(ViewHolder viewHolder) {
            this.viewHolder = viewHolder;
        }
        public void setOnClickListener(OnClickListener onClickListener) {
            this.onClickListener = onClickListener;
        }
        public void setThumbnail(Bitmap thumbnail) {
            this.thumbnail = thumbnail;
            if (viewHolder != null) {
                viewHolder.setThumbnail(thumbnail);
            }
        }
        public interface OnClickListener {
            void onClick(View view);
        }
    }
    public TrackAdapter() {
        this.tracks = new ArrayList<>();
    }
    public TrackAdapter(List<Track> tracks) {
        this.tracks = tracks;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.track_item, viewGroup, false);
        return new ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        Track track = tracks.get(position);
        track.setViewHolder(viewHolder);
        String trackName = track.getTrackName();
        String artistName = track.getArtists().get(0).getArtistName();
        if (trackName.length() > 25)
            trackName = trackName.substring(0, 25) + "...";
        if (artistName.length() > 25)
            artistName = artistName.substring(0, 25) + "...";
        viewHolder.getTrackName().setText(trackName);
        viewHolder.getArtistName().setText(artistName);
        if (track.getThumbnail() != null) {
            viewHolder.setThumbnail(track.getThumbnail());
        }
        viewHolder.getPlay().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Track.OnClickListener onClickListener = track.getOnClickListener();
                if (onClickListener != null) { onClickListener.onClick(view); }
                nextTrackIndex = (position + 1) % tracks.size();
            }
        });
        viewHolder.getActions().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Track.OnClickListener onClickListener = track.getOnClickListener();
                if (onClickListener != null) { onClickListener.onClick(view); }
            }
        });
    }
    @Override
    public int getItemCount() {
        return tracks.size();
    }
    public void setItems(List<Track> tracks) {
        this.tracks.addAll(tracks);
        notifyDataSetChanged();
    }
    public void clearItems() {
        tracks.clear();
        notifyDataSetChanged();
    }
    public int getNextTrackIndex() {
        return nextTrackIndex;
    }
    public String getNextTrackId() {
        return tracks.get(nextTrackIndex).getTrackId();
    }
    public String getNextTrackName() {
        return tracks.get(nextTrackIndex).getTrackName();
    }
    public Track getNextTrack() {
        return tracks.get(nextTrackIndex);
    }
    public void increaseNextTrackIndex() {
        nextTrackIndex = (nextTrackIndex + 1) % tracks.size();
    }
}

