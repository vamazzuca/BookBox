/*
 * User.java
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

import java.util.ArrayList;

/**
 * A class that contains all the information to represent a user
 * @author Carter Sabadash
 * @version 2020.10.22
 *
 * If the password is stored in Firebase, then the only reason to store it here
 *      is to reduce database accesses (if the user tries to change password)
 */

public class User {
    private String username;
    private String password; // ideally this wouldn't be stored in plaintext, but it will do for now
    private String email;
    private String phone;
    private BookList ownedBooks;
    private BookList borrowedBooks;
    private Image photo;
    private ArrayList<Request> requests; // maybe have a container class for requests?

    /**
     * Creates a user without a photo
     * @param username The username of the user (unique)
     * @param password The users password
     * @param email The email of the user
     * @param phone The phone number of the user
     */
    public User(String username, String password, String email, String phone) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.phone = phone;
    }

    /**
     * Creates a user with a photo
     * @param username The username of the user (unique)
     * @param password The users password
     * @param email The users email
     * @param phone The users phone #
     * @param photo A photo for the user
     */
    public User(String username, String password, String email, String phone, Image photo) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.phone = phone;
        this.photo = photo;
    }
}
