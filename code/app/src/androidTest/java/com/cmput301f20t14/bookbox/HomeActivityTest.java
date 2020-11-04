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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class HomeActivityTest {
    private Solo solo;

    @Rule
    public ActivityTestRule<MainActivity> rule =
            new ActivityTestRule<>(MainActivity.class, true);

    /**
     * Runs before all tests; creates solo instance
     */
    @Before
    public void setUp() throws Exception{
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());
    }

    /**
     * Gets the Activity
     * @throws Exception
     */
    @Test
    public void start() throws Exception {
        Activity activity = rule.getActivity();
    }

    @Test
    public void checkActivitySwitch() {
        solo.assertCurrentActivity("Wrong activity", MainActivity.class);

        solo.enterText((EditText) solo.getView(R.id.username_editText), "correctUsername");
        solo.enterText((EditText) solo.getView(R.id.password_editText), "correctPassword");
        solo.clickOnButton("Log in");

        solo.assertCurrentActivity("Wrong Activity/Unsuccessful login", HomeActivity.class);
    }

    @Test
    public void checkHomeActivityList() throws InterruptedException {
        checkActivitySwitch();
        try {
            deleteFromList();
        } catch (Exception e) {
            // Do nothing
        }

        solo.clickOnImageButton(1);
        solo.assertCurrentActivity("Wrong activity", AddBookActivity.class);

        solo.enterText((EditText) solo.getView(R.id.Title_editText), "testTitle");
        solo.enterText((EditText) solo.getView(R.id.Author_editText), "testAuthor");
        solo.enterText((EditText) solo.getView(R.id.ISBN_editText), "testISBN");
        solo.clickOnButton(2);

        solo.assertCurrentActivity("Wrong Activity", HomeActivity.class);

        assertTrue(solo.searchText("testISBN"));
        assertTrue(solo.searchText("testTitle"));
        assertTrue(solo.searchText("testAuthor"));
        assertTrue(solo.searchText("correctUsername"));
        assertTrue(solo.searchText("Available"));

        try {
            deleteFromList();
        } catch (Exception e) {
            // Do nothing
        }
    }

    /**
     * Deletes the added book from the list
     * @throws InterruptedException
     */
    public void deleteFromList() throws InterruptedException {
        try {
            solo.clickInList(1);
        } catch (junit.framework.AssertionFailedError e) {
            return;
        }

        solo.assertCurrentActivity("Wrong activity", EditBookActivity.class);
        solo.clickOnButton("Delete");
        solo.clickOnButton("Yes");
        solo.assertCurrentActivity("Wrong activity/Unsuccessful deletion", HomeActivity.class);
        solo.wait(10000);
        assertFalse(solo.searchText("testISBN"));
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
