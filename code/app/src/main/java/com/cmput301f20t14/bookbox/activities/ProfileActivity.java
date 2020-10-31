package com.cmput301f20t14.bookbox.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.cmput301f20t14.bookbox.R;
import com.cmput301f20t14.bookbox.entities.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;

/**
 * Here is where the user can view their own profile.
 * Through this activity the user can view and edit
 * their username, email address, phone number and
 * profile photo
 * @author Alex Mazzuca, Carter Sabadash
 * @version 2020.10.25
 * @see HomeActivity
 * @see ListsActivity
 * @see NotificationsActivity
 */
public class ProfileActivity extends AppCompatActivity {
    private String username;
    private FirebaseFirestore database;
    private EditText usernameEditText;
    private EditText emailEditText;
    private EditText passwordEditText;
    private EditText phoneEditText;
    private Button confirmButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Get the username from whichever activity we came from
        // this is necessary to access firebase
        username = getIntent().getExtras().getString(User.USERNAME);

        // Get the EditText views
        usernameEditText = (EditText) findViewById(R.id.profile_username_editText);
        emailEditText = (EditText) findViewById(R.id.profile_email_editText);
        passwordEditText = (EditText) findViewById(R.id.profile_password_editText);
        phoneEditText = (EditText) findViewById(R.id.profile_phone_editText);

        // Get "Confirm" Button
        confirmButton = (Button) findViewById(R.id.profile_confirm_button);

        // Set the text in usernameEditText
        usernameEditText.setText(username);

        bottomNavigationView();

        // Initialise database
        database = FirebaseFirestore.getInstance();

        final CollectionReference userCollectionRef = database.collection(User.USERS);

        // Get the user information to fill in the EditTexts
        getUserInfo(userCollectionRef);

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isErrorSet = false;
                String enteredPassword = passwordEditText.getText().toString().trim();
                String enteredEmail = emailEditText.getText().toString().trim();
                String enteredPhone = phoneEditText.getText().toString().trim();

                if (enteredPassword.isEmpty()) {
                    isErrorSet = true;
                    passwordEditText.setError("Required");
                }

                if (enteredPhone.isEmpty()) {
                    isErrorSet = true;
                    phoneEditText.setError("Required");
                }

                if (!isErrorSet) {
                    HashMap<String, String> updatedData = new HashMap<>();
                    updatedData.put(User.PASSWORD, enteredPassword);
                    updatedData.put(User.EMAIL, enteredEmail);
                    updatedData.put(User.PHONE, enteredPhone);

                    userCollectionRef
                            .document(username)
                            .set(updatedData, SetOptions.merge())
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(
                                            ProfileActivity.this,
                                            "User profile updated",
                                            Toast.LENGTH_SHORT)
                                          .show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(
                                            ProfileActivity.this,
                                            "An error occurred",
                                            Toast.LENGTH_SHORT)
                                            .show();
                                }
                            });
                }
            }
        });

    }

    /**
     * Gets the user information from the database
     * @author Olivier Vadiavaloo
     * @version 2020.10.30
     * @param userCollectionRef A reference to the Users' collection
     */
    private void getUserInfo(CollectionReference userCollectionRef) {
        // Get user information to construct a User object
        userCollectionRef
                .document(username)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot doc = task.getResult();

                            if (doc.exists()) {
                                String email = doc.getData().get(User.EMAIL).toString();
                                String password = doc.getData().get(User.PASSWORD).toString();
                                String phone = doc.getData().get(User.PHONE).toString();

                                emailEditText.setText(email);
                                passwordEditText.setText(password);
                                phoneEditText.setText(phone);
                            }
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ProfileActivity.this, "An error occurred", Toast.LENGTH_SHORT).show();
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
    private void bottomNavigationView(){
        //Home Navigation bar implementation
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav_bar);
        bottomNavigationView.setSelectedItemId(R.id.profile_bottom_nav);
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
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.notification_bottom_nav:
                        startActivity(new Intent(getApplicationContext(), NotificationsActivity.class)
                                .putExtra(User.USERNAME, username));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.profile_bottom_nav:
                        return true;
                }
                return false;
            }
        });
    }
}