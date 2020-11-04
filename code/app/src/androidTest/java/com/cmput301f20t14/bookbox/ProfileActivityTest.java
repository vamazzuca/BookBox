package com.cmput301f20t14.bookbox;

import android.app.Activity;
import android.widget.EditText;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.cmput301f20t14.bookbox.activities.HomeActivity;
import com.cmput301f20t14.bookbox.activities.MainActivity;
import com.cmput301f20t14.bookbox.activities.ProfileActivity;
import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ProfileActivityTest {
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
        solo.clickOnView(solo.getView(R.id.profile_bottom_nav));
        solo.assertCurrentActivity("Wrong Activity", ProfileActivity.class);
    }

    @Test
    public void checkProfileEditFail() {
        checkActivitySwitch();

        solo.enterText((EditText) solo.getView(R.id.profile_password_editText), "");
        solo.enterText((EditText) solo.getView(R.id.profile_phone_editText), "");
        solo.clickOnButton("Confirm");
        solo.clickOnView(solo.getView(R.id.home_bottom_nav));
        solo.clickOnView(solo.getView(R.id.profile_bottom_nav));

        assertTrue(solo.searchText("correctUsername"));
        assertTrue(solo.searchText("correctPassword"));
    }

    @Test
    public void checkProfileEditSuccess() {
        checkActivitySwitch();

        solo.enterText((EditText) solo.getView(R.id.profile_password_editText), "");
        solo.enterText((EditText) solo.getView(R.id.profile_phone_editText), "");
        solo.clickOnButton("Confirm");

        solo.enterText((EditText) solo.getView(R.id.profile_password_editText), "correctPassword2");
        solo.enterText((EditText) solo.getView(R.id.profile_phone_editText), "000 000 0001");
        solo.enterText((EditText) solo.getView(R.id.profile_email_editText), "correctEmail@bookbox.com");
        solo.clickOnButton("Confirm");
        solo.clickOnView(solo.getView(R.id.home_bottom_nav));
        solo.clickOnView(solo.getView(R.id.profile_bottom_nav));

        assertTrue(solo.searchText("correctPassword2"));
        assertTrue(solo.searchText("000 000 0001"));
        assertTrue(solo.searchText("correctEmail@bookbox.com"));

        solo.enterText((EditText) solo.getView(R.id.profile_password_editText), "");
        solo.enterText((EditText) solo.getView(R.id.profile_phone_editText), "");
        solo.enterText((EditText) solo.getView(R.id.profile_email_editText), "");
        solo.clickOnButton("Confirm");

        solo.enterText((EditText) solo.getView(R.id.profile_password_editText), "correctPassword");
        solo.enterText((EditText) solo.getView(R.id.profile_phone_editText), "780 999 1111");
        solo.clickOnButton("Confirm");
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
