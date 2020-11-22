package com.cmput301f20t14.bookbox;

import android.app.Activity;
import android.widget.EditText;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.cmput301f20t14.bookbox.activities.HomeActivity;
import com.cmput301f20t14.bookbox.activities.ListsActivity;
import com.cmput301f20t14.bookbox.activities.MainActivity;
import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Test class for OutRequestListActivity. All UI tests related
 * to OutRequestListActivity are written. Robotium test framework
 * is used.
 * @author Olivier Vadiavaloo
 * @version 2020.10.29
 */

public class OutRequestListActivityTest {
    private Solo solo;

    @Rule
    public ActivityTestRule<MainActivity> rule =
            new ActivityTestRule<>(MainActivity.class, true);

    /**
     * Runs before all tests; creates solo instance
     */
    @Before
    public void setUp() throws Exception {
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
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
        solo.enterText((EditText) solo.getView(R.id.username_editText), "Olivier");
        solo.enterText((EditText) solo.getView(R.id.password_editText), "password");

        solo.clickOnButton("Log in");
        solo.assertCurrentActivity("Wrong Activity", HomeActivity.class);

        solo.clickOnView(solo.getView(R.id.lists_bottom_nav));
        solo.assertCurrentActivity("Wrong Activity", ListsActivity.class);

        solo.clickInList(1);
        solo.assertCurrentActivity("Wrong Activity", OutRequestListActivity.class);

        assertTrue(solo.searchText("Requested"));
    }

    /** Close activity after each test */
    @After
    public void tearDown() throws  Exception{
        solo.finishOpenedActivities();
    }
}
