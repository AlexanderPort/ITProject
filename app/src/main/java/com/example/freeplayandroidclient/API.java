package com.example.freeplayandroidclient;

import android.content.Context;
import android.graphics.Bitmap;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class API {
    private final RequestQueue requestQueue;
    //private static final String API = "http://127.0.0.1:8000";
    //private static final String API = "http://10.0.2.2:8000";
    //private static final String API = "https://quick-moose-52.loca.lt";
    //private static final String API = "https://honest-crab-88.loca.lt";
    private static final String API = "https://warm-crag-36597.herokuapp.com";
    public API(Context context) {
        requestQueue = Volley.newRequestQueue(context);
        requestQueue.start();
    }
    public void getTrackData(String id,
            Response.Listener<byte[]> listener,
            Response.ErrorListener errorListener) {
        ByteArrayRequest byteArrayRequest = new ByteArrayRequest(
                Request.Method.GET, getTrackDataURL(id),
                listener, errorListener);
        requestQueue.add(byteArrayRequest);
    }
    public void getAllTracks(String trackName, String albumName, String artistName,
                             Response.Listener<JSONArray> listener,
                             Response.ErrorListener errorListener) {
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                getAllTracksURL(trackName, albumName, artistName),
                listener, errorListener);
        requestQueue.add(jsonArrayRequest);
    }
    public void getRandomTracks(int numTracks,
                                Response.Listener<JSONArray> listener,
                                Response.ErrorListener errorListener) {
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                String.format("%s/api/tracks/random/%s", API, numTracks),
                listener, errorListener);
        requestQueue.add(jsonArrayRequest);
    }
    public void getTrackImage(String id, int maxWidth, int maxHeight,
                              Response.Listener<Bitmap> listener) {
        ImageRequest imageRequest = new ImageRequest(getTrackImageURL(id),
                listener, maxWidth, maxHeight, null, null);
        requestQueue.add(imageRequest);
    }
    public String getTrackImageURL(String id) {
        return String.format("%s/api/tracks/images/%s", API, id);
    }
    public void getAlbumImage(String id, int maxWidth, int maxHeight,
                              Response.Listener<Bitmap> listener) {
        ImageRequest imageRequest = new ImageRequest(getAlbumImageURL(id),
                listener, maxWidth, maxHeight, null, null);
        requestQueue.add(imageRequest);
    }
    public String getAlbumImageURL(String id) {
        return String.format("%s/api/albums/images/%s", API, id);
    }
    public void getArtistImage(String id, int maxWidth, int maxHeight,
                              Response.Listener<Bitmap> listener) {
        ImageRequest imageRequest = new ImageRequest(getArtistImageURL(id),
                listener, maxWidth, maxHeight, null, null);
        requestQueue.add(imageRequest);
    }
    public String getArtistImageURL(String id) {
        return String.format("%s/api/artists/images/%s", API, id);
    }
    public String getTrackDataURL(String id) {
        return String.format("%s/api/tracks/data/%s", API, id);
    }
    public String getAllTracksURL(String trackName, String albumName, String artistName) {
        String url = String.format("%s/api/tracks/meta?", API);
        if (trackName != null) url += "trackName=" + trackName;
        if (albumName != null) url += "albumName=" + albumName;
        if (artistName != null) url += "artistName=" + artistName;
        return url;
    }
    public void searchTrackByName(String key,
                                   Response.Listener<JSONArray> listener,
                                   Response.ErrorListener errorListener) {
        String url = String.format("%s/api/tracks/meta?trackName=%s", API, key);
        JsonArrayRequest jsonObjectRequest = new JsonArrayRequest(
                url, listener, errorListener);
        requestQueue.add(jsonObjectRequest);
    }
    public void searchAlbumByName(String key,
                                  Response.Listener<JSONArray> listener,
                                  Response.ErrorListener errorListener) {
        String url = String.format("%s/api/search/albums/%s", API, key);
        JsonArrayRequest jsonObjectRequest = new JsonArrayRequest(
                url, listener, errorListener);
        requestQueue.add(jsonObjectRequest);
    }
    public void searchArtistByName(String key,
                                   Response.Listener<JSONArray> listener,
                                   Response.ErrorListener errorListener) {
        String url = String.format("%s/api/search/artists/%s", API, key);
        JsonArrayRequest jsonObjectRequest = new JsonArrayRequest(
                url, listener, errorListener);
        requestQueue.add(jsonObjectRequest);
    }

    public void getUserById(String userId,
                            Response.Listener<JSONObject> listener,
                            Response.ErrorListener errorListener) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                String.format("%s/api/users/%s", API, userId),
                null, listener, errorListener);
        requestQueue.add(jsonObjectRequest);
    }

    public void postUser(User user, OnResponseListener<String> listener) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                String.format("%s/api/users", API),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        listener.onResponse(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                })
                {
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String,String> params = new HashMap<String, String>();
                        params.put("userId", user.getUserId());
                        params.put("userName", user.getUserName());
                        params.put("userEmail", user.getUserEmail());
                        params.put("userPassword", user.getUserPassword());
                        return params;
                    }
                    @Override
                    protected Response<String> parseNetworkResponse(NetworkResponse response) {
                        listener.onNetworkResponse(response);
                        return super.parseNetworkResponse(response);
                    }
                };
        requestQueue.add(stringRequest);
    }

    public void postFollower(String userId, String artistId) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                String.format("%s/api/followers?userId=%s&artistId=%s", API, userId, artistId),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                });
        requestQueue.add(stringRequest);
    }

    public interface OnResponseListener<T> {
        public void onResponse(T response);
        public void onNetworkResponse(NetworkResponse response);
    }
}

