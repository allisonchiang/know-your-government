package com.example.knowyourgovernment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, Serializable {

    private static final String TAG = "MainActivity";
    private RecyclerView recyclerView;
    private OfficialAdapter officialAdapter;
    private final List<Official> officialList = new ArrayList<>();
    private TextView locationTV;
    private static String locationString;   //set to static so belongs to class, won't be affected by onCreate()

    private static int MY_LOCATION_REQUEST_CODE_ID = 329;
    private LocationManager locationManager;
    private Criteria criteria;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        locationTV = findViewById(R.id.location);
        recyclerView = findViewById(R.id.recycler);

        officialAdapter = new OfficialAdapter(officialList, this);

        recyclerView.setAdapter(officialAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        criteria = new Criteria();
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        //criteria.setPowerRequirement(Criteria.POWER_HIGH);
        criteria.setAccuracy(Criteria.ACCURACY_MEDIUM);
        //criteria.setAccuracy(Criteria.ACCURACY_FINE);

        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setSpeedRequired(false);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION
                    },
                    MY_LOCATION_REQUEST_CODE_ID);
        } else {
            // if app is starting for first time
            if (locationString == null) {
                setLocation();
            }

            Log.d(TAG, "onCreate: " + locationString);
            if (doNetCheck()) {
                new Downloader(this).execute(locationString);
            } else {
                displayErrorDialog();
//                Toast.makeText(this, "Cannot connect to internet", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull
            String[] permissions, @NonNull
                    int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == MY_LOCATION_REQUEST_CODE_ID) {
            if (permissions[0].equals(Manifest.permission.ACCESS_FINE_LOCATION) &&
                    grantResults[0] == PERMISSION_GRANTED) {
                setLocation();
                return;
            }
        }
//        ((TextView) findViewById(R.id.locText)).setText(R.string.no_perm);
    }

    @SuppressLint("MissingPermission")
    private void setLocation() {
        String bestProvider = locationManager.getBestProvider(criteria, true);

        Location currentLocation = locationManager.getLastKnownLocation(bestProvider);

        if (currentLocation != null) {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            double lat = currentLocation.getLatitude();
            double lon = currentLocation.getLongitude();

            try {
                List<Address> addresses;
                addresses = geocoder.getFromLocation(lat, lon, 1);

                if (addresses.size() > 0) {
                    Address ad = addresses.get(0);

                    locationString = String.format("%s %s %s",
//                        (ad.getSubThoroughfare() == null ? "" : ad.getSubThoroughfare()),
//                        (ad.getThoroughfare() == null ? "" : ad.getThoroughfare()),
                            (ad.getLocality() == null ? "" : ad.getLocality()),
                            (ad.getAdminArea() == null ? "" : ad.getAdminArea()),
                            (ad.getPostalCode() == null ? "" : ad.getPostalCode()));
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

        } else {
            displayErrorDialog();
//            Toast.makeText(this, "Cannot find current location. Best provider:" + bestProvider, Toast.LENGTH_LONG).show();
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

    public void displayErrorDialog() {
        locationTV.setText("No Data For Location");
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("No Network Connection");
        builder.setMessage("Data cannot be accessed/loaded without an internet connection.");
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onClick(View v) {
        int pos = recyclerView.getChildLayoutPosition(v);
        Official selectedOfficial = officialList.get(pos);
//        Toast.makeText(this, "Stock clicked", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(this, OfficialActivity.class);
        if (selectedOfficial != null) {
            intent.putExtra("selectedOfficial", selectedOfficial);
            intent.putExtra("location", locationString);
        }
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuAbout:
                Intent intent = new Intent(this, AboutActivity.class);
                startActivity(intent);
                return true;
            case R.id.menuSearch:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Enter a City, State or a Zip Code:");
                final EditText input = new EditText(this);
                input.setGravity(Gravity.CENTER_HORIZONTAL);
//                input.setInputType(InputType.TYPE_CLASS_TEXT);
                input.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
                builder.setView(input);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (doNetCheck()) {
                            String location;
                            // Code goes here
                            location = input.getText().toString();
//                            Toast.makeText(MainActivity.this, "You entered " + location, Toast.LENGTH_LONG).show();
                            new Downloader(MainActivity.this).execute(location);
                        } else {
                            displayErrorDialog();
                        }
                    }
                });
                builder.setNegativeButton("CANCEL",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                AlertDialog dialog = builder.create();
                dialog.show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void updateData(List<Official> officialListReturned, String newLocation) {
        locationString = newLocation;
        locationTV.setText(newLocation);


        officialList.clear();
        officialList.addAll(officialListReturned);
        officialAdapter.notifyDataSetChanged();
    }
}