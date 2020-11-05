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

public class SearchList extends ArrayAdapter<Book> {

    private ArrayList<Book> searchList;
    private Context context;

    public SearchList(Context context, ArrayList<Book> searchList){
        super(context,0,searchList);
        this.searchList = searchList;
        this.context = context;

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;

        if(view == null){
            view = LayoutInflater.from(context).inflate(R.layout.search_content,parent,false);
        }

        Book book = searchList.get(position);

        TextView title = view.findViewById(R.id.search_title_text);
        TextView author = view.findViewById(R.id.search_author_text);
        TextView isbn = view.findViewById(R.id.search_isbn_text);
        TextView owner = view.findViewById(R.id.search_owner_text);
        TextView status = view.findViewById(R.id.search_status_text);

        owner.setText(book.getOwner());
        author.setText(book.getAuthor());
        title.setText(book.getTitle());
        isbn.setText(book.getIsbn());

        CharSequence statusText = book.getStatusString();

        if (book.getStatus() == Book.BORROWED) {
            statusText = statusText + " (" + book.getLentTo() + ")";
        }

        status.setText(statusText);

        return view;
    }
}
