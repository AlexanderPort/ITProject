package com.example.freeplayandroidclient;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.material.chip.Chip;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SearchActivity extends Base {
    enum SEARCH_MODE {
        TRACKS,
        ALBUMS,
        ARTISTS
    }
    private Chip tracksChip;
    private Chip albumsChip;
    private Chip artistsChip;
    private EditText searchView;
    private SEARCH_MODE search_mode;
    private TrackRecyclerView trackRecyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        search_mode = SEARCH_MODE.TRACKS;

        tracksChip = (Chip) findViewById(R.id.trackChip);
        albumsChip = (Chip) findViewById(R.id.albumsChip);
        artistsChip = (Chip) findViewById(R.id.artistsChip);
        searchView = (EditText) findViewById(R.id.search);
        trackRecyclerView = findViewById(R.id.trackRecyclerView);

        tracksChip.setAlpha(0.9f);

        Response.Listener<JSONArray> jsonObjectListener = new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    trackRecyclerView.clear();
                    trackRecyclerView.addTracks(response);
                } catch (JSONException exception) {
                    exception.printStackTrace();
                }
            }
        };
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        };

        searchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 0) trackRecyclerView.clear();
                if (search_mode == SEARCH_MODE.TRACKS) {
                    api.getAllTracks(s.toString(), null, null, jsonObjectListener, errorListener);
                } else if (search_mode == SEARCH_MODE.ALBUMS) {
                    api.getAllTracks(null, s.toString(), null, jsonObjectListener, errorListener);
                } else if (search_mode == SEARCH_MODE.ARTISTS) {
                    api.getAllTracks(null, null, s.toString(), jsonObjectListener, errorListener);
                }
            }
        });
        tracksChip.setOnClickListener(this);
        albumsChip.setOnClickListener(this);
        artistsChip.setOnClickListener(this);

        onPrepared(mediaPlayer);
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        trackRecyclerView.clear();
        if (view.getId() == tracksChip.getId()) {
            tracksChip.setAlpha(0.9f);
            albumsChip.setAlpha(0.5f);
            artistsChip.setAlpha(0.5f);
            search_mode = SEARCH_MODE.TRACKS;
        } else if (view.getId() == albumsChip.getId()) {
            tracksChip.setAlpha(0.5f);
            albumsChip.setAlpha(0.9f);
            artistsChip.setAlpha(0.5f);
            search_mode = SEARCH_MODE.ALBUMS;
        } else if (view.getId() == artistsChip.getId()) {
            tracksChip.setAlpha(0.5f);
            albumsChip.setAlpha(0.5f);
            artistsChip.setAlpha(0.9f);
            search_mode = SEARCH_MODE.ARTISTS;
        }
    }
}
