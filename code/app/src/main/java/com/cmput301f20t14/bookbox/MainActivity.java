/*
 * MainActivity.java
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
package com.cmput301f20t14.bookbox;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * This is the initial activity that shows a login screen and allows
 * for user registration (RegisterUserActivity). Upon success, retrieves
 * user data and opens main menu (HomeActivity)
 * @author Carter Sabadash
<<<<<<< HEAD
 * @version 2020.10.25
=======
 * @author Olivier Vadiavaloo
 * @version 2020.10.22
>>>>>>> origin
 */
public class MainActivity extends AppCompatActivity {
    FirebaseFirestore database;
<<<<<<< HEAD
    final String TAG = "LOGIN";
=======
    public final int REQUEST_CODE_REGISTER = 1;
>>>>>>> origin

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseApp.initializeApp(this);
        database = FirebaseFirestore.getInstance();

        Button createUserButton = findViewById(R.id.register_button);
        final Button loginButton = findViewById(R.id.login_button);

        createUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Open RegisterUser activity
                Intent intent = new Intent(view.getContext(), RegisterUserActivity.class);
                view.getContext().startActivity(intent);
            }
        });


        // setting listener for login button
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
<<<<<<< HEAD
                loginButton.setText(R.string.login_verify);
                EditText usernameEditText = findViewById(R.id.username_editText);
                EditText passwordEditText = findViewById((R.id.password_editText));
=======
                final EditText usernameEditText = findViewById(R.id.username_editText);
                final EditText passwordEditText = findViewById((R.id.password_editText));
>>>>>>> origin

                final String username = usernameEditText.getText().toString();
                final String password = passwordEditText.getText().toString();

                attemptLogin(view, username, password);
                loginButton.setText(R.string.log_in_button);
            }
        });
    }

<<<<<<< HEAD
    /**
     * Verifies the user and password, if the user and password is correct, go to HomeActivity
     * if it is incorrect, show appropriate error and return
     * @param view The view
     * @param username The entered username
     * @param password The entered password
     */
    private void attemptLogin(final View view, final String username, final String password){
        if (username.length() == 0 || password.length() == 0){
            Toast.makeText(getApplicationContext(),
                    "No User/Password entered", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "No User/Password");
            return;
        }

        // see if user exists in firebase, get password, and verify
        // show appropriate message for wrong credentials
        DocumentReference documentReference
                = database.collection("users").document(username);

        // if documentReference doesn't exist, get document -> document.exists() == False
        documentReference.get().addOnCompleteListener(
                new OnCompleteListener<DocumentSnapshot>() {
=======
                // if documentReference doesn't exist, get document -> document.exists() == False
                documentReference
                        .get()
                        .addOnCompleteListener(
                        new OnCompleteListener<DocumentSnapshot>() {
>>>>>>> origin

                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();

                            if (document != null && document.exists()) {

                                // check that password is correct
                                if (document.get(getString(R.string.password)) != null &&
                                        document.get(getString(R.string.password)).equals(password)) {
                                    // password is correct, perform login operations
                                    Button login = findViewById(R.id.login_button);
                                    login.setText(R.string.login_login);
                                    Intent intent = new Intent(view.getContext(), HomeActivity.class);
                                    intent.putExtra("USERNAME", username);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    // password is incorrect, prompt user
<<<<<<< HEAD
                                    Log.d(TAG, "Password Incorrect");
                                    Toast.makeText(getApplicationContext(),
                                            "Incorrect Password", Toast.LENGTH_SHORT).show();

=======
                                    passwordEditText.setError("Invalid password");
>>>>>>> origin
                                }

                            } else {
                                // user doesn't exist, prompt registration
<<<<<<< HEAD
                                Log.d(TAG, "User Incorrect");
                                Toast.makeText(getApplicationContext(),
                                        "Incorrect Username", Toast.LENGTH_SHORT).show();
=======
                                usernameEditText.setError("Invalid username");
>>>>>>> origin
                            }
                        }
                    }
                });
<<<<<<< HEAD
=======
            }
        });

        createUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register(v);
            }
        });
    }

    private void register(View view) {
        // launches the RegisterUserActivity

        Intent intent = new Intent(view.getContext(), RegisterUserActivity.class);
        startActivityForResult(intent, REQUEST_CODE_REGISTER);
    }

    private void login(View view){
        // gets all data from firebase (user info, books, etc), then starts HomeActivity

        Intent intent = new Intent(view.getContext(), HomeActivity.class);
        startActivity(intent);

        // finish activity to prevent user from going back to
        // login by pressing the back button on the device
        finish();
>>>>>>> origin
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // If the register activity was successful, then finish this
        // activity to prevent user from going back to the login activity
        if (requestCode == REQUEST_CODE_REGISTER && resultCode == CommonStatusCodes.SUCCESS) {
            finish();
        }
    }
}