package com.cmput301f20t14.bookbox;

import android.app.Activity;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.cmput301f20t14.bookbox.activities.HomeActivity;
import com.cmput301f20t14.bookbox.activities.MainActivity;
import com.cmput301f20t14.bookbox.activities.ProfileActivity;
import com.cmput301f20t14.bookbox.activities.SearchActivity;
import com.cmput301f20t14.bookbox.activities.UserSearchActivity;
import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Test Class for the UserSearchActivity
 * Robotium test framework used
 * @author: Nicholas DeMarco
 * @version 2020-11-28
 */
public class UserSearchActivityTest {

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
        solo.enterText((EditText) solo.getView(R.id.username_editText), "ov@ualberta.ca");
        solo.enterText((EditText) solo.getView(R.id.password_editText), "password");

        solo.clickOnButton("Log in");
        solo.assertCurrentActivity("Wrong Activity", HomeActivity.class);

        solo.clickOnView(solo.getView(R.id.profile_bottom_nav));
        solo.assertCurrentActivity("Wrong Activity", ProfileActivity.class);

        solo.clickOnImageButton(0);
        solo.assertCurrentActivity("Wrong Activity", UserSearchActivity.class);

    }

    @Test
    public void checkSearchWithResults() {
        checkActivitySwitch();

        solo.enterText((EditText) solo.getView(R.id.search_text_user), "ol");
        solo.clickOnImageButton(0);
        assertTrue(solo.searchText("Olivier"));

    }

    @Test
    public void checkSearchNoResults() {
        checkActivitySwitch();

        solo.enterText((EditText) solo.getView(R.id.search_text_user), "nothing");
        solo.clickOnImageButton(0);
        assertTrue(solo.searchText(solo.getString(R.string.no_results)));
    }

    @After
    public void tearDown() throws  Exception{
        solo.finishOpenedActivities();
    }

}















