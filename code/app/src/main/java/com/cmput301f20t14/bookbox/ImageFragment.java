package com.cmput301f20t14.bookbox;

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

import com.cmput301f20t14.bookbox.entities.Image;
import com.squareup.picasso.Picasso;

public class ImageFragment extends DialogFragment {
    private ImageView bookImageView;
    private OnFragmentInteractionListener listener;
    private Uri imageUri;

    public interface OnFragmentInteractionListener{
        void onUpdateImage();
        void onDeleteImage();
    }

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

    public static ImageFragment newInstance(Image image) {
        Bundle args = new Bundle();
        args.putSerializable("image", image);

        ImageFragment fragment = new ImageFragment();
        fragment.setArguments(args);
        return fragment;
    }

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
            //bookImageView.setImageURI(imageUri);
            Picasso.get().load(imageUri).into(bookImageView);

            return builder
                    .setView(view)
                    .setNeutralButton("Cancel", null)
                    .setPositiveButton("Change", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int i) {
                            listener.onUpdateImage();
                        }
                    }).create();
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
