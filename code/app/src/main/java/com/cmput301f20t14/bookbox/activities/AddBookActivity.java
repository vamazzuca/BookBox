package com.cmput301f20t14.bookbox.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cmput301f20t14.bookbox.R;
import com.cmput301f20t14.bookbox.entities.Book;
import com.cmput301f20t14.bookbox.entities.User;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

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
 * @version 2020.10.30
 */
public class AddBookActivity extends AppCompatActivity {
    private String username;
    private EditText titleEditText;
    private EditText authorEditText;
    private EditText isbnEditText;
    private TextView warningText;
    private Button addButton, addImageButton;
    private FirebaseFirestore database;
    private ImageView bookImage;
    private Uri imageUri;
    private StorageReference storageReference;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_book);

        // Set up bottom nav bar
        bottomNavigationView();

        // get the username from whichever activity we came from
        // this is necessary to access firebase
        username = getIntent().getExtras().getString(User.USERNAME);

        // Retrieve book image view
        bookImage = findViewById(R.id.book_picture_imageView);

        // Retrieve book add button
        addImageButton = findViewById(R.id.add_book_picture_button);

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
                                                         null)
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


        addImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent selectImageIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(selectImageIntent, 1);
            }

        });


    }

    private void addBookToDb(final CollectionReference ownedBooksCollectionRef, final Book book) {
        final CollectionReference booksCollectionRef = database.collection(Book.BOOKS);
        HashMap<String, String> data = new HashMap<>();
        data.put(Book.ISBN, book.getIsbn());
        data.put(Book.TITLE, book.getTitle());
        data.put(Book.AUTHOR, book.getAuthor());
        data.put(Book.STATUS, String.valueOf(book.getStatus()));
        data.put(Book.OWNER, book.getOwner());
        data.put(Book.LENT_TO, book.getLentTo());

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

    private void addImageToStorage(Uri imageUri){
        final String randomKey = UUID.randomUUID().toString();
        final StorageReference imageRef = storageReference.child("users/"+ username + randomKey);

        imageRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(AddBookActivity.this, "Uploaded", Toast.LENGTH_LONG).show();
                imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get().load(uri).into(bookImage);
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AddBookActivity.this, "Upload Failed", Toast.LENGTH_LONG).show();
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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null){
            imageUri = data.getData();
            bookImage.setImageURI(imageUri);
            addImageToStorage(imageUri);
        }
    }
}
