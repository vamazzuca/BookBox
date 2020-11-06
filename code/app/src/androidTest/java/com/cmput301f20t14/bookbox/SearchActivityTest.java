package com.cmput301f20t14.bookbox;

import android.app.Activity;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.cmput301f20t14.bookbox.activities.HomeActivity;
import com.cmput301f20t14.bookbox.activities.MainActivity;
import com.cmput301f20t14.bookbox.activities.SearchActivity;
import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Test class for SearchActivity. All UI tests related
 * to SearchActivity are written. Robotium test framework
 * is used.
 * @author Olivier Vadiavaloo
 * @version 2020.10.29
 */

public class SearchActivityTest {
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

    @Test
    public void checkActivitySwitch() {
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
        solo.enterText((EditText) solo.getView(R.id.username_editText), "Olivier");
        solo.enterText((EditText) solo.getView(R.id.password_editText), "password");

        solo.clickOnButton("Log in");
        solo.assertCurrentActivity("Wrong Activity", HomeActivity.class);

        solo.clickOnImageButton(0);
        solo.assertCurrentActivity("Wrong Activity", SearchActivity.class);
    }

    @Test
    public void checkSearchWithResults() {
        checkActivitySwitch();

        solo.enterText((EditText) solo.getView(R.id.search_EditText), "man");
        solo.clickOnButton("Search");
        assertTrue(solo.searchText("Man's Search for Meaning"));
        assertTrue(solo.searchText("Available"));
    }
    
    @Test
    public void checkSearchNoResults() {
        checkActivitySwitch();

        solo.enterText((EditText) solo.getView(R.id.search_EditText), "nothing");
        solo.clickOnButton("Search");
        assertTrue(solo.searchText(solo.getString(R.string.no_results)));
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
