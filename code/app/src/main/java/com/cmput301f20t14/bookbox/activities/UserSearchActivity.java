package com.cmput301f20t14.bookbox.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.cmput301f20t14.bookbox.R;
import com.cmput301f20t14.bookbox.adapters.BookList;
import com.cmput301f20t14.bookbox.entities.User;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;


/**
 * This activity allows the user to search for other users and view their profiles
 * @author Alex Mazzuca
 * @version 2020.11.22
 */
public class UserSearchActivity extends AppCompatActivity {
    private String username;
    private FirebaseFirestore database;
    private ListView searchList;
    private ArrayList<User> searchResults;
    private EditText searchField;
    //private UserList searchAdapter;
    private Button searchButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_search);

        username = getIntent().getExtras().getString(User.USERNAME);

        database = FirebaseFirestore.getInstance();

        searchList = (ListView) findViewById(R.id.search_list__user_view);
        searchField = (EditText) findViewById(R.id.search_Text);

        searchResults = new ArrayList<>();

        //searchAdapter = new BookList(this, searchResults, true);

        bottomNavigationView();
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