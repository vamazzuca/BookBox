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

/**
 * This fragment updates the password of the user in the activity
 * to which it is attached to. In this case, it's the ProfileActivity
 * @author Carter Sabadash
 * @see com.cmput301f20t14.bookbox.activities.ProfileActivity
 */

public class UpdatePasswordFragment extends DialogFragment {
    private EditText oldPassword;
    private EditText newPassword;
    private EditText confirmPassword;
    private OnFragmentInteractionListener listener;

    public interface OnFragmentInteractionListener {
        void passwordUpdatePressed(String oldPassword, String newPassword);
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
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_update_password, null);
        oldPassword = view.findViewById(R.id.update_password_old_poassword_editText);
        newPassword = view.findViewById(R.id.update_password_password_editText);
        confirmPassword = view.findViewById(R.id.update_password_password_confirm_editText);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        return builder
                .setView(view)
                .setTitle(R.string.fragment_update_password_title)
                .setNegativeButton(R.string.cancel_2, null)
                .setPositiveButton(R.string.update, null) // override in onStart
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
                    String password = newPassword.getText().toString().trim();
                    String password2 = confirmPassword.getText().toString().trim();
                    if (oldPassword.length() == 0) {
                      oldPassword.setError("Required");
                      oldPassword.requestFocus();
                    } else if (password.equals(password2) && password.length() != 0) {
                        listener.passwordUpdatePressed(oldPassword.getText().toString().trim(), password);
                    } else {
                        // passwords do not match -> show error
                        confirmPassword.setError("Passwords must match");
                        confirmPassword.requestFocus();
                    }
                }
            });
        }
    }

    /**
     * This allows us to close the fragment when the oldPassword is correct
     *      and the password has been updated, otherwise we can continue interacting
     *      with the fragment
     * @param verified If the oldPassword is correct or not
     */
    public void verify(boolean verified) {
        if (verified) {
            dismiss();
        } else {
            oldPassword.setError("Invalid");
            oldPassword.requestFocus();
        }
    }

    /**
     * Call if the new password is too weak. This will tell the user to enter a stronger password
     */
    public void weakPassword() {
        newPassword.setError("Weak Password");
        newPassword.requestFocus();
    }
}