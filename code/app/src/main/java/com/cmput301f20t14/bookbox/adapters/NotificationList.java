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
import com.cmput301f20t14.bookbox.entities.Notification;

import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * ArrayAdapter subclass used to represent a Notification object in
 * a list with layout as in notification_content.xml
 * @author  Olivier Vadiavaloo
 * @version 2020.11.22
 */

public class NotificationList extends ArrayAdapter<Notification> {
    private Context context;
    private ArrayList<Notification> notifications;

    public NotificationList(Context context, ArrayList<Notification> notifications) {
        super(context, 0, notifications);
        this.context = context;
        this.notifications = notifications;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.notification_content, parent, false);
        }

        TextView mainText = view.findViewById(R.id.notification_main_text);
        TextView book = view.findViewById(R.id.notification_book);
        TextView date = view.findViewById(R.id.notification_date);

        Notification notification = notifications.get(position);
        CharSequence bookText = notification.getBook().getTitle() + " by " + notification.getBook().getAuthor();
        book.setText(bookText);

        switch (notification.getType()) {
            case Notification.ACCEPT: {
                CharSequence text = notification.getUserField() + " accepted your request";
                mainText.setText(text);
                break;
            }
            case Notification.BOOK_REQUEST: {
                CharSequence text = "You have a request from " + notification.getUserField();
                mainText.setText(text);
                break;
            }
            case Notification.RETURN: {
                CharSequence text = notification.getUserField() + " wants to return a book";
                mainText.setText(text);
                break;
            }
            default:
                break;
        }

        SimpleDateFormat parser = new SimpleDateFormat("EEE MMM, d HH:mm:ss zzz yyyy", Locale.getDefault());
        SimpleDateFormat format = new SimpleDateFormat("EEE MMM d HH:mm zzz yyyy", Locale.getDefault());
        try {
            Date parsedDate = parser.parse(notification.getDate());
            String formattedDate = format.format(parsedDate);
            date.setText(formattedDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return view;
    }
}
