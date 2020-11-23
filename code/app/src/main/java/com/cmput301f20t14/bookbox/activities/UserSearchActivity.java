package com.cmput301f20t14.bookbox.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cmput301f20t14.bookbox.R;
import com.cmput301f20t14.bookbox.adapters.UserList;
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
 * This activity allows the user to search for other users and view their profiles
 * @author Alex Mazzuca
 * @version 2020.11.22
 */
public class UserSearchActivity extends AppCompatActivity {
    public static final int REQUEST_CODE_FROM_SEARCH = 555;
    private String username;
    private FirebaseFirestore database;
    private ListView searchList;
    private TextView resultsHeader;
    private ArrayList<User> searchResults;
    private EditText searchField;
    private String keyword;
    private UserList searchAdapter;
    private Button searchButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_search);

        bottomNavigationView();

        username = getIntent().getExtras().getString(User.USERNAME);

        database = FirebaseFirestore.getInstance();

        searchList = (ListView) findViewById(R.id.search_list_user_view);
        searchField = (EditText) findViewById(R.id.search_text_user);
        resultsHeader = (TextView) findViewById(R.id.search_results_textview_user);

        searchResults = new ArrayList<>();

        searchAdapter = new UserList(this, searchResults);
        searchList.setAdapter(searchAdapter);

        searchButton = (Button) findViewById(R.id.search_button_user);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchResults.clear();
                executeSearch();
                closeKeyboard();
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
        search(User.USERNAME);
    }

    /**
     * Implements search function to update list with all books
     * that contain a string, entered in the EditText box, in thier description
     * (case sensitive) sorted so available books appear above request books
     * @author Nicholas DeMarco
     * @author Alex Mazzuca
     * @version 2020.11.05
     */
    public void search(String username){
        CollectionReference collectionRef = database.collection(User.USERS);
        collectionRef
                .whereEqualTo(User.USERNAME, username)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            for (QueryDocumentSnapshot queryDoc : task.getResult()) {
                                User user = getUserFromDb(queryDoc);
                                String UsernameDB = user.getUsername().toLowerCase();
                                if (UsernameDB.contains(keyword)) {
                                    searchResults.add(user);
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
                        Toast.makeText(UserSearchActivity.this, "An error occurred", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    public User getUserFromDb(QueryDocumentSnapshot queryDoc) {
        String username = queryDoc.getData().get(User.USERNAME).toString();
        String email = queryDoc.getData().get(User.EMAIL).toString();
        String phone = queryDoc.getData().get(User.PHONE).toString();
        String imageUrl = queryDoc.getData().get(User.IMAGE_URL).toString();

        return new User(
                username,
                email,
                phone,
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