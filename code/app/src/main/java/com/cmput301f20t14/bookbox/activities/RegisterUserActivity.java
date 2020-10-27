/*
 * RegisterUserActivity.java
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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

/**
 * An Activity that will allow a user to register with a unique username
 * @author Carter Sabadash
 * @version 2020.10.22
 */
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.cmput301f20t14.bookbox.R;
import com.cmput301f20t14.bookbox.entities.User;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

/**
 * This the activity allowing a user to register an account
 * for the app. The user is required to enter:
 * - a unique username
 * - a phone number
 * - a password
 * The user can optionally add an email address.
 * It also handles user input validation by setting
 * errors when invalid data is entered by the user.
 * User account information is stored in the Firestore
 * database.
 * @author Olivier Vadiavaloo
 * @version 2020.10.25
 * @see com.google.firebase.firestore.FirebaseFirestore
 */

public class RegisterUserActivity extends AppCompatActivity {
    private EditText username;
    private EditText password;
    private EditText email;
    private EditText phone;
    private Button registerButton;
    private ImageButton backButton;
    private FirebaseFirestore database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Retrieve the EditText views from the layout
        username = (EditText) findViewById(R.id.register_username_editText);
        password = (EditText) findViewById(R.id.register_password_editText);
        email = (EditText) findViewById(R.id.register_email_editText);
        phone = (EditText) findViewById(R.id.register_phone_editText);

        // Retrieve the register Button view
        registerButton = (Button) findViewById(R.id.register_activity_button);

        // Retrieve the back ImageButton view
        backButton = (ImageButton) findViewById(R.id.register_back_button);

        // Initialise database
        database = FirebaseFirestore.getInstance();

        // Get reference to the users collection
        final CollectionReference collectionReference = database.collection(User.USERS);

        // Set the onClickListener for the register button
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                // isErrorSet is true if a required field is empty
                boolean isErrorSet = false;
                final String enteredUsername = username.getText().toString().trim();
                final String enteredPassword = password.getText().toString().trim();
                final String enteredEmail = email.getText().toString().trim();
                final String enteredPhone = phone.getText().toString().trim();

                if (enteredUsername.isEmpty()) {
                    isErrorSet = true;
                    username.setError("Required");
                }

                if (enteredPassword.isEmpty()) {
                    isErrorSet = true;
                    password.setError("Required");
                }

                if (enteredPhone.isEmpty()) {
                    isErrorSet = true;
                    phone.setError("Required");
                }

                // if the required fields are not empty, check if the
                // username is unique by getting a DocumentReference
                // with the entered username as argument
                if (!isErrorSet) {

                    final DocumentReference documentReference = collectionReference.document(enteredUsername);

                    // Try to get data from database
                    // The login in onCompleteListener will
                    // check if there is user account with the
                    // entered username already
                    documentReference.get()
                            .addOnCompleteListener(
                                    new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if (task.isSuccessful()) {
                                                DocumentSnapshot documentSnapshot = task.getResult();

                                                // exists() returns false if the username is not taken already
                                                if (documentSnapshot != null && !documentSnapshot.exists()) {

                                                    // create hash that contains information entered by user
                                                    HashMap<String, String> userInfo = new HashMap<>();
                                                    userInfo.put(User.USERNAME, enteredUsername);
                                                    userInfo.put(User.PASSWORD, enteredPassword);
                                                    userInfo.put(User.EMAIL, enteredEmail);
                                                    userInfo.put(User.PHONE, enteredPhone);

                                                    // documentReference will add the user information
                                                    // to the database at this point since it is confirmed
                                                    // that the entered username is unique. If the addition failed,
                                                    // the onFailureListener will show a toast on the screen
                                                    // notifying that an error occurred
                                                    documentReference
                                                            .set(userInfo)
                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {
                                                                    register(v, enteredUsername);
                                                                }
                                                            })
                                                            .addOnFailureListener(new OnFailureListener() {
                                                                @Override
                                                                public void onFailure(@NonNull Exception e) {
                                                                    Toast
                                                                        .makeText(RegisterUserActivity.this,
                                                                               "An error occurred",
                                                                                Toast.LENGTH_SHORT
                                                                        )
                                                                        .show();
                                                                }
                                                            });

                                                } else {
                                                    //  if the username is already taken, an error
                                                    // is set for the username EditText view
                                                    username.setError("Username already taken");
                                                    username.requestFocus();
                                                }
                                            }
                                        }
                                    }
                            );
                }
            }
        });

        // set the onClickListener for the back button
        // register activity is finished on clicking
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(CommonStatusCodes.CANCELED);
                finish();
            }
        });
    }

    /**
     * Launches the Home activity because user information
     * has been successfully save on the database
     * @author Olivier Vadiavaloo
     * @version 2020.10.25
     * @param view
     * @param enteredUsername
     */
    private void register(View view, String enteredUsername) {
        // User registered successful
        // launch HomeActivity

        Intent intent = new Intent(view.getContext(), HomeActivity.class);
        intent.putExtra(User.USERNAME, enteredUsername);
        intent.putExtra(User.USERNAME, enteredUsername);
        startActivity(intent);

        // finish activity to prevent user from
        // going back to the registering activity
        setResult(CommonStatusCodes.SUCCESS);
        finish();
    }
}
