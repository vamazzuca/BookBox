package com.cmput301f20t14.bookbox.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.cmput301f20t14.bookbox.R;
import com.cmput301f20t14.bookbox.entities.Request;

import java.util.ArrayList;

public class RequestList extends ArrayAdapter<Request> {
    private Context context;
    private ArrayList<Request> requests;

    public RequestList(Context context, ArrayList<Request> requests) {
        super(context, 0, requests);
        this.context = context;
        this.requests = requests;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.requests_list_content, parent, false);
        }

        TextView requester = (TextView) view.findViewById(R.id.view_request_requester);
        TextView date = (TextView) view.findViewById(R.id.view_request_date);

        Request request = requests.get(position);
        requester.setText(request.getBorrower());
        date.setText(request.getDate());

        return view;
    }
}
