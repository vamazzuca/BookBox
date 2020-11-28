package com.cmput301f20t14.bookbox;

import com.cmput301f20t14.bookbox.entities.Book;
import com.cmput301f20t14.bookbox.entities.User;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class UserTest {

    private User mockUser() {
        User user = new User(
                "Joe",
                "password",
                "7805552222",
                "joe@ualberta.ca",
                new ArrayList<Book>(),
                new ArrayList<Book>(),
                ""
        );
        return user;
    }

    @Test
    public void TestUserInfomation() {
        User user = mockUser();
        assertEquals(user.getUsername(), "Joe");
        assertEquals(user.getEmail(), "joe@ualberta.ca");
        assertEquals(user.getPhone(), "7805552222");
        assertEquals(user.getPassword(), "password");
    }

    @Test
    public void TestValidEmailSyntax() {
        assertTrue(User.isEmailSyntaxValid("joe@ualberta.ca"));
        assertFalse(User.isEmailSyntaxValid("lorem ipsum"));
    }

    @Test
    public void TestValidPhoneSyntax() {
        assertTrue(User.isPhoneSyntaxValid("+780 000 2222"));
        assertFalse(User.isEmailSyntaxValid("lorem ipsum"));
    }
}
