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
package com.cmput301f20t14.bookbox;

import android.media.Image;

/**
 * A class that contains all the information necessary to represent a book
 * @author Carter Sabadash
 * @version 2020.10.22
 *
 * All Getters have been implemented, but not setters (will have to also ensure that
 *     data entered is in correct format
 * The behaviour of the getLentTo() must be defined when the book Status is AVAILABLE
 *
 * Move the Status enum to a public file?
 */
public class Book {
    enum Status {
        AVAILABLE,
        REQUESTED,
        ACCEPTED,
        BORROWED
    } // move to a public file with other enums?

    private String isbn;
    private String title;
    private String author;
    private User owner;
    private Status status;
    private User lentTo;
    private Image photo;

    /**
     * Constructs a book without an image
     * @param isbn The isbn of the book
     * @param title The title of the book
     * @param author The author of the book
     * @param owner The User who owns the book
     */
    public Book(String isbn, String title, String author, User owner) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.owner = owner;
        // when we have default images
        // this.photo = DEFAULT_BOOK_PHOTO;
    }

    /**
     * Constructs a book with an image
     * @param isbn The isbn of the book
     * @param title The title of the book
     * @param author The author of the book
     * @param owner The User who owns the book
     * @param photo The image to display with the book
     */
    public Book(String isbn, String title, String author, User owner, Image photo) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.owner = owner;
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
     * @return A User who owns the book
     */
    public User getOwner() {
        return owner;
    }

    /**
     * Gets the Status of the book
     * @return A Status of the book
     */
    public Status getStatus() {
        return status;
    }

    /**
     * Gets who currently has the book
     * ** Need to define behaviour when the book is AVAILABLE
     * @return A User who the book is lent to
     */
    public User getLentTo() {
        return lentTo;
    }

    /**
     * Gets the image associated with the book
     * @return An image for the book
     */
    public Image getPhoto() {
        return photo;
    }
}
