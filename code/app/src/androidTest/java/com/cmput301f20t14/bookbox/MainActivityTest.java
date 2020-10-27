package com.cmput301f20t14.bookbox;

import android.widget.EditText;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.cmput301f20t14.bookbox.activities.HomeActivity;
import com.cmput301f20t14.bookbox.activities.MainActivity;
import com.cmput301f20t14.bookbox.activities.RegisterUserActivity;
import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class MainActivityTest {
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
     * Gets the activity
     * **This test needs to be finished when RegisterUserActivity is added**
     * @throws Exception
     */
    @Test
    public void checkRegister(){
        // asserts that the current activity is main activity
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);

        solo.clickOnButton("Log in");

        // checks that we successfully navigated to RegisterUserActivity
        solo.assertCurrentActivity("Wrong Activity", RegisterUserActivity.class);
    }

    @Test
    public void loginUserFail(){
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);

        // test for just username
        solo.enterText((EditText) solo.getView(R.id.username_editText), "user1");

        solo.clickOnButton("Log in");

        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
    }

    @Test
    public void loginPasswordFail(){
        // test for just password
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);

        solo.enterText((EditText) solo.getView(R.id.password_editText), "user1");

        solo.clickOnButton("Log in");

        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
    }

    @Test
    public void loginWrongPassword(){
        // test for wrong password
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);

        solo.enterText((EditText) solo.getView(R.id.username_editText), "Example User");
        solo.enterText((EditText) solo.getView(R.id.password_editText), "password");

        solo.clickOnButton("Log in");

        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
    }

    @Test
    public void loginSuccess(){
        // test that login works correctly
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);

        solo.enterText((EditText) solo.getView(R.id.username_editText), "Example User");
        solo.enterText((EditText) solo.getView(R.id.password_editText), "p");

        solo.clickOnButton("Log in");

        solo.assertCurrentActivity("Wrong Activity", HomeActivity.class);
    }

    /** Close activity after each test */
    @After
    public void tearDown() throws  Exception{
        solo.finishOpenedActivities();
    }
}
