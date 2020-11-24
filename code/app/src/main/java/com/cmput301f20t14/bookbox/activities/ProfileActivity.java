package com.cmput301f20t14.bookbox.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.cmput301f20t14.bookbox.R;
import com.cmput301f20t14.bookbox.entities.Book;
import com.cmput301f20t14.bookbox.entities.Image;
import com.cmput301f20t14.bookbox.entities.User;
import com.cmput301f20t14.bookbox.fragments.ImageFragment;
import com.cmput301f20t14.bookbox.fragments.UpdateEmailFragment;
import com.cmput301f20t14.bookbox.fragments.UpdatePasswordFragment;
import com.cmput301f20t14.bookbox.fragments.UpdatePhoneFragment;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.UUID;

/**
 * Here is where the user can view their own profile.
 * Through this activity the user can view and edit
 * their username, email address, phone number and
 * profile photo and they can logout
 * Logging out will remove the device notification token so they will
 *      no longer receive notifications on this device
 * @version 2020.11.20
 * @author Alex Mazzuca
 * @author Carter Sabadash
 * @author Olivier Vadiavaloo
 * @see HomeActivity
 * @see ListsActivity
 * @see NotificationsActivity
 */
public class ProfileActivity
        extends AppCompatActivity
        implements  ImageFragment.OnFragmentInteractionListener,
                    UpdatePhoneFragment.OnFragmentInteractionListener,
                    UpdateEmailFragment.OnFragmentInteractionListener,
                    UpdatePasswordFragment.OnFragmentInteractionListener{
    public static final int REQUEST_CODE_SEARCHING = 400;
    private String username;
    private FirebaseFirestore database;
    private EditText usernameEditText;
    private Button emailEditText;
    private Button phoneEditText;
    private Button logoutButton;
    private Button updatePasswordButton;
    private Button addImageButton;
    private Button removeImageButton;;
    private ImageView userImageView;
    private Uri imageUri;
    private StorageReference storageReference;
    private Image userImage;
    private String imageUrl;
    private FirebaseAuth mAuth;
    private User userBundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Get the username from whichever activity we came from
        // this is necessary to access firebase
        username = getIntent().getExtras().getString(User.USERNAME);



        // Get the EditText views
        usernameEditText = findViewById(R.id.profile_username_editText);
        emailEditText = findViewById(R.id.profile_email_editText);
        phoneEditText = findViewById(R.id.profile_phone_editText);

        // Get the Buttons
        logoutButton = findViewById(R.id.profile_logout_button);
        updatePasswordButton = findViewById(R.id.profile_update_password_button);

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
        setUpSearchingButton();


        // initialize firebaseAuth
        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() == null) { // no user signed in --> make them login
            Toast.makeText(this, "An Error Occurred!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, MainActivity.class);
            finishAffinity();
            startActivity(intent);
        }

        // Initialise database
        database = FirebaseFirestore.getInstance();

        // Get storage reference
        storageReference = FirebaseStorage.getInstance().getReference();

        final CollectionReference userCollectionRef = database.collection(User.USERS);

        // Get the user information to fill in the EditTexts
        getUserInfo(userCollectionRef);

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logout();
            }
        });

        updatePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new UpdatePasswordFragment().show(getSupportFragmentManager(), "UPDATE_PASSWORD");
            }
        });

        emailEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new UpdateEmailFragment().show(getSupportFragmentManager(), "UPDATE_EMAIL");
            }
        });

        phoneEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new UpdatePhoneFragment().show(getSupportFragmentManager(), "UPDATE_PHONE");
            }
        });

        //Add picture button listener
        addImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent selectImageIntent = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
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
     * This performs the necessary operations to log a user out
     * When a user logs out, the device token for notifications is removed
     * so they will no longer receive notifications on this device.
     * https://stackoverflow.com/questions/3473168/clear-the-entire-history-stack-and-start-a-new-activity-on-android
     *  This was a good resource for resetting the application state
     */
    private void logout() {
        // first we update the database and delete the current token
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if (task.isSuccessful()) {
                    String token = task.getResult();

                    // remove the token from the database
                    database.collection(User.USERS).document(username).collection("TOKENS")
                            .document(token).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                // then we sign out the user
                                mAuth.signOut();
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                finishAffinity();
                                startActivity(intent);
                            } else {
                                Toast.makeText(ProfileActivity.this, "An Error Occurred",
                                        Toast.LENGTH_SHORT).show();
                                logout();
                            }
                        }
                    });
                }
                else {
                    Toast.makeText(ProfileActivity.this, "An Error Occurred",
                            Toast.LENGTH_SHORT).show();
                    logout();
                }
            }
        });
    }

    /**
     * Part of the UpdatePhoneFragment interface.
     * @param phone The new phone number
     */
    @Override
    public void onPhoneUpdated(final String phone) {
        database.collection(User.USERS).document(username).update(User.PHONE, phone)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            phoneEditText.setText(phone);
                        } else {
                            Toast.makeText(ProfileActivity.this,
                                    "An Error Occurred", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    /**
     * Part of the UpdateEmailFragment interface. We must try to reauthenticate the user before
     *  updating their information.
     * @param email The new email.
     */
    @Override
    public void emailUpdatePressed(final String email, String password) {
        final UpdateEmailFragment fragment =
                (UpdateEmailFragment) getSupportFragmentManager().findFragmentByTag("UPDATE_EMAIL");
        AuthCredential authCredential = EmailAuthProvider
                .getCredential(emailEditText.getText().toString().trim(), password);
        mAuth.getCurrentUser().reauthenticate(authCredential).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    // try to update email as a credential
                    mAuth.getCurrentUser().updateEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                // update in database
                                fragment.emailUpdate(true);
                                database.collection(User.USERS).document(username).update(User.EMAIL, email)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    emailEditText.setText(email);
                                                } else {
                                                    Toast.makeText(ProfileActivity.this,
                                                            "An Error Occurred. Credentials Successfully Updated",
                                                            Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                            } else {
                                fragment.emailUpdate(false);
                            }
                        }
                    });
                } else {
                    fragment.incorrectPassword();
                }
            }
        });
    }

    /**
     * Part of the UpdatePasswordFragment interface.
     * https://stackoverflow.com/questions/46780426/how-reauthenticate-user-at-firebase
     *  for reauthenticating users. We user verify() to ensure that the current password
     *  is correct
     * @param password The password, a non-empty string
     */
    @Override
    public void passwordUpdatePressed(String oldPassword, final String password) {
        AuthCredential authCredential = EmailAuthProvider
                .getCredential(emailEditText.getText().toString().trim(), oldPassword);
        mAuth.getCurrentUser().reauthenticate(authCredential)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        final UpdatePasswordFragment fragment = (UpdatePasswordFragment)
                                getSupportFragmentManager().findFragmentByTag("UPDATE_PASSWORD");
                        if (task.isSuccessful()) {
                            // try to update password
                            mAuth.getCurrentUser().updatePassword(password)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                fragment.verify(true);
                                                Toast.makeText(ProfileActivity.this,
                                                        "Password Updated",
                                                        Toast.LENGTH_SHORT).show();
                                            } else {
                                                try {
                                                    throw task.getException();
                                                } catch (FirebaseAuthWeakPasswordException e) {
                                                    fragment.weakPassword();
                                                } catch (Exception e) {
                                                    Toast.makeText(ProfileActivity.this,
                                                            "An Error Occurred",
                                                            Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        }
                                    });
                        } else {
                            fragment.verify(false);
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
                                String phone = doc.getData().get(User.PHONE).toString();
                                imageUrl = doc.getData().get(User.IMAGE_URL).toString();

                                //Get Image URL
                                userImage.setUrl(imageUrl);

                                //Download Image from Firebase and set it to ImageView
                                if (userImage.getUrl() != "") {

                                    Uri uri = Uri.parse(imageUrl);

                                    Picasso.get().load(uri).into(userImageView);
                                    removeImageButton.setEnabled(true);
                                    addImageButton.setText("Change Picture");
                                    userImage.setUri(uri);

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
     * Setting up the onClick listener for the search button
     * Listener launches the search activity to find available books
     * based on if a keyword is in the book description
     * @author Nicholas DeMarco
     * @author ALex Mazzuca
     * @version 2020.11.04
     */
    private void setUpSearchingButton() {
        ImageButton search = findViewById(R.id.profile_search_button);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, UserSearchActivity.class);
                intent.putExtra(User.USERNAME, username);
                startActivityForResult(intent, REQUEST_CODE_SEARCHING);
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
        String Url = "users/"+ username + randomKey;
        userImage.setUrl(Url);
        final StorageReference imageRef = storageReference.child(Url);

        Task<Uri> urlTask = imageRef.putFile(imageUri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                // Continue with the task to get the download URL
                return imageRef.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    imageUrl = downloadUri.toString();
                    // add image to user profile in database
                    database.collection(User.USERS).document(username).update(User.IMAGE_URL, imageUrl);
                } else {
                    // Handle failures
                }
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
     * The image will also be added to the users profile here
     * @author Alex Mazzuca, Carter Sabadash
     * @version 2020.11.20
     */
    @Override
    public void onUpdateImage(){
        Intent selectImageIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(selectImageIntent, 1);

        // add image to user profile in database
        database.collection(User.USERS).document(username).update(User.IMAGE_URL, imageUrl)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (!task.isSuccessful()) {
                            Toast.makeText(ProfileActivity.this,
                                    "An error occurred", Toast.LENGTH_LONG).show();
                        }
                    }
                });

    }

    /**
     * Part of the ImageFragment interface where when an image is deleted it will changed
     * the image view to a defualt logo and remove the image. The image will also be removed
     * from the user profile here
     * @author Alex Mazzuca, Carter Sabadash
     * @version 2020.11.20
     */
    @Override
    public void onDeleteImage(){
        userImageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_custom_image));
        removeImageButton.setEnabled(false);
        addImageButton.setText(R.string.add_picture);
        userImage.setUri(null);
        imageUrl = "";

        // remove image from user profile in database
        database.collection(User.USERS).document(username).update(User.IMAGE_URL, imageUrl);

    }

}