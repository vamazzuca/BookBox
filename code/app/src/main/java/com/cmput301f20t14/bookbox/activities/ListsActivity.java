package com.cmput301f20t14.bookbox.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.cmput301f20t14.bookbox.R;
import com.cmput301f20t14.bookbox.entities.User;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

/**
 * This activity displays the options to select and view the
 * different lists. The lists include, outgoing requests,
 * accepted requests and currently borrowed books.
 * @author Alex Mazzuca
 *         Carter Sabadash
 *         Olivier Vadiavaloo
 * @version 2020.10.25
 * @see HomeActivity
 * @see NotificationsActivity
 * @see ProfileActivity
 */
public class ListsActivity extends AppCompatActivity {
    public static final String OUTGOING_REQUESTS_LIST = "Outgoing requests";
    public static final String ACCEPTED_REQUESTS_LIST = "Accepted requests";
    public static final String BORROWED_LIST = "Borrowed books";
    private String username;
    private ArrayAdapter<String> listsAdapter;
    private ArrayList<String> lists;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lists);

        // get the username from whichever activity we came from
        // this is necessary to access firebase
        username = getIntent().getExtras().getString(User.USERNAME);

        // find ListView
        listView = (ListView) findViewById(R.id.lists_listView);

        lists = new ArrayList<>();
        lists.add(OUTGOING_REQUESTS_LIST);
        lists.add(ACCEPTED_REQUESTS_LIST);
        lists.add(BORROWED_LIST);

        listsAdapter = new ArrayAdapter<>(this, R.layout.lists_content, lists);
        listView.setAdapter(listsAdapter);

        setListViewListener();
        bottomNavigationView();
    }

    public void setListViewListener() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String list = listsAdapter.getItem(position);
                if (list.matches(OUTGOING_REQUESTS_LIST)) {
                    Intent intent = new Intent(ListsActivity.this, OutRequestListActivity.class);
                    intent.putExtra(User.USERNAME, username);
                    startActivity(intent);
                }
            }
        });
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