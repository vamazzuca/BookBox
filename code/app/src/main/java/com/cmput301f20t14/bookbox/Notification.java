package com.cmput301f20t14.bookbox;


/**
 * A class to represent a notification
 * @author Carter Sabadash
 * @version 2020.10.22
 *
 * This currently provides only basic functionality, if we want to perform actions on the
 *      notification screen, then this will have to be made an abstract class and need to
 *      implement at least RequestNotification RequestAcceptedNotification
 *      (ReturnRequestedNotification, RequestDecllined (or just use default for this one)?) ?
 * The details of this will probably change a lot... the purpose is to view notifications in the app
 *
 */
public abstract class Notification {
    private String description;
    private User from;
    private User to;
    private boolean read;

    /**
     * Creates a Notification
     * @param description The text
     * @param from The user who performed an action to initiate the notification
     * @param to The user who is receiving the notification
     */
    public Notification(String description, User from, User to) {
        this.description = description;
        this.from = from;
        this.to = to;
        this.read = false;
    }
}
