package com.cmput301f20t14.bookbox;

import android.net.Uri;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * This class contains handles making the POST request to the google books
 * library to retrieve the book information from a given ISBN
 * @author Olivier Vadiavaloo
 * @version 2020.11.22
 */

public class NetworkUtils {
    protected static final String BASE_URL = "https://www.googleapis.com/books/v1/volumes?";
    protected static final String PARAM = "q";
    protected static final String MAX_RESULTS = "maxResults";
    protected static final String PRINT_TYPE = "printType";

    public static String getBookInfo(String query) {
        String jsonString = null;
        HttpURLConnection connection = null;
        BufferedReader reader = null;

        try {
            // Build the URI object
            Uri uri = Uri.parse(BASE_URL)
                    .buildUpon()
                    .appendQueryParameter(PARAM, query)
                    .appendQueryParameter(MAX_RESULTS, "1")
                    .appendQueryParameter(PRINT_TYPE, "books")
                    .build();

            // Get the URL object
            URL request = new URL(uri.toString());

            // Establish a connection
            connection = (HttpURLConnection) request.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();

            // Get the input stream and initialize a BufferedReader
            InputStream inputStream = connection.getInputStream();
            reader = new BufferedReader(new InputStreamReader(inputStream));

            // Use a string builder to build the json string
            // which will be parsed into a JSON object later
            StringBuilder stringBuilder = new StringBuilder();
            String line = reader.readLine();
            while (line != null) {
                stringBuilder.append(line);
                stringBuilder.append("\n");
                line = reader.readLine();
            }

            if (stringBuilder.length() == 0) {
                return null;
            }
            jsonString = stringBuilder.toString();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // Close the connection
            if (connection != null) {
                connection.disconnect();
            }

            // Close the reader
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return jsonString;
    }
}
