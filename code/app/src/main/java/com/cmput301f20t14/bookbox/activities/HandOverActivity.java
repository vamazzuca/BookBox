package com.cmput301f20t14.bookbox.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.cmput301f20t14.bookbox.R;
import com.cmput301f20t14.bookbox.entities.Book;
import com.cmput301f20t14.bookbox.entities.Image;
import com.cmput301f20t14.bookbox.entities.Request;
import com.cmput301f20t14.bookbox.entities.User;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

/**
 * This activity allows the user hand over a book to an owner/borrower,
 * depending on whether the transaction is a borrowing or a return.
 * @author Olivier Vadiavaloo
 * @version 2020.11.19
 */

public class HandOverActivity extends AppCompatActivity {
    public static final int REQUEST_SCAN = 529;
    public static final int REQUEST_LOCATION = 5666;
    private TextView requester;
    private TextView title;
    private TextView author;
    private TextView isbn;
    private Button setLocation;
    private Button handOver;
    private ImageButton scan;
    private FirebaseFirestore database;
    private String username;
    private String bookID;
    private String requestID;
    private Book book;
    private Request request;
    private ImageView bookImageView;
    private Uri imageUri;
    private StorageReference storageReference;
    private Image bookImage;
    private String imageUrl;
    private boolean isLocationSet = false;
    private int finalStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hand_over);

        // Initialise database
        database = FirebaseFirestore.getInstance();

        // Get storage reference
        storageReference = FirebaseStorage.getInstance().getReference();

        // Retrieve username
        username = getIntent().getStringExtra(User.USERNAME);

        // Retrieve book ID and request ID
        bookID = getIntent().getStringExtra(Book.ID);
        requestID = getIntent().getStringExtra(Request.ID);

        // Retrieve Book and Request objects
        request = (Request) getIntent().getExtras().getSerializable("REQUEST_OBJECT");
        book = (Book) getIntent().getExtras().getSerializable("BOOK");

        // Retrieve the necessary views and buttons
        TextView header = (TextView) findViewById(R.id.accepting_textview);
        requester = (TextView) findViewById(R.id.accepting_textview_2);
        title = (TextView) findViewById(R.id.accepting_book_title);
        author = (TextView) findViewById(R.id.accepting_book_author);
        isbn = (TextView) findViewById(R.id.accepting_book_isbn);
        setLocation = (Button) findViewById(R.id.accepting_set_location_btn);
        handOver = (Button) findViewById(R.id.accepting_hand_over);
        scan = (ImageButton) findViewById(R.id.accepting_scan_btn);

        // If the book has status borrowed, this
        // means that the user wants to return it.
        // The finalStatus will be AVAILABLE and the
        // the layout texts have to change.
        if (book.getStatus() == Book.BORROWED) {
            finalStatus = Book.AVAILABLE;
            header.setText(R.string.return_book);
            handOver.setText(R.string.return_book);
            setLocation.setText(R.string.see_location);

            CharSequence requesterText = "Return to " + request.getOwner();
            requester.setText(requesterText);
        } else {
            finalStatus = Book.BORROWED;
            CharSequence requesterText = "From " + request.getBorrower();
            requester.setText(requesterText);
        }

        // Set the textviews
        title.setText(book.getTitle());
        author.setText(book.getAuthor());
        isbn.setText(book.getIsbn());

        // Retrieve book image view
        bookImageView = findViewById(R.id.book_picture_imageView);

        // Create book Image object for book Image
        bookImage = new Image(null, null, null, "");
        imageUrl = "";

        //Get Image URL
        bookImage.setUrl(book.getPhotoUrl());

        imageUrl = book.getPhotoUrl();
        //Download Image from Firebase and set it to ImageView
        if (bookImage.getUrl() != "") {

            Uri uri = Uri.parse(imageUrl);

            Glide.with(bookImageView.getContext())
                    .load(uri)
                    .into(bookImageView);
            bookImage.setUri(uri);


        }

        // Set location button listener
        setLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the LocationActivity to SET a location for
                // the transaction if the user is accepting a request
                Intent intent = new Intent(HandOverActivity.this, LocationActivity.class);
                intent.putExtra(User.USERNAME, username);
                Bundle bundle = new Bundle();
                bundle.putSerializable("REQUEST", request);
                intent.putExtras(bundle);
                startActivityForResult(intent, REQUEST_LOCATION);
            }
        });

        // Set scanning button listener
        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (request.getLatLng() != null && !request.getLatLng().isEmpty()) {
                    Intent intent = new Intent(getApplicationContext(), ScanningActivity.class);
                    intent.putExtra(User.USERNAME, username);
                    startActivityForResult(intent, REQUEST_SCAN);
                } else {
                    setLocation.requestFocus();
                    setLocation.setError("Need to set location first");
                }
            }
        });

        // Set the handOver button's onclick listener
        handOver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an AlertDialog builder
                if (request.getLatLng() != null && !request.getLatLng().isEmpty()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(HandOverActivity.this);

                    // Create and set the content of the AlertDialog to an EditText
                    final EditText isbn = new EditText(HandOverActivity.this);
                    isbn.setInputType(InputType.TYPE_CLASS_TEXT);
                    isbn.setHint(R.string.ISBN_hint);
                    isbn.setPadding(20, 10, 20, 10);
                    builder.setView(isbn);

                    // Set the title and the buttons of the dialog
                    builder.setTitle("Enter ISBN for " + book.getTitle());
                    builder.setNegativeButton("Cancel", null);
                    builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Get entered ISBN
                            String isbnString = isbn.getText().toString().trim();

                            Toast.makeText(HandOverActivity.this, isbnString, Toast.LENGTH_SHORT).show();
                            // Check if the ISBN matches the book's ISBN
                            if (!isbnString.equals(book.getIsbn())) {
                                Toast.makeText(HandOverActivity.this, "Wrong ISBN entered", Toast.LENGTH_SHORT).show();
                            } else {
                                // If ISBN matches, then conclude the request
                                concludeRequest();
                            }
                        }
                    });

                    // Create and show dialog
                    builder.show();
                } else {
                    setLocation.requestFocus();
                    setLocation.setError("Need to set location first");
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_LOCATION) {
            // On successful setting of location, update the latlng field
            // of the request document in the database
            if (resultCode == CommonStatusCodes.SUCCESS && data != null) {
                final String latLng = data.getStringExtra(Request.LAT_LNG);
                database
                        .collection(Request.REQUESTS)
                        .document(requestID)
                        .update(Request.LAT_LNG, latLng)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                request.setLatLng(latLng);
                                Toast
                                        .makeText(
                                                HandOverActivity.this,
                                                "Location set",
                                                Toast.LENGTH_SHORT)
                                        .show();
                                recreate();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast
                                        .makeText(
                                                HandOverActivity.this,
                                                "An error occurred",
                                                Toast.LENGTH_SHORT)
                                        .show();
                            }
                        });
            }
        } else if (requestCode == REQUEST_SCAN) {
            if (resultCode == CommonStatusCodes.SUCCESS && data != null) {
                String barcode = data.getStringExtra(HomeActivity.BARCODE);
                // On successful scanning, conclude the request
                if (barcode.equals(book.getIsbn())) {
                    concludeRequest();
                } else {
                    Toast.makeText(HandOverActivity.this, "Wrong scanned ISBN", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    /**
     * Update the STATUS field of the book document in the database
     * If the book is being lent out, the status is borrowed and if
     * it is being returned the status is available.
     * Notice that the LENT_TO is not updated until the receiver has
     * confirmed the transaction. When a borrower confirms a receival or
     * when an owner confirms a return, the LENT_TO field is updated in
     * the ReceiveActivity.java
     */
    public void concludeRequest() {
        database
                .collection(Book.BOOKS)
                .document(bookID)
                .update(Book.STATUS, String.valueOf(finalStatus))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Intent intent = new Intent();
                        intent.putExtra(Request.ID, requestID);
                        intent.putExtra(Book.ID, bookID);
                        setResult(CommonStatusCodes.SUCCESS, intent);
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "An error occurred", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}