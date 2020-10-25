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
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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
 * @version 2020.10.22
 */

/**
 * The Initial Login activity
 * @author Carter Sabadash
 * @version 2020.10.22
 */
public class MainActivity extends AppCompatActivity {
    FirebaseFirestore database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseApp.initializeApp(this);
        database = FirebaseFirestore.getInstance();

        Button createUserButton = findViewById(R.id.register_button);
        Button logInButton = findViewById(R.id.login_button);

        createUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Open RegisterUser activity
                Intent intent = new Intent(view.getContext(), RegisterUserActivity.class);
                view.getContext().startActivity(intent);
            }
        });


        // setting listener for logIn button
        logInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                EditText usernameEditText = findViewById(R.id.username_editText);
                EditText passwordEditText = findViewById((R.id.password_editText));

                final String username = usernameEditText.getText().toString();
                final String password = passwordEditText.getText().toString();

                if (username.length() == 0 || password.length() == 0){
                    Toast.makeText(getApplicationContext(),
                            "No User/Password entered", Toast.LENGTH_SHORT).show();
                    Log.d("LOGIN", "No User/Password");
                    return;
                }

                // see if user exists in firebase, get password, and verify
                // show appropriate message for wrong credentials
                DocumentReference documentReference
                        = database.collection("users").document(username);

                // if documentReference doesn't exist, get document -> document.exists() == False
                documentReference.get().addOnCompleteListener(
                        new OnCompleteListener<DocumentSnapshot>() {

                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                // check that password is correct
                                if (document.get("password").equals(password)) {
                                    // password is correct, perform login operations
                                    login(view);
                                } else {
                                    // password is incorrect, prompt user
                                    Log.d("LOGIN", "Password Incorrect");
                                    Toast.makeText(getApplicationContext(),
                                            "Incorrect Password", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                // user doesn't exist, prompt registration
                                Log.d("LOGIN", "User Incorrect");
                                Toast.makeText(getApplicationContext(),
                                        "Incorrect Username", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
            }
        });
    }

    private void login(View view){
        // gets all data from firebase (user info, books, etc), then starts HomeActivity

        Intent intent = new Intent(view.getContext(), HomeActivity.class);
        startActivity(intent);
        finish();
    }
}