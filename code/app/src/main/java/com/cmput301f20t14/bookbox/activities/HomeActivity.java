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
package com.cmput301f20t14.bookbox.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.cmput301f20t14.bookbox.R;
import com.cmput301f20t14.bookbox.adapters.BookList;
import com.cmput301f20t14.bookbox.entities.Book;
import com.cmput301f20t14.bookbox.entities.User;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

/**
 * This shows the Home Menu with a task bar at the bottom
 * The Home Menu shows the users owned books
 * By navigating the task bar, the user can:
 *  - View their profile
 *  - View their notifications
 *  - View their Library (Home Menu)
 *  - View a menu where they can choose to view requests
 *      & borrowed books
 * @author Carter Sabadash
 * @author Alex Mazzuca
 * @author Olivier Vadiavaloo
 * @version 2020.10.30
 * @see NotificationsActivity
 * @see ProfileActivity
 * @see ListsActivity
 */
public class HomeActivity extends AppCompatActivity {

    public static final int REQUEST_CODE_SCANNING = 100;
    public static final int REQUEST_CODE_ADD_BOOK = 200;
    public static final String BARCODE = "BARCODE";
    private String username;
    private BookList bookAdapter;
    private ArrayList<Book> books;
    private ListView bookList;
    FirebaseFirestore database;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        database = FirebaseFirestore.getInstance();

        // Get the username from whichever activity we came from
        // this is necessary to access firebase
        username = getIntent().getExtras().getString(User.USERNAME);

        // Get ListView
        bookList = (ListView) findViewById(R.id.main_page_books_listView);

        // Initialize book list
        books = new ArrayList<>();

        // Initialize book adapter
        bookAdapter = new BookList(HomeActivity.this, books);

        // Set adapter
        bookList.setAdapter(bookAdapter);

        firebaseInitBookListener();
        bottomNavigationView();
        setUpScanningButton();
        setUpAddBookButton();

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

    /**
     * Setting up the onClick listener for the scanning button
     * Listener launches the Scanning activity to obtain a book
     * description by scanning the ISBN
     * @author Olivier Vadiavaloo
     * @version 2020.10.24
     * */
    private void setUpScanningButton() {
        ImageButton camera = findViewById(R.id.main_page_scan_button);
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, ScanningActivity.class);
                intent.putExtra(User.USERNAME, username);
                startActivityForResult(intent, REQUEST_CODE_SCANNING);
            }
        });
    }

    /**
     * Setting up the onClick listener for the scanning button
     * Listener launches the AddBook activity to allow the user
     * to add a new book to his or her collection of books
     * @author Olivier Vadiavaloo
     * @version 2020.10.28
     */
    public void setUpAddBookButton() {
        ImageButton addButton = findViewById(R.id.add_book_button);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, AddBookActivity.class);
                intent.putExtra(User.USERNAME, username);
                startActivityForResult(intent, REQUEST_CODE_ADD_BOOK);
            }
        });
    }

    /**
     * Implements functionality when a previously launched activity
     * is finished with a potential set result.
     * @author Olivier Vadiavaloo
     * @version 2020.10.25
     * */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            // returning from a scan
            case REQUEST_CODE_SCANNING:
                if (data != null && resultCode == CommonStatusCodes.SUCCESS) {
                    // must launch viewing activity for user to be able to view book description
                    Toast.makeText(this, "Launch viewing", Toast.LENGTH_SHORT).show();
                    Toast.makeText(this, data.getStringExtra(BARCODE), Toast.LENGTH_SHORT).show();
                }
                break;

            case REQUEST_CODE_ADD_BOOK:
                break;

            default:
                Log.d("Wrong return", "Wrong return");
        }
    }

    /**
     * This initializes a SnapShotListener to Firebase so the list of books
     * owned by the user is always correct
     * @author Carter Sabadash
     * @author Olivier Vadiavaloo
     * @version 2020.10.27
     */
    private void firebaseInitBookListener(){
        final CollectionReference collectionReference = database
                .collection(User.USERS)
                .document(username)
                .collection(User.OWNED_BOOKS);

        final CollectionReference booksCollectionRef = database.collection(Book.BOOKS);

        // first, get the references to books associated with the user
        collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots,
                                @Nullable FirebaseFirestoreException error) {
                books.clear();

                try {
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {

                        // Need book id to retrieve book data from books collection
                        String id = doc.getData().get(Book.ID).toString();

                        booksCollectionRef
                                .document(id)
                                .get()
                                .addOnCompleteListener(
                                new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        // On successful search, create a book and add to the list view
                                        if (task.isSuccessful()) {
                                            DocumentSnapshot documentSnapshot = task.getResult();

                                            // Book was successfully found in the database
                                            if (documentSnapshot.exists()) {
                                                String title = documentSnapshot.getData().get(Book.TITLE).toString();
                                                String isbn = documentSnapshot.getData().get(Book.ISBN).toString();
                                                String author = documentSnapshot.getData().get(Book.AUTHOR).toString();
                                                String status = documentSnapshot.getData().get(Book.STATUS).toString();
                                                String lent_to = documentSnapshot.getData().get(Book.LENT_TO).toString();
                                                String owner = documentSnapshot.getData().get(Book.OWNER).toString();
                                                Book book = new Book(
                                                        isbn,
                                                        title,
                                                        author,
                                                        owner,
                                                        Integer.parseInt(status),
                                                        lent_to,
                                                        null
                                                );

                                                // Add book to book list
                                                books.add(book);

                                                bookAdapter.notifyDataSetChanged();
                                            }
                                        }
                                    }
                                });
                    }
                } catch (Exception e) {
                    // error handling, generic error
                    Toast.makeText(HomeActivity.this, "An error occurred", Toast.LENGTH_SHORT).show();
                    Toast.makeText(HomeActivity.this, "Try again later", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
