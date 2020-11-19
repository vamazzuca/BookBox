package com.cmput301f20t14.bookbox.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.cmput301f20t14.bookbox.R;
import com.cmput301f20t14.bookbox.adapters.BookList;
import com.cmput301f20t14.bookbox.entities.Book;
import com.cmput301f20t14.bookbox.entities.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * This activity shows a list of books that the user has
 * requested, but are not accepted yet. As the user clicks
 * on one of the books from the list, he can see the details
 * of that book
 * Potential feature could be to remove a request
 * @author  Olivier Vadiavaloo
 * @version 2020.11.03
 */

public class OutRequestListActivity extends AppCompatActivity {
    private BookList listAdapter;
    private ArrayList<Book> books;
    private ListView listView;
    private String username;
    private FirebaseFirestore database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_outgoing);

        // get username extra
        username = getIntent().getStringExtra(User.USERNAME);

        // initialize database
        database = FirebaseFirestore.getInstance();

        // find listview
        listView = (ListView) findViewById(R.id.outgoing_listview);

        // initialize adapter and books
        books = new ArrayList<>();
        listAdapter = new BookList(this, books);

        bottomNavigationView();
        setUpList();
        listView.setAdapter(listAdapter);
    }

    public void setUpList() {
        database
                .collection(User.USERS)
                .document(username)
                .collection(User.REQUESTED_BOOKS)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (value != null) {
                            books.clear();
                            for (QueryDocumentSnapshot doc : value) {
                                String id = doc.getId();
                                getRequestedBook(id);
                            }
                            listAdapter.notifyDataSetChanged();
                        }
                    }
                });
    }

    public void getRequestedBook(String id) {
        database
                .collection(Book.BOOKS)
                .document(id)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot doc = task.getResult();
                            if (doc.exists()) {
                                String title = doc.getData().get(Book.TITLE).toString();
                                String author = doc.getData().get(Book.AUTHOR).toString();
                                String isbn = doc.getData().get(Book.ISBN).toString();
                                String owner = doc.getData().get(Book.OWNER).toString();
                                String statusString = doc.getData().get(Book.STATUS).toString();
                                int status = Integer.parseInt(statusString);
                                String lentTo = doc.getData().get(Book.LENT_TO).toString();
                                String imageUrl = doc.getData().get(Book.IMAGE_URL).toString();

                                Book book = new Book(isbn, title, author, owner, status, lentTo, imageUrl);
                                books.add(book);
                            }
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(OutRequestListActivity.this, "An error occurred", Toast.LENGTH_SHORT).show();
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
    private void bottomNavigationView() {
        //Home Navigation bar implementation
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav_bar);
        bottomNavigationView.setSelectedItemId(R.id.lists_bottom_nav);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()){
                    case R.id.lists_bottom_nav:
                        startActivity(new Intent(getApplicationContext(), ListsActivity.class)
                                .putExtra(User.USERNAME, username));
                        finish();
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