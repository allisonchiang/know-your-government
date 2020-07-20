package com.example.knowyourgovernment;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.util.Linkify;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

public class OfficialActivity extends AppCompatActivity {

    private ImageView photo, logo;
    private TextView location;
    private TextView office, name, party, address, phone, email, website;
    private ImageButton googlePlus, facebook, twitter, youtube;
    private TextView addressHeader, phoneHeader, emailHeader, websiteHeader;
    private Official selectedOfficial;
    private String locationString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_official);
        location = findViewById(R.id.location);

        office = findViewById(R.id.office);
        name = findViewById(R.id.name);
        party = findViewById(R.id.party);

        photo = findViewById(R.id.photo);
        logo = findViewById(R.id.logo);
        address = findViewById(R.id.address);
        phone = findViewById(R.id.phone);
        email = findViewById(R.id.email);
        website = findViewById(R.id.website);
        googlePlus = findViewById(R.id.googleplus);
        facebook = findViewById(R.id.facebook);
        twitter = findViewById(R.id.twitter);
        youtube = findViewById(R.id.youtube);

        addressHeader = findViewById(R.id.addressHeader);
        phoneHeader = findViewById(R.id.phoneHeader);
        emailHeader = findViewById(R.id.emailHeader);
        websiteHeader = findViewById(R.id.websiteHeader);

        Intent intent = getIntent();
        if (intent.hasExtra("selectedOfficial")) {
            selectedOfficial = (Official) intent.getSerializableExtra("selectedOfficial");
        }

        if (intent.hasExtra("location")) {
            locationString = ((String) intent.getSerializableExtra("location"));
        }

        location.setText(locationString);
        String partyString = selectedOfficial.getParty();

        office.setText(selectedOfficial.getOffice());
        name.setText(selectedOfficial.getName());
        party.setText("(" + partyString + ")");

        if (doNetCheck()) {
            if (selectedOfficial.getPhotoURL() != null) {
                loadRemoteImage(selectedOfficial.getPhotoURL());
            } else {
                photo.setImageResource(R.drawable.missing);
            }
        } else {
            photo.setImageResource(R.drawable.brokenimage);
        }

        if (selectedOfficial.getAddress() == null) {
            address.setVisibility(View.GONE);
            addressHeader.setVisibility(View.GONE);
        } else {
            address.setText(selectedOfficial.getAddress());
        }

        if (selectedOfficial.getPhone() == null) {
            phone.setVisibility(View.GONE);
            phoneHeader.setVisibility(View.GONE);
        } else {
            phone.setText(selectedOfficial.getPhone());
        }

        if (selectedOfficial.getEmail() == null) {
            email.setVisibility(View.GONE);
            emailHeader.setVisibility(View.GONE);
        } else {
            email.setText(selectedOfficial.getEmail());
        }

        if (selectedOfficial.getUrl() == null) {
            website.setVisibility(View.GONE);
            websiteHeader.setVisibility(View.GONE);
        } else {
            website.setText(selectedOfficial.getUrl());
        }

        if (selectedOfficial.getGooglePlus() == null) {
            googlePlus.setVisibility(View.GONE);
        }

        if (selectedOfficial.getFacebook() == null) {
            facebook.setVisibility(View.GONE);
        }

        if (selectedOfficial.getTwitter() == null) {
            twitter.setVisibility(View.GONE);
        }

        if (selectedOfficial.getYoutube() == null) {
            youtube.setVisibility(View.GONE);
        }

        if (partyString.contains("Demo")) {
            getWindow().getDecorView().setBackgroundColor(Color.BLUE);
            logo.setImageResource(R.drawable.dem_logo);
        } else if (partyString.contains("Repub")) {
            getWindow().getDecorView().setBackgroundColor(Color.RED);
            logo.setImageResource(R.drawable.rep_logo);
        } else {
            getWindow().getDecorView().setBackgroundColor(Color.BLACK);
            logo.setVisibility(View.GONE);
        }


        Linkify.addLinks(address, Linkify.ALL);
        Linkify.addLinks(phone, Linkify.ALL);
        Linkify.addLinks(email, Linkify.ALL);
        Linkify.addLinks(website, Linkify.ALL);

        address.setLinkTextColor(Color.WHITE);
        phone.setLinkTextColor(Color.WHITE);
        email.setLinkTextColor(Color.WHITE);
        website.setLinkTextColor(Color.WHITE);
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

    // click on photo opens new activity
    public void imageClick(View v) {
        if (selectedOfficial.getPhotoURL()!= null) {
            Intent intent = new Intent(this, PhotoActivity.class);
            intent.putExtra("location", locationString);
            intent.putExtra("office", selectedOfficial.getOffice());
            intent.putExtra("name", selectedOfficial.getName());
            intent.putExtra("party", selectedOfficial.getParty());
            intent.putExtra("photoUrl", selectedOfficial.getPhotoURL());
            startActivity(intent);
        }
    }

    // click on Dem/Rep logo opens political party's website
    public void logoClick(View v) {
        Intent i = new Intent(Intent.ACTION_VIEW);
        if (selectedOfficial.getParty().contains("Dem")) {
            i.setData(Uri.parse("https://democrats.org/"));
        } else if (selectedOfficial.getParty().contains("Rep")) {
            i.setData(Uri.parse("https://www.gop.com/"));
        }
        startActivity(i);
    }

    public void clickEmail(View v) {
        String[] addresses = new String[]
                {"christopher.hield@gmail.com", "chield@iit.edu"};

        Intent intent = new Intent(Intent.ACTION_SENDTO,
                Uri.parse("mailto:"));

        intent.putExtra(Intent.EXTRA_EMAIL, addresses);
        intent.putExtra(Intent.EXTRA_SUBJECT,
                "This comes from EXTRA_SUBJECT");
        intent.putExtra(Intent.EXTRA_TEXT,
                "Email text body from EXTRA_TEXT...");

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            makeErrorAlert("No Application found that handles SENDTO (mailto) intents");
        }
    }

    private void makeErrorAlert(String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage(msg);
        builder.setTitle("No App Found");

        AlertDialog dialog = builder.create();
        dialog.show();
    }


    public void googlePlusClicked(View v) {
        String name = selectedOfficial.getGooglePlus();
        Intent intent = null;
        try {
            intent = new Intent(Intent.ACTION_VIEW);
            intent.setClassName("com.google.android.apps.plus",
                    "com.google.android.apps.plus.phone.UrlGatewayActivity");
            intent.putExtra("customAppUri", name);
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://plus.google.com/" + name)));
        }
    }

    public void facebookClicked(View v) {
        String FACEBOOK_URL = "https://www.facebook.com/" + selectedOfficial.getFacebook();
        String urlToUse;
        PackageManager packageManager = getPackageManager();
        try {
            int versionCode = packageManager.getPackageInfo("com.facebook.katana", 0).versionCode;
            if (versionCode >= 3002850) { //newer versions of fb app
                urlToUse = "fb://facewebmodal/f?href=" + FACEBOOK_URL;
            } else { //older versions of fb app
                urlToUse = "fb://page/" + selectedOfficial.getFacebook();
            }
        } catch (PackageManager.NameNotFoundException e) {
            urlToUse = FACEBOOK_URL; //normal web url
        }
        Intent facebookIntent = new Intent(Intent.ACTION_VIEW);
        facebookIntent.setData(Uri.parse(urlToUse));
        startActivity(facebookIntent);
    }

    public void twitterClicked(View v) {
        Intent intent = null;
        String name = selectedOfficial.getTwitter();
        try {
            // get the Twitter app if possible
            getPackageManager().getPackageInfo("com.twitter.android", 0);
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("twitter://user?screen_name=" + name));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        } catch (Exception e) {
            // no Twitter app, revert to browser
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/" + name));
        }
        startActivity(intent);
    }

    public void youTubeClicked(View v) {
        String name = selectedOfficial.getYoutube();
        Intent intent = null;
        try {
            intent = new Intent(Intent.ACTION_VIEW);
            intent.setPackage("com.google.android.youtube");
            intent.setData(Uri.parse("https://www.youtube.com/" + name));
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://www.youtube.com/" + name)));
        }
    }
}