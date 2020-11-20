package com.cmput301f20t14.bookbox.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.cmput301f20t14.bookbox.R;

public class UpdateEmailFragment extends DialogFragment {
    private EditText newEmailText;
    private UpdateEmailFragmentInteractionListener listener;

    public interface UpdateEmailFragmentInteractionListener {
        void onOkPressed(String email);
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        if (context instanceof UpdateEmailFragmentInteractionListener){
            listener = (UpdateEmailFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState){
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_update_email, null);
        newEmailText = view.findViewById(R.id.update_email_editText);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        return builder
                .setView(view)
                .setTitle(R.string.fragment_update_email_title)
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(R.string.update, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String email = newEmailText.getText().toString().trim();
                        listener.onOkPressed(email);
                    }
                }).create();
    }
}