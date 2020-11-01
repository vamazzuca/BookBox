package com.cmput301f20t14.bookbox;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.cmput301f20t14.bookbox.entities.Image;

import java.net.URI;

public class ViewImageFragment extends DialogFragment {
    private ImageView bookImageView;
    private OnFragmentInteractionListener listener;

    public interface OnFragmentInteractionListener{

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

    static ViewImageFragment newInstance(Image image) {
        Bundle args = new Bundle();
        args.putSerializable("image", image);

        ViewImageFragment fragment = new ViewImageFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.view_image_fragment_layout, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());


        return builder.create();
    }

}
