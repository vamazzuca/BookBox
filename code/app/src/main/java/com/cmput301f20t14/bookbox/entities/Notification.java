package com.cmput301f20t14.bookbox.entities;

/**
 * This class represents a notification object
 * @author Olivier Vadiavaloo
 * @version 2020.11.22
 */

public class Notification {
    public static final String NOTIFICATIONS = "NOTIFICATIONS";
    public static final String ID = "ID";
    public static final String USER = "USER";
    public static final String BOOK = "BOOK";
    public static final String TYPE = "TYPE";
    public static final String REQUEST = "REQUEST_ID";
    public static final String DATE = "DATE";
    public static final String RETURN = "RETURN";
    public static final String ACCEPT = "ACCEPT REQUEST";
    public static final String BOOK_REQUEST = "BOOK REQUEST";
    private String userField;
    private Book book;
    private String type;
    private String date;
    private String request;

    /**
     * Get request ID string
     * @return request
     */
    public String getRequest() {
        return request;
    }

    /**
     * Set request ID string
     * @param request request ID in database
     */
    public void setRequest(String request) {
        this.request = request;
    }

    /**
     * Constructor of the Notification class
     * @param userField username of the user
     * @param book      book associated with the notification
     * @param type      type of notification
     * @param date      date of the notification
     * @param request   request id of the request in the database
     */
    public Notification(String userField, Book book, String type, String date, String request) {
        this.userField = userField;
        this.book = book;
        this.type = type;
        this.date = date;
        this.request = request;
    }

    /**
     * Get the user who indirectly initiated the notification
     * @return userField username of the user who initiated
     *                   the notification
     */
    public String getUserField() {
        return userField;
    }

    /**
     * Set the username of the user who initiated the notification
     * @param userField username of the user who initiated the notification
     */
    public void setUserField(String userField) {
        this.userField = userField;
    }

    /**
     * Book attribute getter
     * @return book Book object associated with notification
     */
    public Book getBook() {
        return book;
    }

    /**
     * Setter for the book attribute
     * @param book Book object associated with the notification
     */
    public void setBook(Book book) {
        this.book = book;
    }

    /**
     * Getter for the type attribute
     * @return type the type of notification
     */
    public String getType() {
        return type;
    }

    /**
     * Setter for the type attribute
     * @param type the type of the notification
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Getter for the date attribute
     * @return date the date the notification was sent
     */
    public String getDate() {
        return date;
    }

    /**
     * Setter for the date attribute
     * @param date the date the notification was sent
     */
    public void setDate(String date) {
        this.date = date;
    }
}
