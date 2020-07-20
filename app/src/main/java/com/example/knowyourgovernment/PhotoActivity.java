package com.example.knowyourgovernment;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class PhotoActivity extends AppCompatActivity {

    private ImageView photo, logo;
    private TextView location;
    private TextView office, name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);

        location = findViewById(R.id.location);
        office = findViewById(R.id.office);
        name = findViewById(R.id.name);
        photo = findViewById(R.id.photo);
        logo = findViewById(R.id.logo);

        Intent intent = getIntent();
        if (intent.hasExtra("location")) {
            location.setText((String) intent.getSerializableExtra("location"));
        }

        if (intent.hasExtra("office")) {
            office.setText((String) intent.getSerializableExtra("office"));
        }

        if (intent.hasExtra("name")) {
            name.setText((String) intent.getSerializableExtra("name"));
        }

        if (intent.hasExtra("party")) {
            String party = (String) intent.getSerializableExtra("party");

            if (party.contains("Demo")) {
                getWindow().getDecorView().setBackgroundColor(Color.BLUE);
                logo.setImageResource(R.drawable.dem_logo);
            } else if (party.contains("Repub")) {
                getWindow().getDecorView().setBackgroundColor(Color.RED);
                logo.setImageResource(R.drawable.rep_logo);
            } else {
                getWindow().getDecorView().setBackgroundColor(Color.BLACK);
                logo.setVisibility(View.INVISIBLE);
            }
        }

        if (intent.hasExtra("photoUrl")) {
            String photoUrl = (String) intent.getSerializableExtra("photoUrl");
            if (doNetCheck()) {
                if (photoUrl != null) {
                    loadRemoteImage(photoUrl);
                } else {
                    photo.setImageResource(R.drawable.missing);
                }
            } else {
                photo.setImageResource(R.drawable.brokenimage);
            }
        }
    }

    private boolean doNetCheck() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) {
//            Toast.makeText(this, "Cannot access ConnectivityManager", Toast.LENGTH_SHORT).show();
            return false;
        }
        NetworkInfo netInfo = cm.getActiveNetworkInfo();

        if (netInfo != null && netInfo.isConnected()) {
//            Toast.makeText(this, "Connected to network", Toast.LENGTH_SHORT).show();
            return true;
        } else {
//            Toast.makeText(this, "Not connected to network", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    private void loadRemoteImage(final String imageURL) {
        // Needs gradle  implementation 'com.squareup.picasso:picasso:2.71828'

        Picasso picasso = new Picasso.Builder(this).build();
        picasso.setLoggingEnabled(true);
        picasso.load(imageURL)
                .error(R.drawable.brokenimage)
                .placeholder(R.drawable.placeholder)
                .into(photo);
    }
}
