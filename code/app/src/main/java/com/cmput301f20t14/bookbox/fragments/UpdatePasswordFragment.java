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

public class UpdatePasswordFragment extends DialogFragment {
    private EditText newPassword;
    private EditText confirmPassword;
    private UpdatePasswordFragmentInteractionListener listener;

    public interface UpdatePasswordFragmentInteractionListener {
        void onOkPressed(String password);
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        if (context instanceof UpdatePasswordFragmentInteractionListener){
            listener = (UpdatePasswordFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState){
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_update_password, null);
        newPassword = view.findViewById(R.id.update_password_password_editText);
        confirmPassword = view.findViewById(R.id.update_password_password_confirm_editText);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        return builder
                .setView(view)
                .setTitle(R.string.fragment_update_password_title)
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(R.string.update, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String password = newPassword.getText().toString().trim();
                        String password2 = confirmPassword.getText().toString().trim();

                        if (password.equals(password2)) {
                            listener.onOkPressed(password);
                        }
                        // passwords do not match -> show error
                        confirmPassword.setError("Passwords must match");
                        confirmPassword.requestFocus();
                    }
                }).create();
    }
}