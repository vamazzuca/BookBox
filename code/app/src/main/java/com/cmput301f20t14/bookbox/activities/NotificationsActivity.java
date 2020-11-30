package com.cmput301f20t14.bookbox.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cmput301f20t14.bookbox.R;
import com.cmput301f20t14.bookbox.adapters.NotificationList;
import com.cmput301f20t14.bookbox.entities.Book;
import com.cmput301f20t14.bookbox.entities.Notification;
import com.cmput301f20t14.bookbox.entities.Request;
import com.cmput301f20t14.bookbox.entities.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import static com.cmput301f20t14.bookbox.entities.User.USERS;

/**
 * This is the activity where the user can view their
 * notifications that they receive. In this activity, the notifications
 * show that another user would like to borrow your book, and is notified
 * if their own request can been accepted or declined
 * @author Olivier Vadiavaloo
 * @author Alex Mazzuca
 * @author Carter Sabadash
 * @author Faiyaz Ahmed
 * @version 2020.10.29
 * @see HomeActivity
 * @see ListsActivity
 * @see ProfileActivity
 */
public class NotificationsActivity extends AppCompatActivity {
    private final int REQUEST_ACCEPT = 10;
    private final int REQUEST_RECEIVE_ACCEPT = 11;
    private final int REQUEST_RETURN = 13;
    private FirebaseFirestore database;
    private String username;
    private ListView listView;
    private TextView notificationNumber;
    private ArrayList<Notification> notifications;
    private NotificationList adapter;
    private HashMap<String, String> bookIDHash;
    private HashMap<String, String> notificationIDHash;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        // Initialize database
        database = FirebaseFirestore.getInstance();

        // Initialise bookIDHash
        bookIDHash = new HashMap<>();

        // Initialize notificationIDHash
        notificationIDHash = new HashMap<>();

        // get the username from whichever activity we came from
        // this is necessary to access firebase
        username = getIntent().getExtras().getString(User.USERNAME);

        // Obtain UI resources
        listView = findViewById(R.id.notfications_listView);
        notificationNumber = findViewById(R.id.notification_number);

        // Initialize array list of notifications
        notifications = new ArrayList<>();

        // Initialize ArrayAdapter for Notification objects
        adapter = new NotificationList(NotificationsActivity.this, notifications);

        listView.setAdapter(adapter);

        bottomNavigationView();

