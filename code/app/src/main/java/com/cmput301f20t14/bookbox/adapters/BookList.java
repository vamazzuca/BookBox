package com.cmput301f20t14.bookbox.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import com.cmput301f20t14.bookbox.R;
import com.cmput301f20t14.bookbox.entities.Book;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * This is the adapter used to represent a book in
 * a listview with each row having the layout described in
 * owned_book_content.xml
 * @author Olivier Vadiavaloo
 * @version 2020.11.04
 */

public class BookList extends ArrayAdapter<Book> {
    private ArrayList<Book> books;
    private Context context;
    private StorageReference storageReference;
    private String imageUrlList;

    public BookList(Context context, ArrayList<Book> books) {
        super(context, 0, books);
        this.books = books;
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        storageReference = FirebaseStorage.getInstance().getReference();
        imageUrlList = "";

        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.owned_book_content, parent, false);
        }

        Book book = books.get(position);

        TextView owner = (TextView) view.findViewById(R.id.list_content_owner);
        TextView author = (TextView) view.findViewById(R.id.list_content_author);
        TextView title = (TextView) view.findViewById(R.id.list_content_title);
        TextView isbn = (TextView) view.findViewById(R.id.list_content_isbn);
        TextView status = (TextView) view.findViewById(R.id.list_content_status);
        final ImageView bookImageView = view.findViewById(R.id.list_content_image);

        owner.setText(book.getOwner());
        author.setText(book.getAuthor());
        title.setText(book.getTitle());
        isbn.setText(book.getIsbn());

        imageUrlList = book.getPhotoUrl();

        if (!imageUrlList.isEmpty()) {
            StorageReference imageRef = storageReference.child(imageUrlList);

            imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Picasso.get().load(uri).into(bookImageView);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    //Handle any errors
                }
            });
        } else {
            bookImageView.setImageDrawable(ResourcesCompat.getDrawable(context.getResources(), R.drawable.ic_custom_image, null));
        }

        CharSequence statusText = book.getStatusString();

        if (book.getStatus() == Book.BORROWED) {
            statusText = statusText + " (" + book.getLentTo() + ")";
        }

        status.setText(statusText);

        return view;
    }
}
