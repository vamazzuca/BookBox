package com.cmput301f20t14.bookbox.entities;

/**
 * This entity class models a request by a user
 * to another user to borrow a specified book from
 * the latter's collection.
 * @author Olivier Vadiavaloo
 * @version 2020.11.10
 */

public class Request {
    private String borrower;
    private String owner;
    private Book book;

    /**
     * Constructor of the Request object
     * @param borrower username of the one making the request
     * @param owner    username of the one owning the requested book
     * @param book     the requested book
     */
    public Request(String borrower, String owner, Book book) {
        this.borrower = borrower;
        this.owner = owner;
        this.book = book;
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
}
