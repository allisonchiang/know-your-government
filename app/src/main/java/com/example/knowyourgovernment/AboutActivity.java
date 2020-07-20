package com.example.knowyourgovernment;

import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class AboutActivity extends AppCompatActivity {

    TextView googleAPI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        googleAPI = findViewById(R.id.googleAPI);
        googleAPI.setPaintFlags(googleAPI.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

    }

    public void openLink(View v) {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse("https://developers.google.com/civic-information/"));
        startActivity(i);
    }
}
