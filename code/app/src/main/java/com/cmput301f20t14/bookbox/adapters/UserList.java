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

import com.bumptech.glide.Glide;
import com.cmput301f20t14.bookbox.R;
import com.cmput301f20t14.bookbox.entities.Book;
import com.cmput301f20t14.bookbox.entities.User;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class UserList extends ArrayAdapter<User> {
    private ArrayList<User> users;
    private Context context;
    private StorageReference storageReference;

    private static class ViewHolder {
        TextView username;
        TextView email;
        TextView phone;
        ImageView bookImageView;
    }

    public UserList(Context context, ArrayList<User> Users) {
        super(context, 0, Users);
        this.users = users;
        this.context = context;
    }


    

    public void downloadImage(final ImageView imageView, Book book) {
        String imageUrl = book.getPhotoUrl();


        if (imageUrl != "") {

            Uri uri = Uri.parse(imageUrl);

            Glide.with(imageView.getContext())
                    .load(uri)
                    .into(imageView);

        } else {
            imageView.setImageResource(R.drawable.ic_custom_image);
        }

    }
}
