/*
 * HomeActivity.java
 *
 * Version 1.0
 *
 * Date 2020.10.22
 *
 * Copyright 2020 Team 14
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.cmput301f20t14.bookbox;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.material.bottomnavigation.BottomNavigationView;

/**
 * This shows the Home Menu with a task bar at the bottom
 * The Home Menu shows the users owned books (?)
 * By navigating the task bar, the user can:
 *  - View their profile
 *  - View their notifications
 *  - View their Library (Home Menu)
 *  - View a menu where they can choose to view requests
 *      & borrowed books
 * @author Carter Sabadash
 * @author Alex Mazzuca
 * @version 2020.10.24
 */
public class HomeActivity extends AppCompatActivity {

    public static final int REQUEST_CODE_SCANNING = 100;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Log.d("Logged in!", "Yay!");

        bottomNavigationView();
        setUpScanningButton();
    }

    /**
     * Implementation of the bottom navigation bar for switching to different
     * activity views, such as home, profile, notifications and lists
     * References: https://www.youtube.com/watch?v=JjfSjMs0ImQ&feature=youtu.be
     * @author Alex Mazzuca
     * @version 2020.10.24
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
                        startActivity(new Intent(getApplicationContext(), RequestsActivity.class ));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.home_bottom_nav:
                        return true;
                    case R.id.notification_bottom_nav:
                        startActivity(new Intent(getApplicationContext(), NotificationsActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.profile_bottom_nav:
                        startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                }
                return false;
            }
        });
    }

    /**
     * Setting up the onClick listener for the scanning button
     * Listener launches the Scanning activity to obtain a book
     * description by scanning the ISBN
     * @author Olivier Vadiavaloo
     * @version 2020.10.24
     * */
    private void setUpScanningButton() {
        ImageButton camera = (ImageButton) findViewById(R.id.main_page_scan_button);
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, ScanningActivity.class);
                startActivityForResult(intent, REQUEST_CODE_SCANNING);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            // returning from a scan
            case REQUEST_CODE_SCANNING:
                if (data != null && resultCode == CommonStatusCodes.SUCCESS) {
                    // must launch viewing activity for user to be able to view book description
                    Toast.makeText(this, "Launch viewing", Toast.LENGTH_SHORT).show();
                    Toast.makeText(this, data.getStringExtra("BARCODE"), Toast.LENGTH_SHORT).show();
                }
                break;

            default:
                Log.d("Wrong return", "Wrong return");
        }
    }
}
