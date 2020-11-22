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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.CollectionReference;
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
    public static final int REQUEST_CODE_VIEW_BOOK = 300;
    public static final int REQUEST_CODE_SEARCHING = 400;
    public static final String BARCODE = "BARCODE";
    public static final String VIEW_BOOK = "VIEW_BOOK";
    private String username;
    private BookList bookAdapter;
    private ArrayList<Book> books;
    private ListView bookList;
    private Spinner filterSpinner;
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

        // Get Spinner
        filterSpinner = (Spinner) findViewById(R.id.status_filter_spinner);

        // Initialize book list
        books = new ArrayList<>();

        // Initialize book adapter
        bookAdapter = new BookList(HomeActivity.this, books, true);

        // Set adapter
        bookList.setAdapter(bookAdapter);

        bottomNavigationView();
        firebaseInitBookListener();
        setUpItemClickListener();
        setUpFilter();
        setUpSearchingButton();
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
     * This method gets the owned books from the database and adds it to the
     * ListView
     * @param booksCollectionRef Reference to the books collection
     */
    public void getOwnedBooks(CollectionReference booksCollectionRef) {
        booksCollectionRef
                .whereEqualTo(Book.OWNER, username)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            for (QueryDocumentSnapshot queryDoc : task.getResult()) {
                                Book book = getBookFromDbData(queryDoc);
                                bookAdapter.add(book);
                            }
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(HomeActivity.this, "An error occurred 3", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                });
    }

    /**
     * Initializes a Book object from the data in a
     * QueryDocumentSnapshot object
     * @param queryDoc QueryDocumentSnapshot object
     *                 obtained after database query
     * @return Book object initialized from data in
     *         queryDoc
     */
    public Book getBookFromDbData(QueryDocumentSnapshot queryDoc) {
        String isbn = queryDoc.getData().get(Book.ISBN).toString();
        String title = queryDoc.getData().get(Book.TITLE).toString();
        String author = queryDoc.getData().get(Book.AUTHOR).toString();
        String owner = queryDoc.getData().get(Book.OWNER).toString();
        String statusString = queryDoc.getData().get(Book.STATUS).toString();
        int status = Integer.parseInt(statusString);
        String lentTo = queryDoc.getData().get(Book.LENT_TO).toString();
        String imageUrl = queryDoc.getData().get(Book.IMAGE_URL).toString();

        return new Book(
                isbn,
                title,
                author,
                owner,
                status,
                lentTo,
                imageUrl
        );
    }

    /**
     * This method launches the EditBookActivity to allow
     * the user to view the description of a book selected
     * from the list of owned books through clicking or
     * through scanning the ISBN
     * @param book The book whose description will be
     *             viewed
     */
    public void launchViewing(Book book) {
        Intent intent = new Intent(this, EditBookActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(VIEW_BOOK, book);
        intent.putExtras(bundle);
        intent.putExtra(User.USERNAME, username);
        startActivityForResult(intent, REQUEST_CODE_VIEW_BOOK);
    }

    /**
     * This method sets up the OnItemSelected listener and
     * modifies the list of owned books displayed depending
     * on the status selected by the user
     * @author Olivier Vadiavaloo
     * @version 2020.11.02
     */
    public void setUpFilter() {
        // Create the spinner adapter
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.filter_statuses,
                android.R.layout.simple_spinner_item
        );

        // Set which layout to use when list of statuses appears
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        filterSpinner.setAdapter(spinnerAdapter);

        filterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Integer status = null;
                CharSequence itemSelected = (CharSequence) parent.getItemAtPosition(position);
                String itemString = itemSelected.toString();

                // Find what the selected status is
                if (itemString.matches(Book.getStatusString(Book.ACCEPTED))) {
                    status = Book.ACCEPTED;
                } else if (itemString.matches(Book.getStatusString(Book.AVAILABLE))) {
                    status = Book.AVAILABLE;
                } else if (itemString.matches(Book.getStatusString(Book.BORROWED))) {
                    status = Book.BORROWED;
                } else if (itemString.matches(Book.getStatusString(Book.REQUESTED))) {
                    status = Book.REQUESTED;
                }

                // if a status was selected, get the owned books with that status
                if (status != null) {
                    database
                            .collection(Book.BOOKS)
                            .whereEqualTo(Book.OWNER, username)
                            .whereEqualTo(Book.STATUS, status.toString())
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful() && task.getResult() != null) {
                                        if (task.getResult().size() > 0) {
                                            bookAdapter.clear();

                                            // Add the book with selected status to the list to be displayed
                                            for (QueryDocumentSnapshot doc : task.getResult()) {
                                                Book book = getBookFromDbData(doc);
                                                bookAdapter.add(book);
                                            }
                                        } else {
                                            bookAdapter.clear();
                                        }

                                        bookAdapter.notifyDataSetChanged();
                                    }
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(HomeActivity.this, "An error occurred 2", Toast.LENGTH_SHORT).show();
                                }
                            });
                } else if (position == 1) {
                    // if "All" was selected, re-populate the list with all the owned books
                    bookAdapter.clear();
                    getOwnedBooks(database.collection(Book.BOOKS));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    /**
     * Setting up the onItemClickListener for the ListView
     * representing the owned books of the user. On clicking
     * an item, the EditBookActivity is started.
     * @author Olivier Vadiavaloo
     * @version 2020.10.30
     */
    private void setUpItemClickListener() {
        bookList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Book book = bookAdapter.getItem(position);
                launchViewing(book);
            }
        });
    }

    /**
     * Setting up the onClick listener for the search button
     * Listener launches the search activity to find available books
     * based on if a keyword is in the book description
     * @author Nicholas DeMarco
     * @version 2020.11.04
     */
    private void setUpSearchingButton() {
        ImageButton search = findViewById(R.id.search_button);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, SearchActivity.class);
                intent.putExtra(User.USERNAME, username);
                startActivityForResult(intent, REQUEST_CODE_SEARCHING);
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
     * This method launches the EditBookActivity after the
     * ISBN of a book is successfully scanned through the
     * ScanningActivity
     * @param barcode A string representing the scanned
     *                isbn of a book
     */
    public void searchBookInDb(final String barcode) {
        CollectionReference booksCollectionRef = database.collection(Book.BOOKS);
        booksCollectionRef
                .whereEqualTo(Book.OWNER, username)
                .whereEqualTo(Book.ISBN, barcode)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            for (QueryDocumentSnapshot queryDoc : task.getResult()) {
                                launchViewing(getBookFromDbData(queryDoc));
                            }
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(HomeActivity.this, "An error occurred 1", Toast.LENGTH_SHORT).show();
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
                    searchBookInDb(data.getStringExtra(BARCODE));
                }
                break;

            case REQUEST_CODE_ADD_BOOK:
                break;

            case REQUEST_CODE_SEARCHING:
                break;

            case REQUEST_CODE_VIEW_BOOK:
                if (resultCode == EditBookActivity.RESULT_CODE_DELETE) {
                    books.clear();
                    getOwnedBooks(database.collection(Book.BOOKS));
                    Toast.makeText(this, "Book successfully deleted", Toast.LENGTH_SHORT).show();
                }
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
                getOwnedBooks(booksCollectionRef);
                bookAdapter.notifyDataSetChanged();
            }
        });
    }
}
