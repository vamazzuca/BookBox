package com.cmput301f20t14.bookbox.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.bumptech.glide.Glide;
import com.cmput301f20t14.bookbox.R;
import com.cmput301f20t14.bookbox.entities.Image;
import com.cmput301f20t14.bookbox.entities.User;
import com.squareup.picasso.Picasso;

/**
 * A Dialog Fragment for viewing a user profile.
 * References: Lab 3 Instructions - FragmentsFile from the University of Alberta
 * @author Alex Mazzuca
 * @version 2020.11.25
 */
public class ProfileFragment extends DialogFragment {
    private ImageView profileImageView;
    private ProfileFragment.OnFragmentInteractionListener listener;
    private String imageUrl;
    private TextView usernameView;
    private TextView emailView;
    private TextView phoneView;

    public interface OnFragmentInteractionListener{
    }

    /**
     * Attach fragment onto activity that implements ProfileFragment
     * @author Alex Mazzuca
     * @version 2020.11.25
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ProfileFragment.OnFragmentInteractionListener){
            listener = (ProfileFragment.OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    /**
     * Creates a new instance the takes an user from the attached
     * activity an bundles it to be used later for the profile view.
     * @author Alex Mazzuca
     * @version 2020.11.25
     * @param user A user object
     */
    public static ProfileFragment newInstance(User user) {
        Bundle args = new Bundle();
        args.putSerializable("user", user);

        ProfileFragment fragment = new ProfileFragment();
        fragment.setArguments(args);
        return fragment;
    }


    /**
     * Sets the Fragment to the proper xml layout to view a user profile.
     * @author Alex Mazzuca
     * @version 2020.11.25
     * @return Dialog fragment builder
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.view_profile_fragment_layout, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        final User user = (User) getArguments().getSerializable("user");


        usernameView = view.findViewById(R.id.profile_username_fragment);
        phoneView = view.findViewById(R.id.profile_phone_fragment);
        emailView = view.findViewById(R.id.profile_email_fragment);


        String username = user.getUsername();
        String phone = user.getPhone();
        String email = user.getEmail();

        usernameView.setText("Username: " + username);
        phoneView.setText("Phone: " + phone);
        emailView.setText("Email: " + email);

        profileImageView = view.findViewById(R.id.profile_imageView_fragment);

        imageUrl = user.getPhotoUrl();

        //Download Image from Firebase and set it to ImageView
       if (imageUrl != "") {

           Uri uri = Uri.parse(imageUrl);
           Glide.with(profileImageView.getContext())
                   .load(uri)
                   .into(profileImageView);

        }
        return builder
                .setView(view)
                .setNeutralButton("Close", null)
                .create();


    }


}
