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

    public Notification(String userField, Book book, String type, String date) {
        this.userField = userField;
        this.book = book;
        this.type = type;
        this.date = date;
    }

    public String getUserField() {
        return userField;
    }

    public void setUserField(String userField) {
        this.userField = userField;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
