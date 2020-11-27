package com.cmput301f20t14.bookbox.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

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
import com.cmput301f20t14.bookbox.entities.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
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

/**
 * This is the activity where the user can view their
 * notifications that they receive. In this activity, the notifications
 * show that another user would like to borrow your book, and is notified
 * if their own request can been accepted or declined
 * @author Olivier Vadiavaloo
 * @author Alex Mazzuca
 * @author Carter Sabadash
 * @version 2020.10.25
 * @see HomeActivity
 * @see ListsActivity
 * @see ProfileActivity
 */
public class NotificationsActivity extends AppCompatActivity {
    private FirebaseFirestore database;
    private String username;
    private ListView listView;
    private TextView notificationNumber;
    private ArrayList<Notification> notifications;
    private NotificationList adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        // Initialize database
        database = FirebaseFirestore.getInstance();

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
                .collection(User.USERS)
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

                            getBook(notificationId, bookId, date, user, type);
                        }
                    }
                });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Notification notification = adapter.getItem(position);

                switch (notification.getType()) {
                    case Notification.ACCEPT:

                        break;
                    case Notification.BOOK_REQUEST:
                        break;
                    case Notification.RETURN:
                        break;
                }
            }
        });
    }

    public void getBook(final String notificationId, String bookId, final String date, final String user, final String type) {
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

                                initNotification(book, date, user, type);
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

    public void initNotification(Book book, String date, String user, String type) {
        Notification notification = new Notification(user, book, type, date);
        notifications.add(notification);
        adapter.notifyDataSetChanged();
        CharSequence numberText = "You have " + notifications.size() + " new notifications!";
        notificationNumber.setText(numberText);
    }

    public void deleteNotification(String id) {
        database
                .collection(User.USERS)
                .document(username)
                .collection(Notification.NOTIFICATIONS)
                .document(id)
                .delete()
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(NotificationsActivity.this, "An error occurred", Toast.LENGTH_SHORT).show();
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