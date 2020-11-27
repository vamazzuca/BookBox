package com.cmput301f20t14.bookbox.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.cmput301f20t14.bookbox.R;
import com.cmput301f20t14.bookbox.entities.User;

/**
 * This fragment updates the email of the user in the activity
 * to which it is attached to. In this case, it's the ProfileActivity
 * @author Carter Sabadash
 * @see com.cmput301f20t14.bookbox.activities.ProfileActivity
 */

public class UpdateEmailFragment extends DialogFragment {
    private EditText newEmailText;
    private EditText passwordText;
    private OnFragmentInteractionListener listener;

    public interface OnFragmentInteractionListener {
        void emailUpdatePressed(String email, String password);
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener){
            listener = (OnFragmentInteractionListener) context;
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
        passwordText = view.findViewById(R.id.update_email_password);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        return builder
                .setView(view)
                .setTitle(R.string.fragment_update_email_title)
                .setNegativeButton(R.string.cancel_2, null)
                .setPositiveButton(R.string.update, null) // override in onStart()
                .create();
    }

    @Override
    public void onStart(){
        super.onStart();

        AlertDialog alertDialog = (AlertDialog) getDialog();
        if (alertDialog != null) {
            // we don't want to allow dialog to close if the user clicks update and the
            // passwords do not match
            Button updateButton = alertDialog.getButton(Dialog.BUTTON_POSITIVE);
            updateButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String email = newEmailText.getText().toString().trim();
                    String password = passwordText.getText().toString().trim();
                    if (password.length() == 0) {
                        passwordText.setError("Required");
                        passwordText.requestFocus();
                    } else if (email.length() != 0) {
                        if (!User.isEmailSyntaxValid(email)) {
                            newEmailText.setError("Invalid email");
                            newEmailText.requestFocus();
                        } else {
                            listener.emailUpdatePressed(email, password);
                        }
                    } else {
                        newEmailText.setError("Required");
                        newEmailText.requestFocus();
                    }
                }
            });
        }
    }

    public void incorrectPassword(){
        passwordText.setError("Incorrect");
        passwordText.requestFocus();
    }

    public void emailUpdate(boolean success) {
        if (success) {
            dismiss();
        } else {
            newEmailText.setError("Email already in use");
            newEmailText.requestFocus();
        }
    }
}