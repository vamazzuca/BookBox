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

        EditText password = (EditText) solo.getView(R.id.profile_password_editText);
        EditText phone = (EditText) solo.getView(R.id.profile_phone_editText);

        assertEquals("correctPassword", password.getText().toString());
        assertEquals("780 999 1111", phone.getText().toString());
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

        EditText password = (EditText) solo.getView(R.id.profile_password_editText);
        EditText phone = (EditText) solo.getView(R.id.profile_phone_editText);
        EditText email = (EditText) solo.getView(R.id.profile_email_editText);

        assertEquals("correctPassword2", password.getText().toString());
        assertEquals("000 000 0001", phone.getText().toString());
        assertEquals("correctEmail@bookbox.com", email.getText().toString());

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