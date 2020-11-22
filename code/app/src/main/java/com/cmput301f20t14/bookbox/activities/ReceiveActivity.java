package com.cmput301f20t14.bookbox.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

import java.util.HashMap;

public class ReceiveActivity extends AppCompatActivity {
    public static final int REQUEST_SCAN = 529;
    public static final int REQUEST_LOCATION = 5666;
    private TextView requester;
    private TextView title;
    private TextView author;
    private TextView isbn;
    private Button seeLocation;
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
        setContentView(R.layout.activity_receive);

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
        TextView header = (TextView) findViewById(R.id.receive_textview);
        requester = (TextView) findViewById(R.id.receive_textview_2);
        title = (TextView) findViewById(R.id.receive_title);
        author = (TextView) findViewById(R.id.receive_author);
        isbn = (TextView) findViewById(R.id.receive_isbn);
        seeLocation = (Button) findViewById(R.id.receive_see_location);
        handOver = (Button) findViewById(R.id.receive_button);
        scan = (ImageButton) findViewById(R.id.receive_scan);

        // If the book has status borrowed, this
        // means that the user wants to receive a return.
        // The finalStatus will be AVAILABLE and the
        // the layout texts have to change.
        if (book.getStatus() == Book.BORROWED) {
            finalStatus = Book.AVAILABLE;
            header.setText(R.string.return_book);
            handOver.setText(R.string.return_book);
            seeLocation.setText(R.string.see_location);

            CharSequence requesterText = "From " + request.getBorrower();
            requester.setText(requesterText);
        } else {
            finalStatus = Book.BORROWED;
            CharSequence requesterText = "From " + request.getOwner();
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
        if (!bookImage.getUrl().equals("")) {
            StorageReference imageRef = storageReference.child(bookImage.getUrl());

            imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Picasso.get().load(uri).into(bookImageView);
                    bookImage.setUri(uri);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    //Handle any errors
                }
            });
        }

        // Set location button listener
        seeLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ReceiveActivity.this, LocationActivity.class);
                intent.putExtra(User.USERNAME, username);
                intent.putExtra("IS_RECEIVE_RETURN", finalStatus == Book.AVAILABLE);
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
                if (request.getLatLng() != null) {
                    Intent intent = new Intent(getApplicationContext(), ScanningActivity.class);
                    intent.putExtra(User.USERNAME, username);
                    startActivityForResult(intent, REQUEST_SCAN);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_LOCATION) {
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
                                                ReceiveActivity.this,
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
                                                ReceiveActivity.this,
                                                "An error occurred",
                                                Toast.LENGTH_SHORT)
                                        .show();
                            }
                        });
            }
        } else if (requestCode == REQUEST_SCAN) {
            if (resultCode == CommonStatusCodes.SUCCESS && data != null) {
                String barcode = data.getStringExtra(HomeActivity.BARCODE);
                if (barcode.equals(book.getIsbn())) {
                    concludeRequest();
                }
            }
        }
    }

    public void concludeRequest() {
        database
                .collection(Book.BOOKS)
                .document(bookID)
                .update(Book.LENT_TO,
                        finalStatus == Book.AVAILABLE ? "":request.getBorrower())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        if (finalStatus == Book.AVAILABLE) {
                            database
                                    .collection(Request.REQUESTS)
                                    .document(requestID)
                                    .delete()
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(getApplicationContext(), "An error occurred", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
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