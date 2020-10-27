package com.cmput301f20t14.bookbox.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.cmput301f20t14.bookbox.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

/**
 * This is the activity where the user can view their
 * notifications that they receive. In this activity, the notifications
 * show that another user would like to borrow your book, and is notified
 * if their own request can been accepted or declined
 * @author Alex Mazzuca, Carter Sabadash
 * @version 2020.10.25
 * @see HomeActivity
 * @see ListsActivity
 * @see ProfileActivity
 */
public class NotificationsActivity extends AppCompatActivity {
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        // get the username from whichever activity we came from
        // this is necessary to access firebase
        username = getIntent().getExtras().getString("USERNAME");

        bottomNavigationView();

    }


    /**
     * Implementation of the bottom navigation bar for switching to different
     * activity views, such as home, profile, notifications and lists
     * References: https://www.youtube.com/watch?v=JjfSjMs0ImQ&feature=youtu.be
     * @author Alex Mazzuca, Carter Sabadash
     * @version 2020.10.25
     */
    private void bottomNavigationView(){
        //Home Navigation bar implementation
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav_bar);
        bottomNavigationView.setSelectedItemId(R.id.notification_bottom_nav);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()){
                    case R.id.lists_bottom_nav:
                        startActivity(new Intent(getApplicationContext(), ListsActivity.class )
                                .putExtra("USERNAME", username));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.home_bottom_nav:
                        startActivity(new Intent(getApplicationContext(), HomeActivity.class)
                                .putExtra("USERNAME", username));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.notification_bottom_nav:
                        return true;
                    case R.id.profile_bottom_nav:
                        startActivity(new Intent(getApplicationContext(), ProfileActivity.class)
                                .putExtra("USERNAME", username));
                        overridePendingTransition(0,0);
                        return true;
                }
                return false;
            }
        });
    }

}