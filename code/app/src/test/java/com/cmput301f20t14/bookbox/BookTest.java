package com.cmput301f20t14.bookbox;

import com.cmput301f20t14.bookbox.entities.Book;

import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertEquals;

/**
 * Test file for the Book entity class.
 * @author Carter Sabadash
 * @author Olivier Vadiavaloo
 */

public class BookTest {

    private Book mockBook(){
        Book book = new Book("1234567890", "MyBook", "MyAuthor", "Me",
                Book.ACCEPTED, "MyFriend", "doc/picture/image.jpg");
        return book;
    }

    @Test
    public void TestISBN(){
        Book book = mockBook();
        assertEquals(book.getIsbn(), "1234567890");
    }

    @Test
    public void TestTitle(){
        Book book = mockBook();
        assertEquals(book.getTitle(), "MyBook");

        book.setTitle("New Title");
        assertEquals(book.getTitle(), "New Title");
    }

    @Test
    public void TestAuthor(){
        Book book = mockBook();
        assertEquals(book.getAuthor(), "MyAuthor");

        book.setAuthor("David Clement-Davies");
        assertEquals(book.getAuthor(), "David Clement-Davies");
    }

    @Test
    public void TestOwner(){
        Book book = mockBook();
        assertEquals(book.getOwner(), "Me");

        book.setOwner("Carter");
        assertEquals(book.getOwner(), "Carter");
    }

    @Test
    public void TestStatus(){
        Book book = mockBook();
        assertEquals(book.getStatus(), Book.ACCEPTED);
        assertEquals(book.getStatusString(), "Accepted");

        book.setStatus(Book.REQUESTED);
        assertEquals(book.getStatus(), Book.REQUESTED);
        assertEquals(book.getStatusString(), "Requested");
    }

    @Test
    public void TestLentTo(){
        Book book = mockBook();
        assertEquals(book.getLentTo(), "MyFriend");

        book.setLentTo(null);
        assertEquals(book.getLentTo(), "Not Borrowed");

        book.setLentTo("Olivier");
        assertEquals(book.getLentTo(), "Olivier");
    }

    @Test
    public void TestPhotoURL(){
        Book book = mockBook();
        assertEquals(book.getPhotoUrl(), "doc/picture/image.jpg");

        book.setPhotoUrl("photo2");
        assertEquals(book.getPhotoUrl(), "photo2");
    }
}