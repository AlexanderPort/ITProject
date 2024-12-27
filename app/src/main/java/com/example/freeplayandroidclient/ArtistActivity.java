package com.example.freeplayandroidclient;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;

public class ArtistActivity extends Base {
    private Artist currentArtist;
    private TextView artistName;
    private Button followButton;
    private ImageView artistImage;
    private TrackRecyclerView trackRecyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist);
        followButton = (Button) findViewById(R.id.follow);
        artistName = (TextView) findViewById(R.id.artistName);
        artistImage = (ImageView) findViewById(R.id.artistImage);
        trackRecyclerView = (TrackRecyclerView) findViewById(R.id.trackRecyclerView);
        onPrepared(mediaPlayer);

        followButton.setOnClickListener(this);

        Intent intent = getIntent();
        currentArtist = new Artist(
                intent.getStringExtra("artistId"),
                intent.getStringExtra("artistName"));

        artistName.setText(currentArtist.getArtistName());

        Response.Listener<Bitmap> listener = new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap response) {
                artistImage.setImageBitmap(response);
            }
        };
        api.getArtistImage(currentArtist.getArtistId(), 1000, 1000, listener);

        Response.Listener<JSONArray> jsonObjectListener = new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    trackRecyclerView.addTracks(response);
                } catch (JSONException exception) {
                    exception.printStackTrace();
                }
            }
        };
        Response.ErrorListener errorListener = new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error){
                error.printStackTrace();
            }
        };
        api.getAllTracks(null, null,
                currentArtist.getArtistName(), jsonObjectListener, errorListener);
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        if (view.getId() == followButton.getId()) {
            if (currentUser != null) {
                api.postFollower(currentUser.getUserId(), currentArtist.getArtistId());
            }
        }
    }
}