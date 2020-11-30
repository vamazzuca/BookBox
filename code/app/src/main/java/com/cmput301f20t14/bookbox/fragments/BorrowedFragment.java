package com.cmput301f20t14.bookbox.fragments;

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
import com.cmput301f20t14.bookbox.activities.HandOverActivity;
import com.cmput301f20t14.bookbox.adapters.BookList;
import com.cmput301f20t14.bookbox.entities.Book;
import com.cmput301f20t14.bookbox.entities.Request;
import com.cmput301f20t14.bookbox.entities.User;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;

import javax.annotation.Nullable;

import static android.view.View.GONE;

/**
 * This fragment is one of the tabs of the tabbed activity: listsActivity
 * It contains a listview of the requests made by the user that have been borrowed.
 * @author Olivier Vadiavaloo
 * @see com.cmput301f20t14.bookbox.activities.ListsActivity
 */

public class BorrowedFragment extends Fragment {
    private static final int REQUEST_RETURN = 8007;
    private BookList listAdapter;
    private ArrayList<Book> books;
    private ListView listView;
    private String username;
    private FirebaseFirestore database;
    private HashMap<String, String> bookIdHash;

    public static BorrowedFragment newInstance(String usernameArg) {
        Bundle args = new Bundle();
        args.putString(User.USERNAME, usernameArg);

        BorrowedFragment fragment = new BorrowedFragment();
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
        bookIdHash = new HashMap<>();

        setUpList();
        listView.setAdapter(listAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Book book = listAdapter.getItem(position);
                String bookID = bookIdHash.get(book.getIsbn());
                getRequestInfo(bookID, book, view);
            }
        });

        return view;
    }

    /**
     * Gets the details of the request that is associated with the book
     * @param bookID The firestore id of the Book
     * @param book The Book
     * @param view The View
     */
    public void getRequestInfo(final String bookID, final Book book, final View view) {
        database
                .collection(Request.REQUESTS)
                .whereEqualTo(Request.BOOK, bookID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            for (QueryDocumentSnapshot doc : task.getResult()) {
                                String requestID = doc.getId();
                                String owner = doc.getData().get(Request.OWNER).toString();
                                String borrower = doc.getData().get(Request.BORROWER).toString();
                                String isAccepted = doc.getData().get(Request.IS_ACCEPTED).toString();
                                String latLng = doc.getData().get(Request.LAT_LNG).toString();
                                String date = doc.getData().get(Request.DATE).toString();

                                Request request = new Request(borrower, owner, book, date, Boolean.valueOf(isAccepted), latLng);

                                if (book.getStatus() == Book.BORROWED) {
                                    Intent intent = new Intent(view.getContext(), HandOverActivity.class);
                                    intent.putExtra(User.USERNAME, username);
                                    intent.putExtra(Book.ID, bookID);
                                    intent.putExtra(Request.ID, requestID);
                                    Bundle bundle = new Bundle();
                                    bundle.putSerializable("REQUEST_OBJECT", request);
                                    bundle.putSerializable("BOOK", book);
                                    intent.putExtras(bundle);
                                    startActivityForResult(intent, REQUEST_RETURN);
                                }
                            }
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @androidx.annotation.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_RETURN) {
            if (resultCode == CommonStatusCodes.SUCCESS && data != null) {
                this.setUpList();
            }
        }
    }

    /**
     * Set up the list of borrowed books
     */
    public void setUpList() {
        database
                .collection(Book.BOOKS)
                .whereEqualTo(Book.LENT_TO, username)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            books.clear();
                            for (QueryDocumentSnapshot doc : task.getResult()) {
                                String id = doc.getId();
                                String title = doc.getData().get(Book.TITLE).toString();
                                String author = doc.getData().get(Book.AUTHOR).toString();
                                String isbn = doc.getData().get(Book.ISBN).toString();
                                String owner = doc.getData().get(Book.OWNER).toString();
                                String statusString = doc.getData().get(Book.STATUS).toString();
                                int status = Integer.parseInt(statusString);
                                String lentTo = doc.getData().get(Book.LENT_TO).toString();
                                String imageUrl = doc.getData().get(Book.IMAGE_URL).toString();

                                Book book = new Book(isbn, title, author, owner, status, lentTo, imageUrl);
                                books.add(book);
                                listAdapter.notifyDataSetChanged();

                                bookIdHash.put(isbn, id);
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
