package com.cmput301f20t14.bookbox.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.cmput301f20t14.bookbox.R;
import com.cmput301f20t14.bookbox.entities.Book;
import com.cmput301f20t14.bookbox.entities.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class EditBookActivity extends AppCompatActivity {
    private String username;
    private EditText titleEditText;
    private EditText authorEditText;
    private EditText isbnEditText;
    private Button updateBtn;
    private Button viewRequests;
    private Button delete;
    private Button requestBook;
    private FirebaseFirestore database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_book);

        // Get the username from whichever activity we came from
        // this is necessary to access firebase
        username = getIntent().getExtras().getString(User.USERNAME);

        // Get original book object passed through bundle
        final Bundle bundle = getIntent().getExtras();
        final Book book = (Book) bundle.get(HomeActivity.VIEW_BOOK);

        // Get EditText views
        titleEditText = (EditText) findViewById(R.id.edit_title_editText);
        authorEditText = (EditText) findViewById(R.id.edit_author_editText);
        isbnEditText = (EditText) findViewById(R.id.edit_isbn_editText);

        // Get button views
        updateBtn = (Button) findViewById(R.id.edit_book_update_button);
        viewRequests = (Button) findViewById(R.id.edit_book_requests_button);
        delete = (Button) findViewById(R.id.edit_book_delete_button);
        requestBook = (Button) findViewById(R.id.edit_book_request_book);

        // Set up firestore database
        database = FirebaseFirestore.getInstance();

        // Get reference to books collection
        final CollectionReference booksCollectionRef = database.collection(Book.BOOKS);

        // Setting up the bottom nav bar
        bottomNavigationView();

        getBookInfo();

        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isErrorSet = false;

                String title = titleEditText.getText().toString().trim();
                String author = authorEditText.getText().toString().trim();
                String isbn = isbnEditText.getText().toString().trim();

                if (title.isEmpty()) {
                    isErrorSet = true;
                    titleEditText.setError("Required");
                }

                if (author.isEmpty()) {
                    isErrorSet = true;
                    authorEditText.setError("Required");
                }

                if (isbn.isEmpty()) {
                    isErrorSet = true;
                    isbnEditText.setError("Required");
                }

                // left off here
            }
        });
    }

    /**
     * Get the information about the selected book
     * from the bundle
     */
    private void getBookInfo() {
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