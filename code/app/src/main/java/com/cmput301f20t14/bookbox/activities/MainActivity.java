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
package com.cmput301f20t14.bookbox.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.cmput301f20t14.bookbox.R;
import com.cmput301f20t14.bookbox.entities.User;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * This is the initial activity that shows a login screen and allows
 * for user registration (RegisterUserActivity). Upon success, retrieves
 * user data and opens main menu (HomeActivity)
 * @author Carter Sabadash
 * @author Olivier Vadiavaloo
 * @version 2020.10.25
 */
public class MainActivity extends AppCompatActivity {
    EditText usernameEditText;
    EditText passwordEditText;
    FirebaseFirestore database;
    final String TAG = "LOGIN";
    public final int REQUEST_CODE_REGISTER = 1;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        database = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseAuth.getInstance().signOut(); // figure out how to do this properly
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            // start main activity
            login(currentUser.getDisplayName());
        }

        usernameEditText = (EditText) findViewById(R.id.username_editText);
        passwordEditText = (EditText) findViewById(R.id.password_editText);

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
                loginButton.setText(R.string.login_verify);

                final String username = usernameEditText.getText().toString();
                final String password = passwordEditText.getText().toString();

                attemptLogin(view, username, password);
                loginButton.setText(R.string.log_in_button);
            }
        });

        createUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register(v);
            }
        });
    }

    /**
     * Verifies the user and password, if the user and password is correct, go to HomeActivity
     * if it is incorrect, show appropriate error and return
     * @param view The view
     * @param username The entered username
     * @param password The entered password
     */
    private void attemptLogin(final View view, final String username, final String password) {
        boolean isEmptyEditText = false;

        if (username.length() == 0) {
            usernameEditText.setError("Required");
            usernameEditText.requestFocus();
            isEmptyEditText = true;
        }

        if (password.length() == 0) {
            passwordEditText.setError("Required");
            passwordEditText.requestFocus();
            isEmptyEditText = true;
        }

        if (isEmptyEditText) {
            return;
        }

        mAuth.signInWithEmailAndPassword(username, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Button loginButton = findViewById(R.id.login_button);
                            loginButton.setText(R.string.login_login);
                            login(username);
                        } else {
                            // email or password is incorrect; we cant determine which from the task
                            passwordEditText.setError("Email or Password is Incorrect!");
                            passwordEditText.setText("");
                            passwordEditText.requestFocus();

                            usernameEditText.setError("Email or password is Incorrect!");
                            usernameEditText.requestFocus();
                        }
                    }
                });
    }

    /**
     * Starts HomeActivity
     * @param username The Users username
     */
    private void login(String username){
        Button login = findViewById(R.id.login_button);
        login.setText(R.string.login_login);
        Intent intent = new Intent(this, HomeActivity.class);
        intent.putExtra(User.USERNAME, username);
        startActivity(intent);
        finish();
    }

    private void register(View view) {
        // launches the RegisterUserActivity

        Intent intent = new Intent(view.getContext(), RegisterUserActivity.class);
        startActivityForResult(intent, REQUEST_CODE_REGISTER);
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