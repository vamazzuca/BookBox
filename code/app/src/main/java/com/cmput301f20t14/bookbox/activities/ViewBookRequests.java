package com.cmput301f20t14.bookbox.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.cmput301f20t14.bookbox.R;
import com.cmput301f20t14.bookbox.adapters.RequestList;
import com.cmput301f20t14.bookbox.entities.Book;
import com.cmput301f20t14.bookbox.entities.Request;
import com.cmput301f20t14.bookbox.entities.User;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class ViewBookRequests extends AppCompatActivity {
    private ListView requestList;
    private RequestList requestAdapter;
    private ArrayList<Request> requests;
    private String username;
    private FirebaseFirestore database;
    private Book book;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_book_requests);

        // Initialize database
        database = FirebaseFirestore.getInstance();

        // Retrieve book whose requests are being viewed
        book = (Book) getIntent().getExtras().get("VIEW_BOOK");

        // Retrieve username of the user
        username = getIntent().getStringExtra(User.USERNAME);

        TextView bookTitle = (TextView) findViewById(R.id.view_request_textview);
        bookTitle.setText(book.getTitle());

        // Initialize request listview
        requestList = (ListView) findViewById(R.id.view_request_listview);

        // Initialize requests
        requests = new ArrayList<>();

        // Initialize request adapter
        requestAdapter = new RequestList(ViewBookRequests.this, requests);

        bottomNavigationView();

        database
                .collection(Request.REQUESTS)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        requests.clear();
                        for (QueryDocumentSnapshot queryDocumentSnapshot : value) {
                            String requester = queryDocumentSnapshot.getData().get(Request.BORROWER).toString();
                            String date = queryDocumentSnapshot.getData().get(Request.DATE).toString();
                            String owner = queryDocumentSnapshot.getData().get(Request.OWNER).toString();

                            Request request = new Request(requester, owner, book, date);
                            requests.add(request);
                        }
                        requestAdapter.notifyDataSetChanged();
                    }
                });
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