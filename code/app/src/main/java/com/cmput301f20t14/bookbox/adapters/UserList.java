package com.cmput301f20t14.bookbox.adapters;

import android.content.Context;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;

import com.cmput301f20t14.bookbox.entities.Book;
import com.cmput301f20t14.bookbox.entities.User;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class UserList extends ArrayAdapter<User> {
    private ArrayList<User> users;
    private Context context;
    private StorageReference storageReference;

    public UserList(Context context, ArrayList<User> Users) {
        super(context, 0, Users);
        this.users = users;
        this.context = context;
    }
}
