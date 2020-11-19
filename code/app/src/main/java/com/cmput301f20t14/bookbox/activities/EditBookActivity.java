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

import com.cmput301f20t14.bookbox.entities.Request;
import com.cmput301f20t14.bookbox.fragments.ImageFragment;
import com.cmput301f20t14.bookbox.R;
import com.cmput301f20t14.bookbox.entities.Book;
import com.cmput301f20t14.bookbox.entities.Image;
import com.cmput301f20t14.bookbox.entities.User;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.UUID;

/**
 * This activity handles the edit book feature of BookBox
 * It allows the user to add a picture to the book, edit the
 * title, author and the isbn and also allows the user to
 * delete the book. This activity is mainly launched from
 * HomeActivity
 * @author Olivier Vadiavaloo
 * @version 2020.11.03
 */

public class EditBookActivity extends AppCompatActivity implements ImageFragment.OnFragmentInteractionListener{
    public static final int RESULT_CODE_DELETE = 10;
    private String username;
    private TextView status;
    private TextView owner;
    private TextView borrower;
    private EditText titleEditText;
    private EditText authorEditText;
    private EditText isbnEditText;
    private Button updateBtn;
    private Button addImageButton;
    private Button removeImageButton;
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
    private String imageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_book);

        // Get the username from whichever activity we came from
        // this is necessary to access firebase
        username = getIntent().getExtras().getString(User.USERNAME);

        // Get original book object passed through bundle
        final Bundle bundle = getIntent().getExtras();
        book = (Book) bundle.get("VIEW_BOOK");

        // Retrieve book image view
        bookImageView = findViewById(R.id.book_picture_imageView);

        // Create book Image object for book Image
        bookImage = new Image(null, null, null, "");
        imageUrl = "";

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

        if (book.getOwner().equals(username)) {
            requestBook.setVisibility(View.GONE);      // user cannot request own book

            // Hide the following buttons if a request
            // on the book has been accepted or if the
            // book is borrowed by some other user
            if (book.getStatus() == Book.ACCEPTED ||
                book.getStatus() == Book.BORROWED) {
                updateBtn.setVisibility(View.GONE);
                viewRequests.setVisibility(View.GONE);
                delete.setVisibility(View.GONE);
            }
        } else {
            // else, book is not owned by the user
            // So, no changes can be made to the book
            updateBtn.setVisibility(View.GONE);
            viewRequests.setVisibility(View.GONE);
            delete.setVisibility(View.GONE);
            addImageButton.setEnabled(false);
            removeImageButton.setEnabled(false);
            bookImageView.setEnabled(false);

            titleEditText.setEnabled(false);
            authorEditText.setEnabled(false);
            isbnEditText.setEnabled(false);
        }

        // Set up firestore database
        database = FirebaseFirestore.getInstance();

        // Get storage reference
        storageReference = FirebaseStorage.getInstance().getReference();

        // Get reference to books, users and requests collections
        final CollectionReference booksCollectionRef = database.collection(Book.BOOKS);
        final CollectionReference usersCollectionRef = database.collection(User.USERS);
        final CollectionReference requestsCollectionRef = database.collection(Request.REQUESTS);

        // set info in TextViews and EditText views
        titleEditText.setText(book.getTitle());
        authorEditText.setText(book.getAuthor());
        isbnEditText.setText(book.getIsbn());

        CharSequence statusText = "Status: " + book.getStatusString();
        status.setText(statusText);

        CharSequence borrowerText = "Borrower: ";
        if (book.getLentTo().isEmpty()) {
            borrowerText = borrowerText + "None";
        } else {
            borrowerText = borrowerText + book.getLentTo();
        }
        borrower.setText(borrowerText);

        CharSequence ownerText = "Owner: " + book.getOwner();
        owner.setText(ownerText);

        // Get book id
        getBookId(usersCollectionRef);

        // Setting up the bottom nav bar
        bottomNavigationView();

        // Set up the "View Request" button
        viewRequests.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start view book requests activity
                Intent intent = new Intent(EditBookActivity.this, ViewBookRequestsActivity.class);
                intent.putExtra(User.USERNAME, username);
                intent.putExtra(Book.ID, id);
                Bundle bundle = new Bundle();
                bundle.putSerializable("VIEW_BOOK", book);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        // Set up the "Request" button
        requestBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String, String> requestData = new HashMap<>();
                requestData.put(Request.OWNER, book.getOwner());
                requestData.put(Request.BORROWER, username);
                requestData.put(Book.ID, id);

                Date today = Calendar.getInstance().getTime();
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                String date = dateFormat.format(today);
                requestData.put(Request.DATE, date);
                requestData.put(Request.BOOK, id);

                updateRequestsCollection(
                        requestsCollectionRef,
                        booksCollectionRef,
                        usersCollectionRef,
                        requestData
                );
            }
        });

        // Set up the "Update" button
        setUpdateBtn(booksCollectionRef);

        // Set up the "Delete" button
        setDeleteBtn(booksCollectionRef, usersCollectionRef, requestsCollectionRef);

        //Get Image URL
        bookImage.setUrl(book.getPhotoUrl());

        imageUrl = book.getPhotoUrl();
        //Download Image from Firebase and set it to ImageView
        if (!bookImage.getUrl().equals("")) {
            StorageReference imageRef = storageReference.child(bookImage.getUrl());

            imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Picasso.get().load(uri).into(bookImageView);
                    if (book.getOwner().equals(username)) {
                        removeImageButton.setEnabled(true);
                    }
                    addImageButton.setText(R.string.change_picture);
                    bookImage.setUri(uri);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    //Handle any errors
                }
            });
        }


        //Add picture button listener
        addImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent selectImageIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(selectImageIntent, 1);
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

    }

    /**
     * Updates the collection for REQUESTS in the database
     * @param requestsCollectionRef reference to the requests collection
     */
    public void updateRequestsCollection(final CollectionReference requestsCollectionRef,
                                         final CollectionReference booksCollectionRef,
                                         final CollectionReference usersCollectionRef,
                                         HashMap<String, String> requestData) {
        requestsCollectionRef
                .add(requestData)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        updateBooksCollection(
                                booksCollectionRef,
                                usersCollectionRef
                        );
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
     * Updates the collection for BOOKS in the database
     * @param booksCollectionRef reference to the books collection
     */
    public void updateBooksCollection(final CollectionReference booksCollectionRef,
                                      final CollectionReference usersCollectionRef) {
        booksCollectionRef
                .document(id)
                .update(Book.STATUS, String.valueOf(Book.REQUESTED))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        updateUsersCollection(
                                usersCollectionRef
                        );
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
     * Updates the collection for USERS in the database
     * @param usersCollectionRef reference to the users collection
     */
    public void updateUsersCollection(final CollectionReference usersCollectionRef) {
        HashMap<String, String> data = new HashMap<>();
        data.put(Book.OWNER, book.getOwner());

        usersCollectionRef
                .document(username)
                .collection(User.REQUESTED_BOOKS)
                .document(id)
                .set(data, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        setResult(CommonStatusCodes.SUCCESS);
                        Toast.makeText(EditBookActivity.this, "Book requested", Toast.LENGTH_SHORT).show();
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

    /**
     * This method sets up the listener for the "Delete" button of the activity
     * @param booksCollectionRef    A reference to the books collection
     * @param usersCollectionRef    A reference to the users collection
     * @param requestsCollectionRef A reference to the requests collection
     */
    public  void setDeleteBtn(final CollectionReference booksCollectionRef, final CollectionReference usersCollectionRef,
                              final CollectionReference requestsCollectionRef) {
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
                                deleteBook(booksCollectionRef, usersCollectionRef, requestsCollectionRef);
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
                    newData.put(Book.IMAGE_URL, imageUrl);
                    editBookInfo(id, newData, booksCollectionRef);

                }
            }
        });
    }

    /**
     * This method deletes a book from the database
     * @param booksCollectionRef    A reference to the books collection
     * @param usersCollectionRef    A reference to the users collection
     * @param requestsCollectionRef A reference to the requests collection
     */
    public void deleteBook(final CollectionReference booksCollectionRef, final CollectionReference usersCollectionRef,
                           final CollectionReference requestsCollectionRef) {
        // First delete the book from the user's owned_book colllection
        usersCollectionRef
                .document(username)
                .collection(User.OWNED_BOOKS)
                .document(book.getIsbn())
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Then, delete the books from the books collection
                        booksCollectionRef
                                .document(id)
                                .delete()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        // Finally, delete the requests concerning the book
                                        requestsCollectionRef
                                                .whereEqualTo(Book.ID, id)
                                                .get()
                                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                        if (task.isSuccessful()) {
                                                            for (QueryDocumentSnapshot queryDoc : task.getResult()) {
                                                                String requestId = queryDoc.getId();
                                                                requestsCollectionRef
                                                                        .document(requestId)
                                                                        .delete()
                                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                            @Override
                                                                            public void onSuccess(Void aVoid) {

                                                                            }
                                                                        })
                                                                        .addOnFailureListener(new OnFailureListener() {
                                                                            @Override
                                                                            public void onFailure(@NonNull Exception e) {

                                                                            }
                                                                        });
                                                            }
                                                            setResult(RESULT_CODE_DELETE);
                                                            finish();
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
     * Gets the owned book id from the database
     * @param usersCollectionRef A reference to the books collection
     */
    public void getBookId(CollectionReference usersCollectionRef) {
        usersCollectionRef
                .document(book.getOwner())
                .collection(User.OWNED_BOOKS)
                .document(book.getIsbn())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            DocumentSnapshot doc = task.getResult();
                            if (doc.getData() != null) {
                                id = doc.getData().get(Book.ID).toString();
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
     * It first updates the data
     */
    private void editBookInfo(final String id, final HashMap<String, String> data, final CollectionReference booksCollectionRef) {
        // Set the new data for the book and use the merge option
        // because some fields might not have changed

        final CollectionReference ownedBooks =  database
                .collection(User.USERS)
                .document(username)
                .collection(User.OWNED_BOOKS);

        booksCollectionRef
                .document(id)
                .set(data, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        ownedBooks
                                .document(book.getIsbn())
                                .delete()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        HashMap<String, String> idMap = new HashMap<>();
                                        idMap.put(Book.ID, id);

                                        ownedBooks
                                                .document(data.get(Book.ISBN))
                                                .set(idMap)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Toast.makeText(EditBookActivity.this, "Book updated", Toast.LENGTH_SHORT).show();
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
        bookImage.setUrl(imageUrl);
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
            bookImageView.setImageURI(imageUri);
            bookImage.setUri(imageUri);
            book.setPhotoUrl(imageUrl);
            if (book.getOwner().equals(username)) {
                removeImageButton.setEnabled(true);
            }
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
        bookImageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_custom_image));
        removeImageButton.setEnabled(false);
        addImageButton.setText("Add Picture");
        bookImage.setUri(null);
        imageUrl = "";

    }

}