package com.cmput301f20t14.bookbox.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.cmput301f20t14.bookbox.R;
import com.cmput301f20t14.bookbox.entities.Request;
import com.cmput301f20t14.bookbox.entities.User;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;
import java.util.Locale;

/**
 * This activity allows the user to set/view the location
 * where the book will be received or borrowed
 */

public class LocationActivity extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap map;
    private Button cancelButton;
    private Button confirmButton;
    private LatLng mlatLng;
    private String username;
    private Request request;
    private Geocoder geoCoder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        // Retrieve username
        username = getIntent().getStringExtra(User.USERNAME);

        // Retrieve request object
        request = (Request) getIntent().getExtras().getSerializable("REQUEST");

        // Get Buttons
        cancelButton = (Button) findViewById(R.id.location_cancel);
        confirmButton = (Button) findViewById(R.id.location_confirm);
        confirmButton.setEnabled(false);

        // Get the SupportMapFragment
        SupportMapFragment supportMapFragment =
                (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        supportMapFragment.getMapAsync(this);

        // Set up bottom navigation bar
        bottomNavigationView();

        if (!request.getOwner().equals(username)) {
            TextView header = (TextView) findViewById(R.id.location_textview);
            header.setText(R.string.view_location);
            cancelButton.setVisibility(View.GONE);
            confirmButton.setVisibility(View.GONE);

        } else {
            cancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setResult(CommonStatusCodes.CANCELED);
                    finish();
                }
            });

            confirmButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    String latLngString = String.valueOf(mlatLng.latitude) + "," + String.valueOf(mlatLng.longitude);
                    intent.putExtra(Request.LAT_LNG, latLngString);
                    setResult(CommonStatusCodes.SUCCESS, intent);
                    finish();
                }
            });
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        if (request.getLatLng() != null) {
            setMarkerOnMap(Request.parseLatLngString(request.getLatLng()));
        }

        if (request.getOwner().equals(username)) {
            map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {
                    setMarkerOnMap(latLng);
                    confirmButton.setEnabled(true);
                }
            });
        }
    }

    /**
     * This method adds the marker of the passed in latLng object
     * onto the map and zooms in
     * This is called when there is already a marker set previously by
     * the book owner in a request and when the book owner is choosing
     * a location.
     * @param latLng LatLng object where marker is placed
     */
    public void setMarkerOnMap(LatLng latLng) {
        MarkerOptions markerOptions = new MarkerOptions();

        String addressInfo = getAddressInfo(latLng);
        if (!addressInfo.isEmpty()) {
            markerOptions.title(addressInfo);
        }

        markerOptions.position(latLng);
        map.clear();
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
        map.addMarker(markerOptions);
        mlatLng = latLng;
    }

    /**
     * Returns the string representation of the address information at the marker
     * @param  latLng      LatLng object for where marker is placed by user
     * @return addressInfo String representing the postalCode at the marker
     */
    public String getAddressInfo(LatLng latLng) {
        geoCoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geoCoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            return addresses.get(0).getCountryName() + ", " +
                    addresses.get(0).getLocality() + ", " +
                    addresses.get(0).getPostalCode();
        } catch (Exception e) {
            Toast
                    .makeText(
                            LocationActivity.this,
                            "Couldn't display postal code",
                            Toast.LENGTH_SHORT
                            )
                    .show();
            return "";
        }
    }

    /**
     * Implementation of the bottom navigation bar for switching to different
     * activity views, such as home, profile, notifications and lists
     * References: https://www.youtube.com/watch?v=JjfSjMs0ImQ&feature=youtu.be
     * @author Alex Mazzuca
     * @author Carter Sabadash
     * @version 2020.10.25
     */
    private void bottomNavigationView(){
        //Home Navigation bar implementation
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav_bar);
        bottomNavigationView.setSelectedItemId(R.id.home_bottom_nav);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()){
                    case R.id.lists_bottom_nav:
                        startActivity(new Intent(getApplicationContext(), ListsActivity.class)
                                .putExtra(User.USERNAME, username));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.home_bottom_nav:
                        startActivity(new Intent(getApplicationContext(), HomeActivity.class)
                                .putExtra(User.USERNAME, username));
                        return true;
                    case R.id.notification_bottom_nav:
                        startActivity(new Intent(getApplicationContext(), NotificationsActivity.class)
                                .putExtra(User.USERNAME, username));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.profile_bottom_nav:
                        startActivity(new Intent(getApplicationContext(), ProfileActivity.class)
                                .putExtra(User.USERNAME, username));
                        overridePendingTransition(0,0);
                        return true;
                }
                return false;
            }
        });
    }
}