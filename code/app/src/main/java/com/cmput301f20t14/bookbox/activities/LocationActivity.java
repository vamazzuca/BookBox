package com.cmput301f20t14.bookbox.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
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
 * @author  Olivier Vadiavaloo
 * @version 2020.11.21
 */

public class LocationActivity extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap map;
    private Button cancelButton;
    private Button confirmButton;
    private LatLng mlatLng;
    private String username;
    private Request request;
    private Geocoder geoCoder;
    private Boolean isReceiveReturn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        // Retrieve username
        username = getIntent().getStringExtra(User.USERNAME);

        // Retrieve request object
        request = (Request) getIntent().getExtras().getSerializable("REQUEST");

        isReceiveReturn = (boolean) getIntent().getBooleanExtra("IS_RECEIVE_RETURN", false);

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

        // If the user is the borrower in the request,
        // he or she can only view the location
        if (request.getBorrower().equals(username) || isReceiveReturn) {
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

        if (request.getLatLng() != null && !request.getLatLng().equals("")) {
            Log.d("Here", request.getLatLng());
            setMarkerOnMap(Request.parseLatLngString(request.getLatLng()));
        }

        //Toast.makeText(LocationActivity.this, String.valueOf(request.getLatLng().isEmpty()), Toast.LENGTH_SHORT).show();
        //Toast.makeText(LocationActivity.this, isReceiveReturn.toString(), Toast.LENGTH_SHORT).show();

        Log.d("Here", request.getOwner());
        Log.d("Here", String.valueOf(isReceiveReturn));
        Log.d("Here", username);
        if (request.getOwner().equals(username) && !isReceiveReturn) {
            Log.d("Here", "Here");
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