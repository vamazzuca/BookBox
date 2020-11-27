package com.cmput301f20t14.bookbox;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.widget.Button;
import android.widget.EditText;

import androidx.fragment.app.Fragment;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.cmput301f20t14.bookbox.activities.HomeActivity;
import com.cmput301f20t14.bookbox.activities.MainActivity;
import com.cmput301f20t14.bookbox.activities.ProfileActivity;
import com.cmput301f20t14.bookbox.fragments.UpdatePhoneFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Test class for ProfileActivity. All UI tests related
 * to ProfileActivity are written. Robotium test framework
 * is used.
 * *The user must be logged out before the tests start
 * (If it fails, running twice should fix it as we are logged out each time)
 * @author Olivier Vadiavaloo
 * @author Carter Sabadash
 * @version 2020.11.26
 */

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

        solo.enterText((EditText) solo.getView(R.id.username_editText), "bookboxtest1@bookbox.com");
        solo.enterText((EditText) solo.getView(R.id.password_editText), "bookboxtest1");
        solo.clickOnButton("Log in");
        solo.assertCurrentActivity("Wrong Activity/Unsuccessful login", HomeActivity.class);
        solo.clickOnView(solo.getView(R.id.profile_bottom_nav));
        solo.assertCurrentActivity("Wrong Activity", ProfileActivity.class);
    }

    @Test
    public void checkPhoneEditFail(){
        checkActivitySwitch();

        solo.clickOnButton("0000000000");
        solo.enterText((EditText) solo.getView(R.id.update_phone_editText), "01i3/");
        solo.clickOnButton("Update");
        solo.sleep(500);
        solo.searchText("Invalid phone number");
    }

    @Test
    public void checkPhoneEditSuccess(){
        checkActivitySwitch();

        solo.clickOnButton("0000000000");
        solo.waitForFragmentByTag("UPDATE_PHONE");
        solo.enterText((EditText) solo.getView(R.id.update_phone_editText), "123456789");
        solo.clickOnView(solo.getView(android.R.id.button1)); // https://stackoverflow.com/questions/33560746/how-to-test-alertdialog-item-click-in-robotium-for-android-studio
        solo.sleep(500);
        solo.searchText("123456789");

        // switch it back so the test will work next time
        solo.clickOnButton("123456789");
        solo.waitForFragmentByTag("UPDATE_PHONE");
        solo.enterText((EditText) solo.getView(R.id.update_phone_editText), "0000000000");
        solo.clickOnView(solo.getView(android.R.id.button1));
        solo.sleep(500);
        solo.searchText("0000000000");
    }
    @Test
    public void checkEmailEditFail(){
        checkActivitySwitch();

        solo.clickOnButton("bookboxtest1@bookbox.com");
        solo.waitForFragmentByTag("UPDATE_EMAIL");
        solo.enterText((EditText) solo.getView(R.id.update_email_editText), "badEmail"); // check bad email, good password
        solo.enterText((EditText) solo.getView(R.id.update_email_password), "bookboxtest1");

        solo.clickOnView(solo.getView(android.R.id.button1)); // https://stackoverflow.com/questions/33560746/how-to-test-alertdialog-item-click-in-robotium-for-android-studio
        solo.sleep(500);

        solo.clearEditText((EditText) solo.getView(R.id.update_email_editText));
        solo.clearEditText((EditText) solo.getView(R.id.update_email_password));
        solo.enterText((EditText) solo.getView(R.id.update_email_editText), "newEmail@bookbox.com"); // check good email, bad password
        solo.enterText((EditText) solo.getView(R.id.update_email_password), "badPAssword");

        solo.clickOnView(solo.getView(android.R.id.button1));
        solo.clickOnView(solo.getView(android.R.id.button2));
        solo.searchText("bookboxtest1@bookbox.com");
    }

    @Test
    public void checkEmailEditSuccess(){
        checkActivitySwitch();

        solo.clickOnButton("bookboxtest1@bookbox.com");
        solo.waitForFragmentByTag("UPDATE_EMAIL");
        solo.enterText((EditText) solo.getView(R.id.update_email_editText), "newEmail@bookbox.com");
        solo.enterText((EditText) solo.getView(R.id.update_email_password), "bookboxtest1");

        solo.clickOnView(solo.getView(android.R.id.button1)); // https://stackoverflow.com/questions/33560746/how-to-test-alertdialog-item-click-in-robotium-for-android-studio
        solo.sleep(500);
        solo.searchText("newEmail@bookbox.com");

        // change back so future tests work
        solo.clickOnButton("newEmail@bookbox.com");
        solo.waitForFragmentByTag("UPDATE_EMAIL");
        solo.enterText((EditText) solo.getView(R.id.update_email_editText), "bookboxtest1@bookbox.com"); // check good email, bad password
        solo.enterText((EditText) solo.getView(R.id.update_email_password), "bookboxtest1");

        solo.clickOnView(solo.getView(android.R.id.button1));
        solo.searchText("bookboxtest1@bookbox.com");
    }

    @Test
    public void checkPasswordEditFail(){
        // check with correct password, incorrect new passwords
        // and with matching new passwords, bad old password
        checkActivitySwitch();

        solo.clickOnButton("Update Password");
        solo.waitForFragmentByTag("UPDATE_PASSWORD");
        solo.enterText((EditText) solo.getView(R.id.update_password_old_poassword_editText), "bookboxtest1");
        solo.enterText((EditText) solo.getView(R.id.update_password_password_editText), "password1");
        solo.enterText((EditText) solo.getView(R.id.update_password_password_confirm_editText), "password2");

        solo.clickOnView(solo.getView(android.R.id.button1));

        solo.clearEditText((EditText) solo.getView(R.id.update_password_old_poassword_editText));
        solo.enterText((EditText) solo.getView(R.id.update_password_old_poassword_editText), "badPassword");
        solo.clearEditText((EditText) solo.getView(R.id.update_password_password_confirm_editText));
        solo.enterText((EditText) solo.getView(R.id.update_password_password_confirm_editText), "password1");
        solo.clickOnView(solo.getView(android.R.id.button1));
        solo.clickOnView(solo.getView(android.R.id.button2));
    }

    @Test
    public void checkPasswordEditSuccess(){
        checkActivitySwitch();

        solo.clickOnButton("Update Password");
        solo.waitForFragmentByTag("UPDATE_PASSWORD");
        solo.enterText((EditText) solo.getView(R.id.update_password_old_poassword_editText), "bookboxtest1");
        solo.enterText((EditText) solo.getView(R.id.update_password_password_editText), "password1");
        solo.enterText((EditText) solo.getView(R.id.update_password_password_confirm_editText), "password1");

        solo.clickOnView(solo.getView(android.R.id.button1));
        solo.sleep(500);

        // and change back the password
        solo.clickOnButton("Update Password");
        solo.waitForFragmentByTag("UPDATE_PASSWORD");
        solo.enterText((EditText) solo.getView(R.id.update_password_old_poassword_editText), "password1");
        solo.enterText((EditText) solo.getView(R.id.update_password_password_editText), "bookboxtest1");
        solo.enterText((EditText) solo.getView(R.id.update_password_password_confirm_editText), "bookboxtest1");

        solo.clickOnView(solo.getView(android.R.id.button1));
        solo.sleep(500);
    }

    /**
     * Close activity after each test
     * @throws Exception
     */
    @After
    public void tearDown() throws  Exception {
        solo.finishOpenedActivities();
        FirebaseAuth.getInstance().signOut();
    }
}
