package com.cmput301f20t14.bookbox.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

/**
 * This activity handles the searching feature of the app.
 * The user can search the database for books that are Available
 * or Requested based on a keyword
 * @author Nicholas DeMarco
 * @version 2020.11.04
 */

public class SearchActivity extends AppCompatActivity {
    public static final String VIEW_BOOK = "VIEW_BOOK";
    public static final int REQUEST_CODE_FROM_SEARCH = 555;
    private FirebaseFirestore database;
    private ListView searchList;
    private EditText searchField;
    private TextView resultsHeader;
    private ArrayList<Book> searchResults;
    private String keyword;
    private BookList searchAdapter;
    private String username;
    private Button searchBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        bottomNavigationView();

        username = getIntent().getExtras().getString(User.USERNAME);

        database = FirebaseFirestore.getInstance();

        searchList = (ListView) findViewById(R.id.search_list_view);

        searchField = (EditText) findViewById(R.id.search_EditText);
        resultsHeader = (TextView) findViewById(R.id.search_results_textview);

        searchResults = new ArrayList<>();

        searchAdapter = new BookList(this, searchResults);

        searchList.setAdapter(searchAdapter);

        searchBtn = (Button) findViewById(R.id.search_word_button);
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchResults.clear();
                executeSearch();
                closeKeyboard();
            }
        });

        // Set the OnItemClick listener of the results ListView
        searchList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Book book = searchAdapter.getItem(position);
                Intent intent = new Intent(SearchActivity.this, EditBookActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable(VIEW_BOOK, book);
                intent.putExtras(bundle);
                intent.putExtra(User.USERNAME, username);
                startActivityForResult(intent, REQUEST_CODE_FROM_SEARCH);
            }
        });
    }

    /**
     * Method closes the keyboard after a search
     * @author Olivier Vadiavaloo
     * @version 2020.11.05
     */
    public void closeKeyboard() {
        InputMethodManager imm = (InputMethodManager) this.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = this.getCurrentFocus();
        if (view == null) {
            view = new View(this);
        }

        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public void executeSearch(){
        keyword = searchField.getText().toString().toLowerCase();
        search(Book.AVAILABLE);
        search(Book.REQUESTED);
    }

    /**
     * Implements search function to update list with all books
     * that contain a string, entered in the EditText box, in thier description
     * (case sensitive) sorted so available books appear above request books
     * @author Nicholas DeMarco
     * @version 2020.11.05
     */
    public void search(int status){
        CollectionReference collectionRef = database.collection(Book.BOOKS);
        collectionRef
                .whereEqualTo(Book.STATUS, String.valueOf(status))
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            for (QueryDocumentSnapshot queryDoc : task.getResult()) {
                                Book book = getBookFromDb(queryDoc);
                                String titleDB = book.getTitle().toLowerCase();
                                String authorDB = book.getAuthor().toLowerCase();
                                String isbnDB = book.getIsbn().toLowerCase();
                                if (titleDB.contains(keyword) || authorDB.contains(keyword) || isbnDB.contains(keyword)) {
                                    searchResults.add(book);
                                }
                            }
                            searchAdapter.notifyDataSetChanged();

                            if (searchResults.isEmpty()) {
                                resultsHeader.setText(R.string.no_results);
                            } else {
                                resultsHeader.setText(R.string.results);
                            }

                            resultsHeader.setVisibility(View.VISIBLE);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(SearchActivity.this, "An error occurred", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    public Book getBookFromDb(QueryDocumentSnapshot queryDoc) {
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_FROM_SEARCH && resultCode == CommonStatusCodes.SUCCESS) {
            searchResults.clear();
            executeSearch();
        }
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
