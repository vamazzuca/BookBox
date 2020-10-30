package com.cmput301f20t14.bookbox.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.cmput301f20t14.bookbox.R;
import com.cmput301f20t14.bookbox.entities.Book;

import java.util.ArrayList;

public class BookList extends ArrayAdapter<Book> {
    private ArrayList<Book> books;
    private Context context;

    public BookList(Context context, ArrayList<Book> books) {
        super(context, 0, books);
        this.books = books;
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.owned_book_content, parent, false);
        }

        Book book = books.get(position);

        TextView owner = (TextView) view.findViewById(R.id.list_content_owner);
        TextView author = (TextView) view.findViewById(R.id.list_content_author);
        TextView title = (TextView) view.findViewById(R.id.list_content_title);
        TextView isbn = (TextView) view.findViewById(R.id.list_content_isbn);
        TextView status = (TextView) view.findViewById(R.id.list_content_status);

        owner.setText(book.getOwner());
        author.setText(book.getAuthor());
        title.setText(book.getTitle());
        isbn.setText(book.getIsbn());
        status.setText(String.valueOf(book.getStatus()));

        return view;
    }
}
