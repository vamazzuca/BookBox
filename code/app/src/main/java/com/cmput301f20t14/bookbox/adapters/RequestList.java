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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

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

        SimpleDateFormat parser = new SimpleDateFormat("EEE MMM d HH:mm:ss zzz yyyy", Locale.getDefault());
        SimpleDateFormat format = new SimpleDateFormat("EEE MMM d HH:mm zzz yyyy", Locale.getDefault());
        try {
            Date parsedDate = parser.parse(request.getDate());
            String formattedDate = format.format(parsedDate);
            date.setText(formattedDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return view;
    }
}
