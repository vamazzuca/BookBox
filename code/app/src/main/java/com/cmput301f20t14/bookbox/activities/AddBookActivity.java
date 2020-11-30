package com.cmput301f20t14.bookbox.activities;

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
import android.widget.TextView;
import android.widget.Toast;

import com.cmput301f20t14.bookbox.FetchBook;
import com.cmput301f20t14.bookbox.R;
import com.cmput301f20t14.bookbox.fragments.ImageFragment;
import com.cmput301f20t14.bookbox.entities.Book;
import com.cmput301f20t14.bookbox.entities.Image;
import com.cmput301f20t14.bookbox.entities.User;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;
import java.util.UUID;

/**
 * This activity handles the book addition functionality.
 * It allows the user to add a book with a title, author,
 * isbn and an optional picture.
 * @author Olivier Vadiaval
 * @author Alex Mazzuca
 * @version 2020.11.04
 */
public class AddBookActivity extends AppCompatActivity implements ImageFragment.OnFragmentInteractionListener {
    private static final int REQUEST_SCAN = 1234;
    private static final int REQUEST_IMAGE = 1;
    private String username;
    private EditText titleEditText;
    private EditText authorEditText;
    private EditText isbnEditText;
    private TextView warningText;
    private Button addButton, addImageButton, removeImageButton;
    private FirebaseFirestore database;
    private ImageView bookImageView;
    private Uri imageUri;
    private StorageReference storageReference;
    private Image bookImage;
    private String imageUrl;
    private ImageButton scan;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_book);

        // Set up bottom nav bar
        bottomNavigationView();

        // Create book Image object for book Image
        bookImage = new Image(null, null, null, "");
        imageUrl = "";

        // get the username from whichever activity we came from
        // this is necessary to access firebase
        username = getIntent().getExtras().getString(User.USERNAME);

        // Retrieve book image view
        bookImageView = findViewById(R.id.book_picture_imageView);

        // Retrieve book add button and remove button
        addImageButton = findViewById(R.id.add_book_picture_button);
        removeImageButton = findViewById(R.id.remove_book_picture_button);
        scan = findViewById(R.id.add_book_scan);

        // Retrieve the EditText views
        titleEditText = (EditText) findViewById(R.id.Title_editText);
        authorEditText = (EditText) findViewById(R.id.Author_editText);
        isbnEditText = (EditText) findViewById(R.id.ISBN_editText);

        // Retrieve the warning TextView whose visibility is set to "gone"
        warningText = (TextView) findViewById(R.id.add_book_warning_msg);

        // Retrieve the "Add" button
        addButton = (Button) findViewById(R.id.add_book_confirm_button);

        // Get database instance
        database = FirebaseFirestore.getInstance();

        // Get storage reference
        storageReference = FirebaseStorage.getInstance().getReference();

        // Get the collection reference for OWNED books collections
        final CollectionReference ownedBooksCollectionRef = database.collection(User.USERS)
                .document(username)
                .collection(User.OWNED_BOOKS);

        // Set the listener for the "Add" button
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //isError is true if one of the fields is empty
                boolean isErrorSet = false;
                final String title = titleEditText.getText().toString().trim();
                final String author = authorEditText.getText().toString().trim();
                final String isbn = isbnEditText.getText().toString().trim();

                if (title.isEmpty()) {
                    isErrorSet = true;
                    titleEditText.setError("Required");
                }

                if (author.isEmpty()) {
                    isErrorSet = true;
                    authorEditText.setError("Required");
                }

                if (isbn.isEmpty()) {
                    isErrorSet = true;
                    isbnEditText.setError("Required");
                }

                // if isErrorSet is false, then the required fields are not empty
                if (!isErrorSet) {

                    final DocumentReference docRef = ownedBooksCollectionRef.document(isbn);

                    // Check if the book is already inside the collections
                    docRef.get()
                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                         DocumentSnapshot document = task.getResult();

                                         if (document.exists()) {
                                             warningText.setVisibility(View.VISIBLE);
                                             CharSequence warning = "Book with entered ISBN already exists";
                                             warningText.setText(warning);
                                         } else {
                                             addBookToDb(ownedBooksCollectionRef,
                                                     new Book(
                                                         isbn,
                                                         title,
                                                         author,
                                                         username,
                                                         Book.AVAILABLE,
                                                         "",
                                                         imageUrl)
                                             );
                                         }
                                    } else {
                                        Toast.makeText(AddBookActivity.this, "An error occurred", Toast.LENGTH_SHORT).show();
                                    }
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
                startActivityForResult(selectImageIntent, REQUEST_IMAGE);
            }

        });

        //View picture button listener
        bookImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    new ImageFragment().newInstance(bookImage).show(getSupportFragmentManager(), "View Image");
            }
        });

        //Delete picture button listener
        removeImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ImageFragment().show(getSupportFragmentManager(), "Delete Image");
            }
        });

        // Set scan button listener
        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddBookActivity.this, ScanningActivity.class);
                intent.putExtra(User.USERNAME, username);
                startActivityForResult(intent, REQUEST_SCAN);
            }
        });
    }

    /**
     * Adds a book to firestore and updated the users Owned Books
     * @param ownedBooksCollectionRef The CollectionReference to the users owned books
     * @param book The book
     */
    private void addBookToDb(final CollectionReference ownedBooksCollectionRef, final Book book) {
        final CollectionReference booksCollectionRef = database.collection(Book.BOOKS);
        HashMap<String, String> data = new HashMap<>();
        data.put(Book.ISBN, book.getIsbn());
        data.put(Book.TITLE, book.getTitle());
        data.put(Book.AUTHOR, book.getAuthor());
        data.put(Book.STATUS, String.valueOf(book.getStatus()));
        data.put(Book.OWNER, book.getOwner());
        data.put(Book.LENT_TO, book.getLentTo());
        data.put(Book.IMAGE_URL, book.getPhotoUrl());

        booksCollectionRef
                .add(data)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        String id = documentReference.getId();
                        HashMap<String, String> id_map = new HashMap<>();
                        id_map.put(Book.ID, id);

                        ownedBooksCollectionRef.document(book.getIsbn())
                                .set(id_map)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        setResult(CommonStatusCodes.SUCCESS);
                                        finish();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(
                                                AddBookActivity.this,
                                                "Sorry! An error occurred!",
                                                Toast.LENGTH_SHORT
                                        )
                                                .show();
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(
                                AddBookActivity.this,
                                "Sorry! An error occurred!",
                                Toast.LENGTH_SHORT
                        )
                                .show();
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
        bookImage.setUrl(Url);
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
                    Toast.makeText(AddBookActivity.this, "Image Uploaded", Toast.LENGTH_SHORT).show();
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


    /**
     * When and image is added or changed to a select book from the android gallery, this will
     * set the image to the image view can call addImageToStorage to store the image
     * @author Alex Mazzuca
     * @version 2020.11.04
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null){
            imageUri = data.getData();
            addImageToStorage(imageUri);
            bookImageView.setImageURI(imageUri);
            bookImage.setUri(imageUri);
            removeImageButton.setEnabled(true);
            addImageButton.setText("Change Picture");
        } else if (requestCode == REQUEST_SCAN && resultCode == CommonStatusCodes.SUCCESS
                    && data != null) {
            String barcode = data.getStringExtra(HomeActivity.BARCODE);
            isbnEditText.setText(barcode);
            new FetchBook(titleEditText, authorEditText).execute(barcode);
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
        startActivityForResult(selectImageIntent, REQUEST_IMAGE);
    }

    /**
     * Part of the ImageFragment interface where when an image is deleted it will changed
     * the image view to a defualt logo and remove the image
     * @author Alex Mazzuca
     * @version 2020.11.04
     */
    @Override
    public void onDeleteImage(){
        bookImageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_custom_image));
        removeImageButton.setEnabled(false);
        addImageButton.setText("Add Picture");
        bookImage.setUri(null);
        imageUrl = "";

    }

}
