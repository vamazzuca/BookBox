package com.cmput301f20t14.bookbox.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cmput301f20t14.bookbox.R;
import com.cmput301f20t14.bookbox.adapters.RequestList;
import com.cmput301f20t14.bookbox.entities.Book;
import com.cmput301f20t14.bookbox.entities.Request;
import com.cmput301f20t14.bookbox.entities.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class ViewBookRequestsActivity extends AppCompatActivity {
    private ListView requestList;
    private RequestList requestAdapter;
    private ArrayList<Request> requests;
    private String username;
    private FirebaseFirestore database;
    private String bookID;
    private Book book;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_book_requests);

        // Initialize database
        database = FirebaseFirestore.getInstance();

        // Get reference to the requests collection
        final CollectionReference requestsCollectionRef = database.collection(Request.REQUESTS);

        // Retrieve book ID whose requests are being viewed
        bookID = getIntent().getStringExtra(Book.ID);

        // Retrieve book whose requests are being viewed
        book = (Book) getIntent().getExtras().getSerializable("VIEW_BOOK");

        // Retrieve username of the user
        username = getIntent().getStringExtra(User.USERNAME);

        TextView bookTitle = (TextView) findViewById(R.id.view_request_book);
        bookTitle.setText(book.getTitle());

        // Initialize request listview
        requestList = (ListView) findViewById(R.id.view_request_listview);

        // Initialize requests
        requests = new ArrayList<>();

        // Initialize request adapter
        requestAdapter = new RequestList(ViewBookRequestsActivity.this, requests);

        requestList.setAdapter(requestAdapter);

        bottomNavigationView();

        requestsCollectionRef
                .whereEqualTo(Request.BOOK, bookID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            for (QueryDocumentSnapshot queryDoc : task.getResult()) {
                                String owner = queryDoc.getData().get(Request.OWNER).toString();
                                String borrower = queryDoc.getData().get(Request.BORROWER).toString();
                                String date = queryDoc.getData().get(Request.DATE).toString();
                                Request request = new Request(borrower, owner, book, date);

                                requests.add(request);
                            }
                            requestAdapter.notifyDataSetChanged();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ViewBookRequestsActivity.this, "An error occurred", Toast.LENGTH_SHORT).show();
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