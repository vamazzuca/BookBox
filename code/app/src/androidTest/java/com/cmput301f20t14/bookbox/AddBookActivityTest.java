package com.cmput301f20t14.bookbox;

import android.app.Activity;
import android.widget.EditText;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.cmput301f20t14.bookbox.activities.AddBookActivity;
import com.cmput301f20t14.bookbox.activities.EditBookActivity;
import com.cmput301f20t14.bookbox.activities.HomeActivity;
import com.cmput301f20t14.bookbox.activities.ListsActivity;
import com.cmput301f20t14.bookbox.activities.MainActivity;
import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class AddBookActivityTest {
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

    public void checkActivitySwitch() {
        solo.assertCurrentActivity("Wrong activity", MainActivity.class);

        solo.enterText((EditText) solo.getView(R.id.username_editText), "joe@ualberta.ca");
        solo.enterText((EditText) solo.getView(R.id.password_editText), "password");
        solo.clickOnButton("Log in");

        solo.assertCurrentActivity("Wrong Activity/Unsuccessful login", HomeActivity.class);
        solo.clickOnView(solo.getView(R.id.add_book_button));
        solo.assertCurrentActivity("Wrong Activity", AddBookActivity.class);
    }

    @Test
    public void checkAddBook() {
        checkActivitySwitch();
        solo.enterText(R.id.Author_editText, "TestTitle");
        solo.enterText(R.id.Title_editText, "TestAuthor");
        solo.enterText(R.id.ISBN_editText, "00000000");
        solo.clickOnButton("Add");
        solo.assertCurrentActivity("Wrong activity", HomeActivity.class);
        assertTrue(solo.searchText("TestTitle"));
        solo.clickInList(1);
        solo.assertCurrentActivity("Wrong activity", EditBookActivity.class);
        solo.clickOnButton("Delete");
        solo.clickOnButton("Confirm");
        solo.assertCurrentActivity("Wrong activity", HomeActivity.class);
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
