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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.cmput301f20t14.bookbox.R;
import com.cmput301f20t14.bookbox.entities.Image;
import com.squareup.picasso.Picasso;

/**
 * A Dialog Fragment for viewing an image
 * and a confirmation message for deleting an
 * image. It also allows anyone to view an image without being able to change it
 * References: Lab 3 Instructions - FragmentsFile from the University of Alberta
 * @author Alex Mazzuca
 * @author Carter Sabadash
 * @version 2020.11.29
 */
public class ImageFragment extends DialogFragment {
    private ImageView bookImageView;
    private OnFragmentInteractionListener listener;
    private Uri imageUri;

    /**
     * An interface for deleting images and updating images
     * @author Alex Mazzuca
     * @version 2020.11.04
     */
    public interface OnFragmentInteractionListener{
        void onUpdateImage();
        void onDeleteImage();
    }

    /**
     * Attach fragment onto activity that implements ImageFragment
     * @author Alex Mazzuca
     * @version 2020.11.04
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener){
            listener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    /**
     * Creates a new instance the takes an image from the attached
     * activity an bundles it to be used later for the imageView.
     * @author Alex Mazzuca
     * @version 2020.11.04
     * @param image An image object
     */
    public static ImageFragment newInstance(Image image) {
        Bundle args = new Bundle();
        args.putSerializable("image", image);
        args.putSerializable("owner", true);

        ImageFragment fragment = new ImageFragment();
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Creates a new instance that takes an image from the attached activity
     * and bundles it to be used later for the imageView. This creates a
     * fragment for people to view the image (and doesn't provide the edit
     * functionality)
     * @param image
     * @param isOwner
     */
    public static ImageFragment newInstance(Image image, boolean isOwner) {
        Bundle args = new Bundle();
        args.putSerializable("image", image);
        args.putSerializable("owner", isOwner);

        ImageFragment fragment = new ImageFragment();
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Sets the Fragment to the proper xml layout depending on if the user is deleting or viewing and
     * Image. If an image is passed though to the fragment then the Fragment will show a view where the user can
     * change the image. If no image is passed though then the fragment will will a confirmation to delete
     * the image.
     * @author Alex Mazzuca
     * @version 2020.11.04
     * @return Dialog fragment builder
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        LayoutInflater LayoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        if (getArguments() != null) {
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.view_image_fragment_layout, null);
            bookImageView = view.findViewById(R.id.imageViewFrag);
            final Image image = (Image) getArguments().getSerializable("image");
            imageUri = image.getUri();

            if (imageUri == null){
                bookImageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_custom_image));
            } else{
                Picasso.get().load(imageUri).into(bookImageView);
            }
            // if the uwer is the owner enable the positive button, otherwise don't include it
            if ((boolean) getArguments().getSerializable("owner")) {
                return builder
                        .setView(view)
                        .setNeutralButton("Cancel", null)
                        .setPositiveButton("Change", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int i) {
                                listener.onUpdateImage();
                            }
                        }).create();
            } else {
                return builder
                        .setView(view)
                        .setNeutralButton("Back", null)
                        .create();
            }
        } else{
            View viewDelete = LayoutInflater.from(getActivity()).inflate(R.layout.delete_image_fragment_layout, null);
            return builder
                    .setView(viewDelete)
                    .setNeutralButton("Cancel", null)
                    .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            listener.onDeleteImage();
                        }
                    })
                    .create();
        }

    }

}
