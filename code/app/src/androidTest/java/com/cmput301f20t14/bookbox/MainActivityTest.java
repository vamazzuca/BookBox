package com.cmput301f20t14.bookbox;

import android.app.Activity;
import android.widget.EditText;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.cmput301f20t14.bookbox.activities.HomeActivity;
import com.cmput301f20t14.bookbox.activities.MainActivity;
import com.cmput301f20t14.bookbox.activities.RegisterUserActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Test class for MainActivity. All UI tests related
 * to MainActivity are written. Robotium test framework
 * is used.
 * Before running this test, it is necessary to create a test user
 *  -> RegisterUserActivityTest will create the proper user (if not already created)
 *  -> Also make sure that you are logged out before testing
 *      Users are signed out after each test (@Before is not early enough)
 *      so try running the test twice
 * @author Olivier Vadiavaloo, Carter Sabadash
 * @version 2020.10.29
 */

public class MainActivityTest {
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
    public void checkLoginWrongUsername(){
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);

        // test for just username
        solo.enterText((EditText) solo.getView(R.id.username_editText), "IncorrectUsername");
        solo.enterText((EditText) solo.getView(R.id.password_editText), "bookboxtest1");

        solo.clickOnButton("Log in");

        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
    }

    @Test
    public void checkLoginFail() {
        // test for just password
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);

        solo.enterText((EditText) solo.getView(R.id.username_editText), "IncorrectUsername");
        solo.enterText((EditText) solo.getView(R.id.password_editText), "IncorrectPassword");

        solo.clickOnButton("Log in");

        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
    }

    @Test
    public void checkLoginEmptyFields() {
        solo.assertCurrentActivity("Wrong activity", MainActivity.class);

        solo.enterText((EditText) solo.getView(R.id.username_editText), "");
        solo.enterText((EditText) solo.getView(R.id.password_editText), "");

        solo.clickOnButton("Log in");

        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
    }

    @Test
    public void checkLoginWrongPassword() {
        // test for wrong password
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);

        solo.enterText((EditText) solo.getView(R.id.username_editText), "bookboxtest1@bookbox.com");
        solo.enterText((EditText) solo.getView(R.id.password_editText), "IncorrectPassword");

        solo.clickOnButton("Log in");

        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
    }

    @Test
    public void checkLoginSuccess(){
        // test that login works correctly
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);

        solo.enterText((EditText) solo.getView(R.id.username_editText), "bookboxtest1@bookbox.com");
        solo.enterText((EditText) solo.getView(R.id.password_editText), "bookboxtest1");

        solo.clickOnButton("Log in");

        solo.assertCurrentActivity("Wrong Activity", HomeActivity.class);
    }

    /** Close activity after each test */
    @After
    public void tearDown() throws  Exception{
        solo.finishOpenedActivities();
        FirebaseAuth.getInstance().signOut();
    }
}
