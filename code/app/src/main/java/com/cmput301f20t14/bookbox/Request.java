/*
 * Request.java
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

import android.app.Notification;
import android.location.Location;

/**
 * A class to represent a request on a book; sends a notification to the user
 * @author Carter Sabadash
 * @version 2020.10.22
 */
public class Request {
    private User from;
    private Book bookRequested;
    private Location geoLocation;

    /**
     * Initiates a request on a book and sends a notification to the book owner
     * @param from The User making the request
     * @param bookRequested The Book to be requested
     */
    public Request(User to, User from, Book bookRequested) {
        // assert(bookRequested.getStatus == AVAILABLE || bookRequested.getStatus == REQUESTED);
        this.from = from;
        this.bookRequested = bookRequested;
    }
}
