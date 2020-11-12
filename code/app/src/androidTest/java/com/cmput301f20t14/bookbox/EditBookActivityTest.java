package com.cmput301f20t14.bookbox;

import android.app.Activity;
import android.widget.EditText;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.cmput301f20t14.bookbox.activities.AddBookActivity;
import com.cmput301f20t14.bookbox.activities.EditBookActivity;
import com.cmput301f20t14.bookbox.activities.HomeActivity;
import com.cmput301f20t14.bookbox.activities.MainActivity;
import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Test class for EditBookActivity. All UI tests related
 * to EditBookActivity are written. Robotium test framework
 * is used.
 * @author Olivier Vadiavaloo
 * @version 2020.10.29
 */

public class EditBookActivityTest {
    private Solo solo;

    /**
     * Logs into the app
     */
    public void logIn() {
        solo.assertCurrentActivity("Wrong activity", MainActivity.class);

        solo.enterText((EditText) solo.getView(R.id.username_editText), "correctUsername");
        solo.enterText((EditText) solo.getView(R.id.password_editText), "correctPassword");
        solo.clickOnButton("Log in");
    }

    /**
     * Add the book to be edited
     */
    public void addBook() {
        logIn();

        solo.clickOnImageButton(1);
        solo.assertCurrentActivity("Wrong activity", AddBookActivity.class);

        solo.enterText((EditText) solo.getView(R.id.Title_editText), "testTitle");
        solo.enterText((EditText) solo.getView(R.id.Author_editText), "testAuthor");
        solo.enterText((EditText) solo.getView(R.id.ISBN_editText), "testISBN");
        solo.clickOnButton(2);

        solo.assertCurrentActivity("Wrong Activity", HomeActivity.class);

        solo.clickInList(1);
        solo.assertCurrentActivity("Wrong activity", EditBookActivity.class);
        assertTrue(solo.searchText("testISBN"));
    }

    @Rule
    public ActivityTestRule<MainActivity> rule =
            new ActivityTestRule<>(MainActivity.class, true);

    /**
     * Runs before all tests; creates solo instance
     */
    @Before
    public void setUp() throws Exception{
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());
        addBook();
    }

    /**
     * Deletes added book from the list of owned books in home page and database
     * @throws InterruptedException
     */
    @Test
    public void checkDeleteFromList() throws InterruptedException {
        solo.assertCurrentActivity("Wrong activity", EditBookActivity.class);
        solo.clickOnButton("Delete");
        solo.clickOnButton("Yes");
        solo.assertCurrentActivity("Wrong activity/Unsuccessful deletion", HomeActivity.class);
        assertFalse(solo.searchText("testISBN"));
    }

    @Test
    public void checkEmptyEdit() throws InterruptedException {
        solo.enterText((EditText) solo.getView(R.id.edit_title_editText), "");
        solo.enterText((EditText) solo.getView(R.id.edit_author_editText), "");
        solo.enterText((EditText) solo.getView(R.id.edit_isbn_editText), "");
        solo.waitForText("");

        solo.clickOnButton("Update");
        solo.clickOnView(solo.getView(R.id.home_bottom_nav));
        solo.clickInList(1);

        assertTrue(solo.searchText("testTitle"));
        assertTrue(solo.searchText("testAuthor"));
        assertTrue(solo.searchText("testISBN"));

        solo.clickOnButton("Delete");
        solo.clickOnButton("Yes");
        solo.assertCurrentActivity("Wrong activity", HomeActivity.class);
    }

    @Test
    public void checkEditBookSuccess() throws InterruptedException {
        solo.enterText((EditText) solo.getView(R.id.edit_title_editText), "");
        solo.enterText((EditText) solo.getView(R.id.edit_author_editText), "");
        solo.enterText((EditText) solo.getView(R.id.edit_isbn_editText), "");

        solo.waitForText("");
        solo.enterText((EditText) solo.getView(R.id.edit_title_editText), "testTitleEdit");
        solo.enterText((EditText) solo.getView(R.id.edit_author_editText), "testAuthorEdit");
        solo.enterText((EditText) solo.getView(R.id.edit_isbn_editText), "testISBNEdit");
        solo.waitForText("testTitleEdit");
        solo.waitForText("testAuthorEdit");
        solo.waitForText("testISBNEDIT");

        solo.clickOnButton("Update");
        solo.clickOnView(solo.getView(R.id.home_bottom_nav));
        solo.clickInList(1);

        assertTrue(solo.searchText("testTitleEdit"));
        assertTrue(solo.searchText("testAuthorEdit"));
        assertTrue(solo.searchText("testISBNEdit"));

        solo.clickOnButton("Delete");
        solo.clickOnButton("Yes");
    }



    /**
     * Close activity after each test
     * @throws Exception
     */
    @After
    public void tearDown() throws  Exception{
        solo.finishOpenedActivities();
    }
}
