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
        ImageView userImageView;
    }

    public UserList(Context context, ArrayList<User> users) {
        super(context, 0, users);
        this.users = users;
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        storageReference = FirebaseStorage.getInstance().getReference();

        UserList.ViewHolder holder;
        if (view == null) {
            holder = new UserList.ViewHolder();
            view = LayoutInflater.from(context).inflate(R.layout.user_list_content, parent, false);
            holder.username = (TextView) view.findViewById(R.id.list_content_username);
            holder.email = (TextView) view.findViewById(R.id.list_content_email);
            holder.phone = (TextView) view.findViewById(R.id.list_content_phone);
            holder.userImageView = (ImageView) view.findViewById(R.id.list_user_image);

            view.setTag(holder);
        } else {
            holder = (UserList.ViewHolder) view.getTag();
        }

        User user = users.get(position);

        holder.username.setText(user.getUsername());
        holder.email.setText(user.getEmail());
        holder.phone.setText(user.getPhone());


        downloadImage(holder.userImageView, user);
        return view;
    }


    public void downloadImage(final ImageView imageView, User user) {
        String imageUrl = user.getPhotoUrl();


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
