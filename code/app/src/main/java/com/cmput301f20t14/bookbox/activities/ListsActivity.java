package com.cmput301f20t14.bookbox.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.cmput301f20t14.bookbox.R;
import com.cmput301f20t14.bookbox.adapters.ListsPagerAdapter;
import com.cmput301f20t14.bookbox.entities.User;
import com.cmput301f20t14.bookbox.fragments.AcceptedFragment;
import com.cmput301f20t14.bookbox.fragments.BorrowedFragment;
import com.cmput301f20t14.bookbox.fragments.OutRequestFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

/**
 * This activity allows the user to interact with three
 * lists: Requested Books, Accepted books that he/she
 * has requested and Borrowed books. Note that this is
 * a Tabbed activity.
 * @author  Olivier Vadiavaloo
 * @version 2020.11.21
 */

public class ListsActivity extends AppCompatActivity {
    private ListsPagerAdapter listsPagerAdapter;
    private ViewPager viewPager;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lists);

        username = getIntent().getStringExtra(User.USERNAME);

        listsPagerAdapter = new ListsPagerAdapter(getSupportFragmentManager(),
                FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);

        viewPager = findViewById(R.id.view_pager);
        setUpViewPager(viewPager);

        bottomNavigationView();
        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void setUpViewPager(ViewPager viewPager) {
        ListsPagerAdapter adapter = new ListsPagerAdapter(getSupportFragmentManager(),
                FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        adapter.addFragment(OutRequestFragment.newInstance(username), "Requested");
        adapter.addFragment(AcceptedFragment.newInstance(username), "Accepted");
        adapter.addFragment(BorrowedFragment.newInstance(username), "Borrowed");
        viewPager.setAdapter(adapter);
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