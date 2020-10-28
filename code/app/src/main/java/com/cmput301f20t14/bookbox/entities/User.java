package com.cmput301f20t14.bookbox.entities;

import java.util.ArrayList;

public class User {
    public static final String USERS = "USERS";
    public static final String USERNAME = "USERNAME";
    public static final String PASSWORD = "PASSWORD";
    public static final String PHONE = "PHONE";
    public static final String EMAIL = "EMAIL";
    public static final String OWNED_BOOKS = "OWNED_BOOKS";
    public static final String BORROWED_BOOKS = "BORROWED_BOOKS";

    private String username;
    private String password;
    private String phone;
    private String email;
    private ArrayList<Book> ownedBooks;
    private ArrayList<Book> borrowedBooks;


}
