/*
 * Book.java
 *
 * Version 1.0
 *
 * Date 2020.10.22
 *
 * Copyright 2020 Team 14
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.cmput301f20t14.bookbox.entities;

import android.media.Image;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * A class that contains all the information necessary to represent a book
 * @author Carter Sabadash
 * @author Olivier Vadiavaloo
 * @version 2020.10.27
 *
 * All Getters have been implemented, but not setters (will have to also ensure that
 *     data entered is in correct format
 * The behaviour of the getLentTo() must be defined when the book Status is AVAILABLE
 *
 * Move the Status enum to a public file?
 */
public class Book implements Serializable {

    private String isbn;
    private String title;
    private String author;
    private String owner;
    private int status;
    private String lentTo;
    private Image photo;

    public static final String ID = "ID";
    public static final String BOOKS = "BOOKS";
    public static final String ISBN = "ISBN";
    public static final String TITLE = "TITLE";
    public static final String AUTHOR = "AUTHOR";
    public static final String STATUS = "STATUS";
    public static final String LENT_TO = "LENT_TO";
    public static final String OWNER = "OWNER";

    public static final int AVAILABLE = 66;
    public static final int REQUESTED = 67;
    public static final int ACCEPTED = 68;
    public static final int BORROWED = 69;

    /**
     * Constructs a book without an image
     * @param isbn The isbn of the book
     * @param title The title of the book
     * @param author The author of the book
     * @param owner The User who owns the book
     * @param status The Status of the Book (Book.Status)
     * @param lentTo Who the book is lent to (null if no-one)
     * @param photo The image association with the book
     */
    public Book(String isbn, String title, String author, String owner, int status,
                String lentTo, Image photo) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.owner = owner;
        this.status = status;
        this.lentTo = lentTo;
        this.photo = photo;
    }

    /**
     * Get the ISBN of the book
     * @return A String of the ISBN
     */
    public String getIsbn() {
        return isbn;
    }

    /**
     * Get the Title of the book
     * @return A String of the Title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Get the author of the book
     * @return A String of the Author
     */
    public String getAuthor() {
        return author;
    }

    /**
     * Gets the owner of the book
     * @return A String of who owns the book
     */
    public String getOwner() {
        return owner;
    }

    /**
     * Gets the Status of the book
     * @return A Status of the book
     */
    public int getStatus() {
        return status;
    }

    /**
     * Gets the Status of the book in string format
     * @return A string literal representing the status
     *         the book. It can be one of "Accepted",
     *         "Borrowed", "Requested" or "Available"
     */
    public String getStatusString() {
        switch (this.status) {
            case Book.ACCEPTED:
                return "Accepted";
            case Book.AVAILABLE:
                return "Available";
            case Book.BORROWED:
                return "Borrowed";
            case Book.REQUESTED:
                return "Requested";
            default:
                return "";
        }
    }

    /**
     * static version of the getStatusString method
     * @param status integer constant representing
     *               a status
     * @return A string literal representing the status
     *         the book. It can be one of "Accepted",
     *         "Borrowed", "Requested" or "Available"
     */
    public static String getStatusString(int status) {
        switch (status) {
            case Book.ACCEPTED:
                return "Accepted";
            case Book.AVAILABLE:
                return "Available";
            case Book.BORROWED:
                return "Borrowed";
            case Book.REQUESTED:
                return "Requested";
            default:
                return "";
        }
    }

    /**
     * Gets who currently has the book
     * ** Need to define behaviour when the book is AVAILABLE
     * @return A string identifying who the book is lent to
     */
    public String getLentTo() {
        if (lentTo == null) { return "Not Borrowed"; }
        return lentTo;
    }

    /**
     * Gets the image associated with the book
     * @return An image for the book
     */
    public Image getPhoto() {
        return photo;
    }

    /**
     * Gets the image associated with the book
     * @return An image for the book
     */
    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    /**
     * Gets the image associated with the book
     * @return An image for the book
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Sets the author associated with the book
     * @param author
     */
    public void setAuthor(String author) {
        this.author = author;
    }

    /**
     * Sets the image associated with the book
     * @param owner
     */
    public void setOwner(String owner) {
        this.owner = owner;
    }

    /**
     * Sets the image associated with the book
     * @param status
     */
    public void setStatus(int status) {
        this.status = status;
    }

    /**
     * Sets the image associated with the book
     * @param lentTo
     */
    public void setLentTo(String lentTo) {
        this.lentTo = lentTo;
    }

    /**
     * Sets the image associated with the book
     * @param photo
     */
    public void setPhoto(Image photo) {
        this.photo = photo;
    }
}