        database
                .collection(USERS)
                .document(username)
                .collection(Notification.NOTIFICATIONS)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        notifications.clear();
                        for (QueryDocumentSnapshot doc : value) {
                            String notificationId = doc.getId();
                            String date = "";
                            String bookId = doc.getData().get(Notification.BOOK).toString();
                            if (doc.getData().get(Notification.DATE) != null) {
                              date = doc.getData().get(Notification.DATE).toString();
                            }
                            String user = doc.getData().get(Notification.USER).toString();
                            String type = doc.getData().get(Notification.TYPE).toString();
                            String requestID = doc.getData().get(Notification.REQUEST).toString();

                            notificationIDHash.put(date, notificationId);
                            getBook(notificationId, bookId, date, user, type, requestID);
                        }
                    }
                });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Notification notification = adapter.getItem(position);

                getRequest(notification);
            }
        });
    }

    public void getRequest(final Notification notification) {
        database
                .collection(Request.REQUESTS)
                .document(notification.getRequest())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot doc = task.getResult();
                            if (doc != null && doc.exists()) {
                                String owner = doc.getData().get(Request.OWNER).toString();
                                String borrower = doc.getData().get(Request.BORROWER).toString();
                                String latLng = doc.getData().get(Request.LAT_LNG).toString();
                                String date = doc.getData().get(Request.DATE).toString();
                                String isAccepted = doc.getData().get(Request.IS_ACCEPTED).toString();

                                Request request = new Request(
                                        borrower,
                                        owner,
                                        notification.getBook(),
                                        date,
                                        Boolean.valueOf(isAccepted),
                                        latLng
                                );

                                launchActivity(request, notification);
                            } else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(NotificationsActivity.this);
                                builder
                                        .setTitle("Outdated request! Delete?")
                                        .setNegativeButton("Cancel", null)
                                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                deleteNotification(notificationIDHash.get(notification.getDate()));
                                            }
                                        })
                                        .create()
                                        .show();
                            }
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(NotificationsActivity.this, "An error occurred", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void launchActivity(final Request request, final Notification notification) {
        Intent intent = null;
        int REQUEST_CODE = 0;
        final Book book = notification.getBook();
        switch (notification.getType()) {
            case Notification.ACCEPT:
                if (book.getStatus() == Book.BORROWED) {
                    intent = new Intent(NotificationsActivity.this, ReceiveActivity.class);
                    REQUEST_CODE = REQUEST_RECEIVE_ACCEPT;
                }
                break;
            case Notification.BOOK_REQUEST:
                if (book.getStatus() != Book.ACCEPTED) {
                    intent = new Intent(NotificationsActivity.this, HandOverActivity.class);
                    REQUEST_CODE = REQUEST_ACCEPT;
                }
                break;
            case Notification.RETURN:
                intent = new Intent(NotificationsActivity.this, ReceiveActivity.class);
                REQUEST_CODE = REQUEST_RETURN;
                break;
        }

        if (intent != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(NotificationsActivity.this);
            String title = "";
            String negativeText = "";
            String positiveText = "";
            DialogInterface.OnClickListener negativeBtnListener = null;
            final Intent finalIntent = intent;
            final int finalREQUEST_CODE = REQUEST_CODE;
            DialogInterface.OnClickListener positiveBtnListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finalIntent.putExtra(User.USERNAME, username);
                    finalIntent.putExtra(Request.ID, notification.getRequest());
                    finalIntent.putExtra(Book.ID, bookIDHash.get(book.getIsbn()));

                    deleteNotification(notificationIDHash.get(notification.getDate()));

                    Bundle bundle = new Bundle();
                    bundle.putSerializable("REQUEST_OBJECT", request);
                    bundle.putSerializable("BOOK", book);
                    finalIntent.putExtras(bundle);
                    startActivityForResult(finalIntent, finalREQUEST_CODE);
                }
            };

            switch (REQUEST_CODE) {
                case REQUEST_ACCEPT:
                    if (book.getStatus() == Book.ACCEPTED || book.getStatus() == Book.BORROWED) {
                        title = "Outdated request! Delete?";
                        negativeText = "Cancel";
                        positiveText = "Delete";
                        positiveBtnListener = new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                deleteNotification(notificationIDHash.get(notification.getDate()));
                            }
                        };
                    } else if (book.getLentTo().isEmpty()) {
                        title = "Would you like to accept this request?";
                        negativeText = "Decline";
                        positiveText = "Accept";

                        negativeBtnListener = new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                database
                                        .collection(Request.REQUESTS)
                                        .document(notification.getRequest())
                                        .delete()
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                deleteNotification(notificationIDHash.get(notification.getDate()));
                                                deleteRequest(notification.getRequest(), notification.getBook());
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast
                                                        .makeText(
                                                                NotificationsActivity.this,
                                                                "Could not decline request",
                                                                Toast.LENGTH_SHORT
                                                        )
                                                        .show();
                                            }
                                        });
                            }
                        };

                        positiveBtnListener = new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                database
                                        .collection(Request.REQUESTS)
                                        .document(notification.getRequest())
                                        .update(Request.IS_ACCEPTED, String.valueOf(true))
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                database
                                                        .collection(Book.BOOKS)
                                                        .document(
                                                                bookIDHash.get(book.getIsbn())
                                                        )
                                                        .update(Book.STATUS, String.valueOf(Book.ACCEPTED))
                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {
                                                                declineOtherRequests(bookIDHash.get(book.getIsbn()), notification);
                                                                deleteNotification(notificationIDHash.get(notification.getDate()));
                                                                finalIntent.putExtra(User.USERNAME, username);
                                                                finalIntent.putExtra(Request.ID, notification.getRequest());
                                                                finalIntent.putExtra(Book.ID, bookIDHash.get(book.getIsbn()));

                                                                Bundle bundle = new Bundle();
                                                                bundle.putSerializable("REQUEST_OBJECT", request);
                                                                bundle.putSerializable("BOOK", book);
                                                                finalIntent.putExtras(bundle);
                                                                startActivityForResult(finalIntent, finalREQUEST_CODE);
                                                            }
                                                        });
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast
                                                        .makeText(
                                                                NotificationsActivity.this,
                                                                "Could not accept request",
                                                                Toast.LENGTH_SHORT
                                                        )
                                                        .show();
                                            }
                                        });
                            }
                        };
                    }
                    break;

                case REQUEST_RECEIVE_ACCEPT:
                    if (!book.getLentTo().isEmpty()) {
                        title = "Outdated request! Delete?";
                        negativeText = "Cancel";
                        positiveText = "Delete";
                        positiveBtnListener = new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                deleteNotification(notificationIDHash.get(notification.getDate()));
                            }
                        };
                    } else {
                        title = "Borrow " + book.getTitle();
                        negativeText = "Cancel";
                        positiveText = "Confirm";
                    }
                    break;

                case REQUEST_RETURN:
                    if (book.getLentTo().isEmpty()) {
                        title = "Outdated request! Delete?";
                        negativeText = "Cancel";
                        positiveText = "Delete";
                        positiveBtnListener = new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                deleteNotification(notificationIDHash.get(notification.getDate()));
                            }
                        };
                    } else {
                        title = "Receive return";
                        negativeText = "Cancel";
                        positiveText = "Confirm";

                        positiveBtnListener = new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finalIntent.putExtra(User.USERNAME, username);
                                finalIntent.putExtra(Request.ID, notification.getRequest());
                                finalIntent.putExtra(Book.ID, bookIDHash.get(book.getIsbn()));
                                Bundle bundle = new Bundle();
                                bundle.putSerializable("REQUEST_OBJECT", request);
                                bundle.putSerializable("BOOK", book);
                                finalIntent.putExtras(bundle);
                                startActivityForResult(finalIntent, finalREQUEST_CODE);
                            }
                        };
                    }
                    break;
            }

            builder
                    .setTitle(title)
                    .setNegativeButton(negativeText, negativeBtnListener)
                    .setPositiveButton(positiveText, positiveBtnListener)
                    .create()
                    .show();

        }
    }

    public void deleteRequest(String requestID, final Book book) {
        database
                .collection(Request.REQUESTS)
                .document(requestID)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        database
                                .collection(Request.REQUESTS)
                                .whereEqualTo(Request.BOOK, bookIDHash.get(book.getIsbn()))
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            int count = 0;
                                            for (QueryDocumentSnapshot doc : task.getResult()) {
                                                count += 1;
                                            }
                                            if (count == 0) {
                                                database
                                                        .collection(Book.BOOKS)
                                                        .document(bookIDHash.get(book.getIsbn()))
                                                        .update(Book.STATUS, String.valueOf(Book.AVAILABLE));
                                            }
                                        }
                                    }
                                })
                                .addOnFailureListener(
                                        new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast
                                                        .makeText(NotificationsActivity.this, "An error occurred", Toast.LENGTH_SHORT)
                                                        .show();
                                            }
                                        }
                                );
                    }
                })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast
                                        .makeText(NotificationsActivity.this, "An error occurred", Toast.LENGTH_SHORT)
                                        .show();
                            }
                        }
                );
    }

    public void declineOtherRequests(String bookID, final Notification notification) {
        database
                .collection(Request.REQUESTS)
                .whereEqualTo(Request.BOOK, bookID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if (task.getResult() != null) {
                                for (QueryDocumentSnapshot doc : task.getResult()) {
                                    if (!doc.getId().equals(notification.getRequest())) {
                                        database
                                                .collection(Request.REQUESTS)
                                                .document(doc.getId())
                                                .delete();
                                    }
                                }
                            }
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast
                                .makeText(
                                        NotificationsActivity.this,
                                        "Could not accept request",
                                        Toast.LENGTH_SHORT
                                )
                                .show();
                    }
                });
    }

    public void getBook(final String notificationId, final String bookId, final String date,
                        final String user, final String type, final String requestID) {
        database
                .collection(Book.BOOKS)
                .document(bookId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot doc = task.getResult();
                            if (doc.exists()) {
                                String isbn = doc.getData().get(Book.ISBN).toString();
                                String title = doc.getData().get(Book.TITLE).toString();
                                String author = doc.getData().get(Book.AUTHOR).toString();
                                String owner = doc.getData().get(Book.OWNER).toString();
                                String statusString = doc.getData().get(Book.STATUS).toString();
                                int status = Integer.parseInt(statusString);
                                String lentTo = doc.getData().get(Book.LENT_TO).toString();
                                String imageUrl = doc.getData().get(Book.IMAGE_URL).toString();

                                Book book = new Book(
                                        isbn,
                                        title,
                                        author,
                                        owner,
                                        status,
                                        lentTo,
                                        imageUrl
                                );

                                bookIDHash.put(book.getIsbn(), bookId);

                                initNotification(book, date, user, type, requestID);
                            } else {
                                deleteNotification(notificationId);
                            }
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(NotificationsActivity.this, "An error occurred", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void initNotification(Book book, String date, String user, String type, String requestID) {
        Notification notification = new Notification(user, book, type, date, requestID);
        notifications.add(notification);
        adapter.notifyDataSetChanged();
        CharSequence numberText = "You have " + notifications.size() + " new notifications!";
        notificationNumber.setText(numberText);
    }

    public void deleteNotification(String id) {
        database
                .collection(USERS)
                .document(username)
                .collection(Notification.NOTIFICATIONS)
                .document(id)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        adapter.notifyDataSetChanged();
                        CharSequence numberText = "You have " + notifications.size() + " new notifications!";
                        notificationNumber.setText(numberText);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(NotificationsActivity.this, "An error occurred", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        recreate();
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
        bottomNavigationView.setSelectedItemId(R.id.notification_bottom_nav);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()){
                    case R.id.lists_bottom_nav:
                        startActivity(new Intent(getApplicationContext(), ListsActivity.class )
                                .putExtra(User.USERNAME, username));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.home_bottom_nav:
                        startActivity(new Intent(getApplicationContext(), HomeActivity.class)
                                .putExtra(User.USERNAME, username));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.notification_bottom_nav:
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

}
