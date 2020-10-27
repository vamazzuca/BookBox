package com.cmput301f20t14.bookbox.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.cmput301f20t14.bookbox.R;
import com.cmput301f20t14.bookbox.entities.User;
import com.google.android.material.bottomnavigation.BottomNavigationView;

/**
 * This activity displays the options to select and view the
 * different lists. The lists include, outgoing requests,
 * accepted requests and currently borrowed books.
 * @author Alex Mazzuca, Carter Sabadash
 * @version 2020.10.25
 * @see HomeActivity
 * @see NotificationsActivity
 * @see ProfileActivity
 */
public class ListsActivity extends AppCompatActivity {
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lists);

        // get the username from whichever activity we came from
        // this is necessary to access firebase
        username = getIntent().getExtras().getString(User.USERNAME);

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
        bottomNavigationView.setSelectedItemId(R.id.lists_bottom_nav);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()){
                    case R.id.lists_bottom_nav:
                        return true;
                    case R.id.home_bottom_nav:
                        startActivity(new Intent(getApplicationContext(), HomeActivity.class)
                                .putExtra(User.USERNAME, username));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.notification_bottom_nav:
                        startActivity(new Intent(getApplicationContext(), NotificationsActivity.class )
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