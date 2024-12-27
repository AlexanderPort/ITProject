package com.example.freeplayandroidclient;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.UUID;

public class ProfileActivity extends Base implements RegistrationDialog.RegistrationDialogListener {
    private TextView userName;
    private RegistrationDialog dialog;
    private LinearLayout artistsLayout;
    private TextView numArtists, numPlaylists;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        userName = (TextView) findViewById(R.id.userName);
        artistsLayout = (LinearLayout) findViewById(R.id.artists);
        numArtists = (TextView) findViewById(R.id.numArtists);
        numPlaylists = (TextView) findViewById(R.id.numPlaylists);

        numArtists.setText("Подписки 0");
        numPlaylists.setText("Плейлисты 0");

        if (currentUser != null) {
            api.getUserById(currentUser.getUserId(), new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        currentUser = User.fromJSON(response);
                        numArtists.setText("Подписки " + currentUser.getArtists().size());
                        numPlaylists.setText("Плейлисты " + currentUser.getPlaylists().size());
                        for (Artist artist : currentUser.getArtists()) {
                            View view = getLayoutInflater().inflate(R.layout.artist_item, null);
                            view.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent intent = new Intent(ProfileActivity.this, ArtistActivity.class);
                                    intent.putExtra("artistId", artist.getArtistId());
                                    intent.putExtra("artistName", artist.getArtistName());
                                    startActivity(intent);
                                }
                            });
                            ((TextView) view.findViewById(R.id.artistName)).setText(artist.getArtistName());
                            api.getArtistImage(artist.getArtistId(), 300, 300,
                                    new Response.Listener<Bitmap>() {
                                @Override
                                public void onResponse(Bitmap response) {
                                    ((ImageView) view.findViewById(R.id.artistImage)).setImageBitmap(response);
                                    artistsLayout.requestLayout();
                                }
                            });
                            artistsLayout.addView(view);
                            artistsLayout.requestLayout();
                        }
                    } catch (JSONException exception) {
                        exception.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });
        }

        onPrepared(mediaPlayer);
        if (currentUser == null) showRegistrationDialog();
        else { userName.setText(currentUser.getUserName()); }
    }

    public void showRegistrationDialog() {
        dialog = new RegistrationDialog();
        dialog.show(getSupportFragmentManager(), "RegistrationDialog");

    }

    @Override
    public void onDialogPositiveClick(RegistrationDialog dialog) {
        currentUser = new User();

        currentUser.setUserId(UUID.randomUUID().toString());
        currentUser.setUserName(dialog.getUserName());
        currentUser.setUserEmail(dialog.getUserEmail());
        currentUser.setUserPassword(dialog.getUserPassword());
        currentUser.setUserStatus(false);

        databaseHelper.insertUser(currentUser);

        api.postUser(currentUser, new API.OnResponseListener<String>() {
            @Override
            public void onResponse(String response) {

            }

            @Override
            public void onNetworkResponse(NetworkResponse response) {
                if (response.statusCode == 200) {
                    currentUser.setUserStatus(true);
                    databaseHelper.updateUser(currentUser);
                    userName.setText(currentUser.getUserName());
                }
            }
        });

        dialog.dismiss();
    }

    @Override
    public void onDialogNegativeClick(RegistrationDialog dialog) {
        dialog.dismiss();
    }
}