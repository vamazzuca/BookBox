package com.cmput301f20t14.bookbox.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.cmput301f20t14.bookbox.R;
import com.cmput301f20t14.bookbox.entities.Book;
import com.cmput301f20t14.bookbox.entities.User;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class OwnerRequestActivity extends AppCompatActivity {
    public static final int REQUEST_CODE_SCANNING = 100;
    public static final String BARCODE = "BARCODE";
    private String username;
    private Button scan;
    FirebaseFirestore database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_owner_request);


    }


    /**
     * Setting up the onClick listener for the scanning button
     * Listener launches the Scanning activity to obtain a book
     * description by scanning the ISBN
     * @author Olivier Vadiavaloo
     * @author ALex Mazzuca
     * @version 2020.11.19
     * */
    //private void setUpScanningButton() {
        //ImageButton camera = findViewById(R.id.main_page_scan_button);
        //camera.setOnClickListener(new View.OnClickListener() {
            //@Override
            //public void onClick(View v) {
                //Intent intent = new Intent(OwnerRequestActivity.this, ScanningActivity.class);
                //intent.putExtra(User.USERNAME, username);
                //startActivityForResult(intent, REQUEST_CODE_SCANNING);
           // }
       // });
   // }

    /**
     * Initializes a Book object from the data in a
     * QueryDocumentSnapshot object
     * @param queryDoc QueryDocumentSnapshot object
     *                 obtained after database query
     * @return Book object initialized from data in
     *         queryDoc
     */
    public Book getBookFromDbData(QueryDocumentSnapshot queryDoc) {
        String isbn = queryDoc.getData().get(Book.ISBN).toString();
        String title = queryDoc.getData().get(Book.TITLE).toString();
        String author = queryDoc.getData().get(Book.AUTHOR).toString();
        String owner = queryDoc.getData().get(Book.OWNER).toString();
        String statusString = queryDoc.getData().get(Book.STATUS).toString();
        int status = Integer.parseInt(statusString);
        String lentTo = queryDoc.getData().get(Book.LENT_TO).toString();
        String imageUrl = queryDoc.getData().get(Book.IMAGE_URL).toString();

        return new Book(
                isbn,
                title,
                author,
                owner,
                status,
                lentTo,
                imageUrl
        );
    }

    /**
     * This method launches the EditBookActivity after the
     * ISBN of a book is successfully scanned through the
     * ScanningActivity
     * @param barcode A string representing the scanned
     *                isbn of a book
     */
    public void searchBookInDb(final String barcode) {
        CollectionReference booksCollectionRef = database.collection(Book.BOOKS);
        booksCollectionRef
                .whereEqualTo(Book.OWNER, username)
                .whereEqualTo(Book.ISBN, barcode)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            for (QueryDocumentSnapshot queryDoc : task.getResult()) {
                                //launchViewing(getBookFromDbData(queryDoc));
                            }
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(OwnerRequestActivity.this, "An error occurred", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            case REQUEST_CODE_SCANNING:
                if (data != null && resultCode == CommonStatusCodes.SUCCESS) {
                    // must launch viewing activity for user to be able to view book description
                    Toast.makeText(this, "Launch viewing", Toast.LENGTH_SHORT).show();
                    Toast.makeText(this, data.getStringExtra(BARCODE), Toast.LENGTH_SHORT).show();
                    //searchBookInDb(data.getStringExtra(BARCODE));
                }
                break;
        }
    }
}