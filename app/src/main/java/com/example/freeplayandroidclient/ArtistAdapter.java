package com.example.freeplayandroidclient;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;


public class ArtistAdapter extends RecyclerView.Adapter<ArtistAdapter.ViewHolder> {
    private List<Artist> artists;
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView artistName;
        private final ImageView thumbnail;
        public ViewHolder(View view) {
            super(view);
            artistName = (TextView) view.findViewById(R.id.author);
            thumbnail = (ImageView) view.findViewById(R.id.thumbnail);
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
    public static class Artist extends com.example.freeplayandroidclient.Artist {
        private Bitmap thumbnail;
        private ViewHolder viewHolder;
        private OnClickListener onClickListener;
        public Artist(String artistId, String artistName) {
            super(artistId, artistName);
        }
        public Artist(com.example.freeplayandroidclient.Artist artist) {
            super(artist);
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
    public ArtistAdapter() {
        this.artists = new ArrayList<>();
    }
    public ArtistAdapter(List<Artist> artists) {
        this.artists = artists;
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
        Artist artist = artists.get(position);
        artist.setViewHolder(viewHolder);
        String artistName = artist.getArtistName();
        if (artistName.length() > 25)
            artistName = artistName.substring(0, 25) + "...";
        viewHolder.getArtistName().setText(artistName);
        if (artist.getThumbnail() != null) {
            viewHolder.setThumbnail(artist.getThumbnail());
        }
    }
    @Override
    public int getItemCount() {
        return artists.size();
    }
    public void setItems(List<Artist> artists) {
        this.artists.addAll(artists);
        notifyDataSetChanged();
    }
    public void clearItems() {
        artists.clear();
        notifyDataSetChanged();
    }
}

