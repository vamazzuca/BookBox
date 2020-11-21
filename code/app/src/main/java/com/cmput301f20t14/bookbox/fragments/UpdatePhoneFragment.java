package com.cmput301f20t14.bookbox.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.cmput301f20t14.bookbox.R;

public class UpdatePhoneFragment extends DialogFragment {
    private EditText newPhoneText;
    private OnFragmentInteractionListener listener;

    public interface OnFragmentInteractionListener {
        void onPhoneUpdated(String phone);
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
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_update_phone, null);
        newPhoneText = view.findViewById(R.id.update_phone_editText);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        return builder
                .setView(view)
                .setTitle(R.string.fragment_update_phone_title)
                .setNegativeButton(R.string.cancel, null)
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
                    String phone = newPhoneText.getText().toString().trim();
                    if (phone.length() != 0) {
                        listener.onPhoneUpdated(phone);
                        dismiss();
                    }
                    newPhoneText.setError("Required");
                    newPhoneText.requestFocus();
                }
            });
        }
    }
}