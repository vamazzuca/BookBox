package com.cmput301f20t14.bookbox.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.cmput301f20t14.bookbox.R;
import com.cmput301f20t14.bookbox.adapters.SearchList;
import com.cmput301f20t14.bookbox.entities.Book;
import com.cmput301f20t14.bookbox.entities.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import static com.cmput301f20t14.bookbox.entities.Book.AVAILABLE;

public class SearchActivity extends AppCompatActivity {

    FirebaseFirestore database;
    private ListView searchList;
   // private EditText searchText;
    private Button searchButton;
    private ArrayList<Book> searchResults;
    private String keyString;
    private SearchList searchAdapter;
    private String username;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        bottomNavigationView();

        username = getIntent().getExtras().getString(User.USERNAME);

        database = FirebaseFirestore.getInstance();

        searchList = (ListView) findViewById(R.id.search_list_view);

        searchResults = new ArrayList<>();

        searchAdapter = new SearchList(this,searchResults);

        searchList.setAdapter(searchAdapter);

       // searchText = (EditText) findViewById(R.id.search_EditText);
       // searchButton = (Button) findViewById(R.id.search_word_button);

    }

    public void executeSearch(View view){
        EditText editText = (EditText) findViewById(R.id.search_EditText);
        keyString = editText.getText().toString();
        searchResults = new ArrayList<>();
        searchAdapter = new SearchList(this,searchResults);
        searchList.setAdapter(searchAdapter);
        search();

    }

    public void search(){
        CollectionReference collectionRef = database.collection(Book.BOOKS);
        collectionRef
                .whereEqualTo(Book.STATUS, "66")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            for (QueryDocumentSnapshot queryDoc : task.getResult()) {
//                                String isbn = queryDoc.getData().get(Book.ISBN).toString();
//                                String title = queryDoc.getData().get(Book.TITLE).toString();
//                                String author = queryDoc.getData().get(Book.AUTHOR).toString();
//                                String owner = queryDoc.getData().get(Book.OWNER).toString();
//                                String lentTo = queryDoc.getData().get(Book.LENT_TO).toString();
//                                String statusString = queryDoc.getData().get(Book.STATUS).toString();
//                                int status = Integer.parseInt(statusString);
//
//                                Book book = new Book(
//                                        isbn,
//                                        title,
//                                        author,
//                                        owner,
//                                        status,
//                                        lentTo,
//                                        null
//                                );

                                Book book = getBookFromDb(queryDoc);
                                String titleDB = queryDoc.getData().get(Book.TITLE).toString();
                                String authorDB = queryDoc.getData().get(Book.AUTHOR).toString();
                                String isbnDB = queryDoc.getData().get(Book.ISBN).toString();
                                if (keyString.equals(titleDB) || keyString.equals(authorDB) || keyString.equals(isbnDB)) {

                                    searchAdapter.add(book);
                                }

                            }
                        }
                    }
                });


        collectionRef = database.collection(Book.BOOKS);
        collectionRef
                .whereEqualTo(Book.STATUS, "67")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            for (QueryDocumentSnapshot queryDoc : task.getResult()) {
//                                String isbn = queryDoc.getData().get(Book.ISBN).toString();
//                                String title = queryDoc.getData().get(Book.TITLE).toString();
//                                String author = queryDoc.getData().get(Book.AUTHOR).toString();
//                                String owner = queryDoc.getData().get(Book.OWNER).toString();
//                                String lentTo = queryDoc.getData().get(Book.LENT_TO).toString();
//                                String statusString = queryDoc.getData().get(Book.STATUS).toString();
//                                int status = Integer.parseInt(statusString);
//
//                                Book book = new Book(
//                                        isbn,
//                                        title,
//                                        author,
//                                        owner,
//                                        status,
//                                        lentTo,
//                                        null
//                                );

                                Book book = getBookFromDb(queryDoc);
                                String titleDB = queryDoc.getData().get(Book.TITLE).toString();
                                String authorDB = queryDoc.getData().get(Book.AUTHOR).toString();
                                String isbnDB = queryDoc.getData().get(Book.ISBN).toString();
                                if (keyString.equals(titleDB) || keyString.equals(authorDB) || keyString.equals(isbnDB)) {

                                    searchAdapter.add(book);
                                }

                            }
                        }
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

        return new Book(
                isbn,
                title,
                author,
                owner,
                status,
                lentTo,
                null
        );
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
