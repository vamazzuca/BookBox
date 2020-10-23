package com.cmput301f20t14.bookbox;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

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

        logInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                EditText usernameEditText = findViewById(R.id.username_editText);
                EditText passwordEditText = findViewById((R.id.password_editText));

                final String username = usernameEditText.getText().toString();
                final String password = passwordEditText.getText().toString();

                if (username.length() == 0 || password.length() == 0){
                    // prompt error message
                    Log.d("No: ", "User/Password");
                    return;
                }

                // establish a connection to firebase, and verify that the user exists
                // if they do, log in
                // if they don't, or the password is wrong, show error message
                    // (use a fragment, or look into adding text above the user/password boxes
                DocumentReference documentReference
                        = database.collection("users").document(username);

                // if documentReference doesn't exist, get document -> document.exists() == False
                documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                // check that password is correct
                                if (document.get("password").equals(password)) {
                                    // password is correct, log in
                                    Intent intent = new Intent(view.getContext(), MainMenuActivity.class);
                                    view.getContext().startActivity(intent);
                                } else {
                                    // password is incorrect, prompt user
                                    Log.d("Password: ", "Incorrect");
                                    return;
                                }
                            } else {
                                // user doesn't exist, prompt registration
                                Log.d("User", ": Incorrect");
                                return;
                            }
                        }
                    }
                });
            }
        });

        createUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Open RegisterUser activity
                Intent intent = new Intent(view.getContext(), RegisterUserActivity.class);
                view.getContext().startActivity(intent);
            }
        });
    }
}