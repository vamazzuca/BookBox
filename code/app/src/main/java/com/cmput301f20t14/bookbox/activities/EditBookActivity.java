package com.cmput301f20t14.bookbox.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
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

import com.cmput301f20t14.bookbox.ImageFragment;
import com.cmput301f20t14.bookbox.R;
import com.cmput301f20t14.bookbox.entities.Book;
import com.cmput301f20t14.bookbox.entities.Image;
import com.cmput301f20t14.bookbox.entities.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.net.URI;
import java.util.HashMap;
import java.util.UUID;

public class EditBookActivity extends AppCompatActivity implements ImageFragment.OnFragmentInteractionListener{
    public static final int RESULT_CODE_DELETE = 10;
    private String username;
    private TextView status;
    private TextView owner;
    private TextView borrower;
    private EditText titleEditText;
    private EditText authorEditText;
    private EditText isbnEditText;
    private Button updateBtn, addImageButton, removeImageButton;;
    private Button viewRequests;
    private Button delete;
    private Button requestBook;
    private FirebaseFirestore database;
    private String id;
    private Book book;
    private ImageView bookImageView;
    private Uri imageUri;
    private StorageReference storageReference;
    private Image bookImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_book);

        // Get the username from whichever activity we came from
        // this is necessary to access firebase
        username = getIntent().getExtras().getString(User.USERNAME);

        // Get original book object passed through bundle
        final Bundle bundle = getIntent().getExtras();
        book = (Book) bundle.get(HomeActivity.VIEW_BOOK);

        // Retrieve book image view
        bookImageView = findViewById(R.id.book_picture_imageView);

        // Create book Image object for book Image
        bookImage = new Image(null, null, null, null);

        // Retrieve book add button and remove button
        addImageButton = findViewById(R.id.add_book_picture_button);
        removeImageButton = findViewById(R.id.remove_book_picture_button);

        // Get TextView objects
        status = (TextView) findViewById(R.id.edit_book_status);
        owner = (TextView) findViewById(R.id.edit_book_Owner);
        borrower = (TextView) findViewById(R.id.edit_book_Borrower);

        // Get EditText views
        titleEditText = (EditText) findViewById(R.id.edit_title_editText);
        authorEditText = (EditText) findViewById(R.id.edit_author_editText);
        isbnEditText = (EditText) findViewById(R.id.edit_isbn_editText);

        // Get button views
        updateBtn = (Button) findViewById(R.id.edit_book_update_button);
        viewRequests = (Button) findViewById(R.id.edit_book_requests_button);
        delete = (Button) findViewById(R.id.edit_book_delete_button);
        requestBook = (Button) findViewById(R.id.edit_book_request_book);

        // Set up firestore database
        database = FirebaseFirestore.getInstance();

        // Get storage reference
        storageReference = FirebaseStorage.getInstance().getReference();

        // Get reference to books and users collections
        final CollectionReference booksCollectionRef = database.collection(Book.BOOKS);
        final CollectionReference usersCollectionRef = database.collection(User.USERS);

        // Get book information
        getBookInfo(booksCollectionRef);

        // Setting up the bottom nav bar
        bottomNavigationView();

        // Set up the "Update" button
        setUpdateBtn(booksCollectionRef);

        // Set up the "Delete" button
        setDeleteBtn(booksCollectionRef, usersCollectionRef);



        addImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent selectImageIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(selectImageIntent, 1);
            }

        });

        bookImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bookImage.getUri() != null) {
                    new ImageFragment().newInstance(bookImage).show(getSupportFragmentManager(), "View Image");
                }
            }
        });

        removeImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ImageFragment().show(getSupportFragmentManager(), "Delete Image");
            }
        });

    }

    /**
     * This method sets up the listener for the "Delete" button of the activity
     * @param booksCollectionRef A reference to the books collection
     */
    public  void setDeleteBtn(final CollectionReference booksCollectionRef, final CollectionReference usersCollectionRef) {
        // Set the onClick listener of the "Delete" button
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog dialog = new AlertDialog.Builder(EditBookActivity.this)
                        .setTitle("Are you sure you want to delete this book?")
                        .setNegativeButton("Cancel", null)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                deleteBook(booksCollectionRef, usersCollectionRef);
                            }
                        })
                        .create();
                dialog.show();
            }
        });
    }

    /**
     * This method sets up the listener for the "Update" button of the activity
     * @param booksCollectionRef A reference to the books collection
     */
    public void setUpdateBtn(final CollectionReference booksCollectionRef) {
        // Set the onClick listener of the "Update" button
        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isErrorSet = false;

                // Get the text entered through the EditText views
                final String title = titleEditText.getText().toString().trim();
                final String author = authorEditText.getText().toString().trim();
                final String isbn = isbnEditText.getText().toString().trim();

                // Set an error if any field is left empty
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

                // if no error is set, we can move on to update the data
                // in the database using the editBookInfo method
                if (!isErrorSet) {
                    HashMap<String, String> newData = new HashMap<>();
                    newData.put(Book.TITLE, title);
                    newData.put(Book.AUTHOR, author);
                    newData.put(Book.ISBN, isbn);
                    book.setTitle(title);
                    book.setAuthor(author);
                    book.setIsbn(isbn);

                    editBookInfo(id, newData, booksCollectionRef);

                }
            }
        });
    }

    /**
     * This method deletes a book from the database
     * @param booksCollectionRef A reference to the books collection
     */
    public void deleteBook(final CollectionReference booksCollectionRef, final CollectionReference usersCollectionRef) {
        usersCollectionRef
                .document(username)
                .collection(User.OWNED_BOOKS)
                .document(book.getIsbn())
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        booksCollectionRef
                                .document(id)
                                .delete()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        setResult(RESULT_CODE_DELETE);
                                        finish();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(EditBookActivity.this, "An error occurred", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(EditBookActivity.this, "An error occurred", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * Gets the owned book information from the database
     * @param booksCollectionRef A reference to the books collection
     */
    public void getBookInfo(CollectionReference booksCollectionRef) {
        // Get book information to construct a User object
        // It is important to note that this query should return
        // only one snapshot because we are searching for a book
        // using the username of the owner and the isbn of the book
        // which can be seen a key for the books data if the storage
        // was relational in nature
        booksCollectionRef
                .whereEqualTo(Book.OWNER, username)
                .whereEqualTo(Book.ISBN, book.getIsbn())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && task.getResult().size() == 1) {
                            for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                                // Get the id of the document from the query
                                id = queryDocumentSnapshot.getId();

                                //Get Image URL
                                bookImage.setUrl(queryDocumentSnapshot.getData().get(Book.IMAGE_URL).toString());

                                if (bookImage.getUrl() != null) {
                                    StorageReference imageRef = storageReference.child(bookImage.getUrl());

                                    imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            Picasso.get().load(uri).into(bookImageView);
                                            removeImageButton.setEnabled(true);
                                            addImageButton.setText("Change Picture");
                                            bookImage.setUri(uri);
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception exception) {
                                            //Handle any errors
                                        }
                                    });
                                }

                                // Get the data and put into the EditText views
                                titleEditText.setText(
                                        queryDocumentSnapshot.getData().get(Book.TITLE).toString()
                                );
                                authorEditText.setText(
                                        queryDocumentSnapshot.getData().get(Book.AUTHOR).toString()
                                );
                                isbnEditText.setText(
                                        queryDocumentSnapshot.getData().get(Book.ISBN).toString()
                                );

                                int statusInt = Integer.parseInt(
                                        queryDocumentSnapshot.getData().get(Book.STATUS).toString()
                                );

                                CharSequence statusText = "Status: " + Book.getStatusString(statusInt);
                                status.setText(statusText);

                                CharSequence ownerText = "Owner: " + queryDocumentSnapshot.getData().get(Book.OWNER).toString();
                                owner.setText(ownerText);

                                String borrowerString = queryDocumentSnapshot.getData().get(Book.LENT_TO).toString();
                                CharSequence borrowerText = "Borrower: ";

                                // if there's no borrower, the displayed text should be
                                // "Borrower: None", but if there is a borrower, the
                                // displayed text should be "Borrower: [Username of borrower]"
                                if (!borrowerString.isEmpty()) {
                                    borrowerText = borrowerText + borrowerString;
                                } else {
                                    borrowerText = borrowerText + "None";
                                }

                                borrower.setText(borrowerText);
                                break;
                            }
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(EditBookActivity.this, "An error occurred", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * Edit the information about the selected book
     * using the firestore method and listeners
     */
    private void editBookInfo(String id, HashMap<String, String> data, final CollectionReference booksCollectionRef) {
        // Set the new data for the book and use the merge option
        // because some fields might not have changed
        booksCollectionRef
                .document(id)
                .set(data, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(EditBookActivity.this, "Book updated", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(EditBookActivity.this, "An error occurred", Toast.LENGTH_SHORT).show();
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

    private void addImageToStorage(Uri imageUri){
        final String randomKey = UUID.randomUUID().toString();
        final StorageReference imageRef = storageReference.child("users/"+ username + randomKey);

        imageRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(EditBookActivity.this, "Uploaded", Toast.LENGTH_LONG).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(EditBookActivity.this, "Upload Failed", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null){
            imageUri = data.getData();
            addImageToStorage(imageUri);
            bookImageView.setImageURI(imageUri);
            bookImage.setUri(imageUri);
            removeImageButton.setEnabled(true);
            addImageButton.setText("Change Picture");
        }
    }

    @Override
    public void onUpdateImage(){
        Intent selectImageIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(selectImageIntent, 1);
    }

    @Override
    public void onDeleteImage(){
        bookImageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_custom_image));
        removeImageButton.setEnabled(false);
        addImageButton.setText("Add Picture");
        bookImage.setUri(null);

    }

}