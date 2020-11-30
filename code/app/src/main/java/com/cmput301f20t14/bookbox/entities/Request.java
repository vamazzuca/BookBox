package com.cmput301f20t14.bookbox.entities;

import android.widget.ScrollView;

import com.google.android.gms.maps.model.LatLng;
import java.io.Serializable;

/**
 * This entity class models a request by a user
 * to another user to borrow a specified book from
 * the latter's collection.
 * @author Olivier Vadiavaloo
 * @version 2020.11.10
 */

public class Request implements Serializable {
    public static final String REQUESTS = "REQUESTS";
    public static final String ID = "REQUEST_ID";
    public static final String BORROWER = "BORROWER";
    public static final String  OWNER = "OWNER";
    public static final String DATE = "DATE";
    public static final String BOOK = "BOOK";
    public static final String IS_ACCEPTED = "IS_ACCEPTED";
    public static final String LAT_LNG = "LOCATION";
    private String borrower;
    private String owner;
    private Book book;
    private String date;
    private String latLng;
    private Boolean isAccepted;

    /**
     * Constructor of the Request object
     * @param borrower   username of the one making the request
     * @param owner      username of the one owning the requested book
     * @param book       the requested book
     * @param isAccepted true if request was accepted and false otherwise
     * @param latLng     set location when request is accepted
     */
    public Request(String borrower, String owner, Book book, String date, Boolean isAccepted, String latLng) {
        this.borrower = borrower;
        this.owner = owner;
        this.book = book;
        this.date = date;
        this.isAccepted = isAccepted;
        this.latLng = latLng;
    }

    /**
     * Accept the request if the passed in username is
     * the one who made the initial request
     * @param  username username of the one trying to accept the request
     * @return boolean True if accepting action was successful, false otherwise
     */
    public boolean acceptRequest(String username) {
        // If the passed in username matches that of
        // the owner and the book is not accepted already,
        // set the book status to accepted
        if (username.equals(this.owner) &&
            this.book.getStatus() != Book.ACCEPTED) {
            this.book.setStatus(Book.ACCEPTED);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Static method that parses a string into
     * Latlng object
     * @param  latLngString string to be parsed
     * @return latLng       LatLng object obtained from parsing
     *         null         null if parsing failed
     */
    public static LatLng parseLatLngString(String latLngString) {
        try {
            double latitude = Double.parseDouble(latLngString.split(",")[0]);
            double longitude = Double.parseDouble(latLngString.split(",")[1]);
            return new LatLng(latitude, longitude);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Gets the date the request was made
     * @return date on which the request was made
     */
    public String getDate() { return date;}

    /**
     * Sets the date the request was made
     */
    public void setDate(String date) {
        this.date = date;
    }

    /**
     * Gets the borrower
     * @return borrower username of the borrower
     */
    public String getBorrower() {
        return borrower;
    }

    /**
     * Sets the borrower
     * @param borrower username of the borrower
     */
    public void setBorrower(String borrower) {
        this.borrower = borrower;
    }

    /**
     * Gets the owner of the requested book
     * @return owner username of the owner
     */
    public String getOwner() {
        return owner;
    }

    /**
     * Sets the owner of the requested book
     * @param owner username of the owner
     */
    public void setOwner(String owner) {
        this.owner = owner;
    }

    /**
     * Gets the requested book object
     * @return book Book object that the borrower requested
     */
    public Book getBook() {
        return book;
    }

    /**
     * Sets the requested book object
     * @param book Book object that the borrower requested
     */
    public void setBook(Book book) {
        this.book = book;
    }

    /**
     * Gets whether or not the request is accepted
     * @return If the request is accepted
     */
    public Boolean getAccepted() {
        return isAccepted;
    }

    /**
     * Set the accepted state of the request
     * @param accepted The new state of the request
     */
    public void setAccepted(Boolean accepted) {
        isAccepted = accepted;
    }

    /**
     * Gets a string of the location associated with the request
     * @return The location associated with the request Latitude and Longitude
     */
    public String getLatLng() {
        return latLng;
    }

    /**
     * Set the location to transfer the book
     * @param latLng The latitude and longitude
     */
    public void setLatLng(String latLng) {
        this.latLng = latLng;
    }
}
