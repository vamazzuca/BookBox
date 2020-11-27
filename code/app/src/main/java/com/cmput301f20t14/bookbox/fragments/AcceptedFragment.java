package com.cmput301f20t14.bookbox.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.cmput301f20t14.bookbox.R;
import com.cmput301f20t14.bookbox.activities.ReceiveActivity;
import com.cmput301f20t14.bookbox.adapters.BookList;
import com.cmput301f20t14.bookbox.entities.Book;
import com.cmput301f20t14.bookbox.entities.Request;
import com.cmput301f20t14.bookbox.entities.User;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;

import javax.annotation.Nullable;

import static android.view.View.GONE;

/**
 * This fragment is one of the tabs of the tabbed activity: listsActivity
 * It contains a listview of the requests made by the user that have been accepted.
 * @author Olivier Vadiavaloo
 * @see com.cmput301f20t14.bookbox.activities.ListsActivity
 */

public class AcceptedFragment extends Fragment {
    public static final int REQUEST_RECEIVE = 8450;
    private BookList listAdapter;
    private ArrayList<Book> books;
    private ListView listView;
    private String username;
    private FirebaseFirestore database;
    private HashMap<String, String> requestIDHash;
    private HashMap<String, String> bookIDHash;

    public static AcceptedFragment newInstance(String usernameArg) {
        Bundle args = new Bundle();
        args.putString(User.USERNAME, usernameArg);

        AcceptedFragment fragment = new AcceptedFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @androidx.annotation.Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_outrequest, container, false);

        // get username extra
        Bundle args = getArguments();
        if (args != null) {
            username = args.getString(User.USERNAME);
        }

        // initialize database
        database = FirebaseFirestore.getInstance();

        // find listview
        listView = (ListView) view.findViewById(R.id.fragment_listview);

        // initialize adapter and books
        books = new ArrayList<>();
        listAdapter = new BookList(getContext(), books, false);

        // initialize HashMaps
        requestIDHash = new HashMap<>();
        bookIDHash = new HashMap<>();

        setUpList();
        listView.setAdapter(listAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
                final Book book = listAdapter.getItem(position);

                if (book.getStatus() == Book.BORROWED) {
                    AlertDialog dialog = new AlertDialog.Builder(view.getContext())
                            .setTitle("Borrow book from " + book.getOwner())
                            .setNegativeButton("Cancel", null)
                            .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    getRequestInfo(book, view);
                                }
                            })
                            .create();
                    dialog.show();
                }

            }
        });

        return view;
    }

    public void getRequestInfo(final Book book, final View view) {
        final String bookID = bookIDHash.get(book.getIsbn());
        final String requestID = requestIDHash.get(bookID);
        database
                .collection(Request.REQUESTS)
                .document(requestID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            DocumentSnapshot doc = task.getResult();
                            String owner = doc.getData().get(Request.OWNER).toString();
                            String borrower = doc.getData().get(Request.BORROWER).toString();
                            String isAccepted = doc.getData().get(Request.IS_ACCEPTED).toString();
                            String latLng = doc.getData().get(Request.LAT_LNG).toString();
                            String date = doc.getData().get(Request.DATE).toString();

                            Request request = new Request(borrower, owner, book, date, Boolean.valueOf(isAccepted), latLng);
                            Intent intent = new Intent(view.getContext(), ReceiveActivity.class);
                            intent.putExtra(User.USERNAME, username);
                            intent.putExtra(Book.ID, bookID);
                            intent.putExtra(Request.ID, requestID);
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("REQUEST_OBJECT", request);
                            bundle.putSerializable("BOOK", book);
                            intent.putExtras(bundle);
                            startActivityForResult(intent, REQUEST_RECEIVE);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "An error occurred", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void setUpList() {
        database
                .collection(Request.REQUESTS)
                .whereEqualTo(Request.BORROWER, username)
                .whereEqualTo(Request.IS_ACCEPTED, Boolean.valueOf(true).toString())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            books.clear();
                            for (QueryDocumentSnapshot doc : task.getResult()) {
                                String requestID = doc.getId();
                                String bookID = doc.getData().get(Request.BOOK).toString();
                                requestIDHash.put(bookID, requestID);
                                getAcceptedBook(bookID);
                            }
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "An error occurred", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @androidx.annotation.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_RECEIVE) {
            if (resultCode == CommonStatusCodes.SUCCESS && data != null) {
                this.setUpList();
                this.getActivity().recreate();
            }
        }
    }

    public void getAcceptedBook(String id) {
        database
                .collection(Book.BOOKS)
                .document(id)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot doc = task.getResult();
                            if (doc != null && doc.exists()) {
                                String lentTo = doc.getData().get(Book.LENT_TO).toString();

                                if (lentTo.isEmpty()) {
                                    String bookID = doc.getId();
                                    String title = doc.getData().get(Book.TITLE).toString();
                                    String author = doc.getData().get(Book.AUTHOR).toString();
                                    String isbn = doc.getData().get(Book.ISBN).toString();
                                    String owner = doc.getData().get(Book.OWNER).toString();
                                    String statusString = doc.getData().get(Book.STATUS).toString();
                                    int status = Integer.parseInt(statusString);
                                    String imageUrl = doc.getData().get(Book.IMAGE_URL).toString();

                                    Book book = new Book(isbn, title, author, owner, status, lentTo, imageUrl);
                                    books.add(book);
                                    listAdapter.notifyDataSetChanged();

                                    bookIDHash.put(isbn, bookID);
                                }
                            }
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "An error occurred", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
