package com.cmput301f20t14.bookbox;

import android.os.AsyncTask;
import android.widget.EditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.ref.WeakReference;

/**
 * This class fetches the title and the author of a book
 * based an isbn given by the user (through scanning).
 * The class parses the JSON object returned by the post request
 * to obtain the title and the author of the book
 */

public class FetchBook extends AsyncTask<String, Void, String> {
    private WeakReference<EditText> titleEditText;
    private WeakReference<EditText> authorEditText;

    public FetchBook(EditText title, EditText author) {
        this.titleEditText = new WeakReference<>(title);
        this.authorEditText = new WeakReference<>(author);
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

        try {
            JSONObject jsonObj = new JSONObject(s);
            JSONArray array = jsonObj.getJSONArray("items");

            int count = 0;
            String author = null;
            String title = null;
            while ((count < array.length()) &&
                    (author == null && title == null)) {
                JSONObject book = array.getJSONObject(count);
                JSONObject volumeInfo = book.getJSONObject("volumeInfo");

                try {
                    title = volumeInfo.getString("title");
                    JSONArray authors = volumeInfo.getJSONArray("authors");
                    for (int i = 0; i < authors.length(); i++) {
                        author = authors.getString(i);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                count += 1;
            }

            if (title != null && author != null) {
                titleEditText.get().setText(title);
                authorEditText.get().setText(author);
            } else {
                titleEditText.get().setText(R.string.no_results);
                authorEditText.get().setText(R.string.no_results);
            }

        } catch (JSONException e) {
            titleEditText.get().setText(R.string.no_results);
            authorEditText.get().setText(R.string.no_results);
            e.printStackTrace();
        }
    }

    @Override
    protected String doInBackground(String... strings) {
        return NetworkUtils.getBookInfo(strings[0]);
    }
}
