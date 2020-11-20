package com.cmput301f20t14.bookbox.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.cmput301f20t14.bookbox.R;
import com.cmput301f20t14.bookbox.entities.Book;
import com.cmput301f20t14.bookbox.entities.Image;
import com.cmput301f20t14.bookbox.entities.User;
import com.cmput301f20t14.bookbox.fragments.ImageFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.UUID;

/**
 * Here is where the user can view their own profile.
 * Through this activity the user can view and edit
 * their username, email address, phone number and
 * profile photo
 * @version 2020.11.10
 * @author Alex Mazzuca
 * @author Carter Sabadash
 * @author Olivier Vadiavaloo
 * @see HomeActivity
 * @see ListsActivity
 * @see NotificationsActivity
 */
public class ProfileActivity extends AppCompatActivity implements ImageFragment.OnFragmentInteractionListener{
    private String username;
    private FirebaseFirestore database;
    private EditText usernameEditText;
    private EditText emailEditText;
    private EditText passwordEditText;
    private EditText phoneEditText;
    private Button confirmButton, addImageButton, removeImageButton;;
    private ImageView userImageView;
    private Uri imageUri;
    private StorageReference storageReference;
    private Image userImage;
    private String imageUrl;

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
        emailEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        passwordEditText = (EditText) findViewById(R.id.profile_password_editText);
        phoneEditText = (EditText) findViewById(R.id.profile_phone_editText);

        // Get "Confirm" Button
        confirmButton = (Button) findViewById(R.id.profile_confirm_button);

        // Set the text in usernameEditText
        usernameEditText.setText(username);

        // Create user Image object for user Image
        userImage = new Image(null, null, null, "");
        imageUrl = "";

        // Retrieve book image view
        userImageView = findViewById(R.id.edit_book_imageView);

        // Retrieve book add button and remove button
        addImageButton = findViewById(R.id.add_picture_button);
        removeImageButton = findViewById(R.id.delete_picture_button);

        bottomNavigationView();

        // Initialise database
        database = FirebaseFirestore.getInstance();

        // Get storage reference
        storageReference = FirebaseStorage.getInstance().getReference();

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
                    updatedData.put(User.EMAIL, enteredEmail);
                    updatedData.put(User.PHONE, enteredPhone);
                    updatedData.put(Book.IMAGE_URL, imageUrl);

                    // update email & password as user credentials
                    // must modify the following to ensure that the update can complete
                    FirebaseAuth.getInstance().getCurrentUser()
                            .updateEmail(enteredEmail);
                    FirebaseAuth.getInstance().getCurrentUser()
                            .updatePassword(enteredPassword);

                    // and update everything in the database
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



        //Add picture button listener
        addImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent selectImageIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(selectImageIntent, 1);
            }

        });

        //View picture button listener
        userImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ImageFragment().newInstance(userImage).show(getSupportFragmentManager(), "View Image");
            }
        });

        //Delete picture button listener
        removeImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ImageFragment().show(getSupportFragmentManager(), "Delete Image");
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
                                String phone = doc.getData().get(User.PHONE).toString();
                                imageUrl = doc.getData().get(User.IMAGE_URL).toString();

                                //Get Image URL
                                userImage.setUrl(imageUrl);

                                //Download Image from Firebase and set it to ImageView
                                if (userImage.getUrl() != "") {
                                    StorageReference imageRef = storageReference.child(userImage.getUrl());

                                    imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            Picasso.get().load(uri).into(userImageView);
                                            removeImageButton.setEnabled(true);
                                            addImageButton.setText("Change Picture");
                                            userImage.setUri(uri);
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception exception) {
                                            //Handle any errors
                                        }
                                    });
                                }


                                emailEditText.setText(email);
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
     * This method will add the selected image from the android gallery and upload it to the
     * Firebase storage.
     * @author Alex Mazzuca
     * @version 2020.11.04
     * @param imageUri An imageuri to point to image location
     */
    private void addImageToStorage(Uri imageUri){
        final String randomKey = UUID.randomUUID().toString();
        imageUrl = "users/"+ username + randomKey;
        userImage.setUrl(imageUrl);
        final StorageReference imageRef = storageReference.child(imageUrl);

        imageRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(ProfileActivity.this, "Uploaded", Toast.LENGTH_LONG).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ProfileActivity.this, "Upload Failed", Toast.LENGTH_LONG).show();
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
                        finish();
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.home_bottom_nav:
                        startActivity(new Intent(getApplicationContext(), HomeActivity.class)
                                .putExtra(User.USERNAME, username));
                        finish();
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.notification_bottom_nav:
                        startActivity(new Intent(getApplicationContext(), NotificationsActivity.class)
                                .putExtra(User.USERNAME, username));
                        finish();
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.profile_bottom_nav:
                        return true;
                }
                return false;
            }
        });
    }

    /**
     * When and image is added or changed to a select book from the android gallery, this will
     * set the image to the image view can call addImageToStorage to store the image
     * @author Alex Mazzuca
     * @version 2020.11.04
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null){
            imageUri = data.getData();
            addImageToStorage(imageUri);
            userImageView.setImageURI(imageUri);
            userImage.setUri(imageUri);
            removeImageButton.setEnabled(true);
            addImageButton.setText(R.string.change_picture);
        }
    }

    /**
     * Part of the ImageFragment interface where when an image is changed in the fragment it will
     * get the image from the android gallery and pass it onto onActivityResult
     * @author Alex Mazzuca
     * @version 2020.11.04
     */
    @Override
    public void onUpdateImage(){
        Intent selectImageIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(selectImageIntent, 1);
    }

    /**
     * Part of the ImageFragment interface where when an image is deleted it will changed
     * the image view to a defualt logo and remove the image
     * @author Alex Mazzuca
     * @version 2020.11.04
     */
    @Override
    public void onDeleteImage(){
        userImageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_custom_image));
        removeImageButton.setEnabled(false);
        addImageButton.setText(R.string.add_picture);
        userImage.setUri(null);
        imageUrl = "";

    }

}