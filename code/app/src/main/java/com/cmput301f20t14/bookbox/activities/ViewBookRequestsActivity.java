package com.cmput301f20t14.bookbox.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
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
import com.google.android.gms.tasks.OnSuccessListener;
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

        getRequests(requestsCollectionRef);

        requestList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final Request request = requestAdapter.getItem(position);

                requestsCollectionRef
                        .whereEqualTo(Request.DATE, request.getDate())
                        .whereEqualTo(Request.OWNER, request.getOwner())
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful() && task.getResult() != null) {
                                    for (QueryDocumentSnapshot doc : task.getResult()) {
                                        final String requestID = doc.getId();

                                        AlertDialog dialog = new AlertDialog.Builder(ViewBookRequestsActivity.this)
                                                .setTitle(R.string.accept_decline_request)
                                                .setNegativeButton("Cancel", null)
                                                .setNeutralButton("Decline", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        deleteRequest(requestsCollectionRef, requestID);
                                                    }
                                                })
                                                .setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        declineOtherRequests(requestsCollectionRef, requestID);

                                                        Intent intent = new Intent(
                                                                ViewBookRequestsActivity.this,
                                                                AcceptingRequestActivity.class
                                                        );

                                                        intent.putExtra(User.USERNAME, username);
                                                        intent.putExtra(Request.BOOK, requestID);
                                                        Bundle bundle = new Bundle();
                                                        bundle.putSerializable("REQUEST_OBJECT", request);
                                                        intent.putExtras(bundle);

                                                        startActivity(intent);
                                                    }
                                                })
                                                .create();

                                        dialog.show();
                                    }
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast
                                        .makeText(
                                                ViewBookRequestsActivity.this,
                                                "An error occurred",
                                                Toast.LENGTH_SHORT)
                                        .show();
                            }
                        });
            }
        });

    }

    public void getRequests(final CollectionReference requestsCollectionRef) {
        requestsCollectionRef
                .whereEqualTo(Request.BOOK, bookID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            requests.clear();
                            for (QueryDocumentSnapshot queryDoc : task.getResult()) {
                                String owner = queryDoc.getData().get(Request.OWNER).toString();
                                String borrower = queryDoc.getData().get(Request.BORROWER).toString();
                                String date = queryDoc.getData().get(Request.DATE).toString();
                                Request request = new Request(borrower, owner, book, date);

                                requests.add(request);
                            }
                            requestAdapter.notifyDataSetChanged();
                        }

                        if (task.getResult().isEmpty()) {
                            database
                                    .collection(Book.BOOKS)
                                    .document(bookID)
                                    .update(Book.STATUS, String.valueOf(Book.AVAILABLE));
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

    public void declineOtherRequests(final CollectionReference requestsCollectionRef, final String requestID) {
        requestsCollectionRef
                .whereEqualTo(Request.BOOK, bookID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            for (QueryDocumentSnapshot doc : task.getResult()) {
                                if (!requestID.equals(doc.getId())) {
                                    deleteRequest(requestsCollectionRef, doc.getId());
                                }
                            }
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast
                                .makeText(
                                        ViewBookRequestsActivity.this,
                                        "An error occurred",
                                        Toast.LENGTH_SHORT)
                                .show();
                    }
                });
    }

    public void deleteRequest(final CollectionReference requestsCollectionRef, String requestID) {
        requestsCollectionRef
                .document(requestID)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        getRequests(requestsCollectionRef);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast
                                .makeText(
                                        ViewBookRequestsActivity.this,
                                        "An error occurred",
                                        Toast.LENGTH_SHORT)
                                .show();
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