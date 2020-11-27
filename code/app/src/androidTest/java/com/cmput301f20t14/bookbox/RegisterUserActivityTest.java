package com.cmput301f20t14.bookbox;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.cmput301f20t14.bookbox.activities.EditBookActivity;
import com.cmput301f20t14.bookbox.activities.HomeActivity;
import com.cmput301f20t14.bookbox.activities.MainActivity;
import com.cmput301f20t14.bookbox.activities.RegisterUserActivity;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.firebase.auth.FirebaseAuth;
import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.ComparisonFailure;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertTrue;

/**
 * Test class for RegisterUserActivity. All UI tests related
 * to RegisterUserActivity are written. Robotium test framework
 * is used.
 * ** Make sure that no user is logged in before starting the tests
 *      Users are signed out after each test (@Before is not early enough)
 *      so try running the test twice
 * @author Olivier Vadiavaloo, Carter Sabadash
 * @version 2020.11.15
 */

public class RegisterUserActivityTest {
    private Solo solo;
    private String username;
    private String password;

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
        solo.clickOnButton("Create account");
        solo.assertCurrentActivity("Wrong activity/No activity switch", RegisterUserActivity.class);
    }

    @Test
    public void checkRegisterEmptyFields() {
        checkActivitySwitch();
        solo.enterText((EditText) solo.getView(R.id.register_username_editText), "");
        solo.enterText((EditText) solo.getView(R.id.register_password_editText), "");
        solo.enterText((EditText) solo.getView(R.id.register_email_editText), "");
        solo.enterText((EditText) solo.getView(R.id.register_phone_editText), "");

        solo.clickOnButton("Create account");
        solo.assertCurrentActivity("Wrong activity", RegisterUserActivity.class);
    }

    @Test
    public void checkRegisterEmptyUsername() {
        checkActivitySwitch();
        solo.enterText((EditText) solo.getView(R.id.register_username_editText), "");
        solo.enterText((EditText) solo.getView(R.id.register_password_editText), "testPassword");
        solo.enterText((EditText) solo.getView(R.id.register_email_editText), "");
        solo.enterText((EditText) solo.getView(R.id.register_phone_editText), "000 000 0000");

        solo.clickOnButton("Create account");
        solo.assertCurrentActivity("Wrong activity", RegisterUserActivity.class);
    }

    @Test
    public void checkRegisterEmptyPassword() {
        checkActivitySwitch();
        solo.enterText((EditText) solo.getView(R.id.register_username_editText), "testUsername");
        solo.enterText((EditText) solo.getView(R.id.register_password_editText), "");
        solo.enterText((EditText) solo.getView(R.id.register_email_editText), "");
        solo.enterText((EditText) solo.getView(R.id.register_phone_editText), "000 000 0000");

        solo.clickOnButton("Create account");
        solo.assertCurrentActivity("Wrong activity", RegisterUserActivity.class);
    }

    @Test
    public void checkRegisterEmptyEmail() {
        checkActivitySwitch();
        solo.enterText((EditText) solo.getView(R.id.register_username_editText), "testUsername");
        solo.enterText((EditText) solo.getView(R.id.register_password_editText), "password");
        solo.enterText((EditText) solo.getView(R.id.register_email_editText), "");
        solo.enterText((EditText) solo.getView(R.id.register_phone_editText), "000 000 0000");

        solo.clickOnButton("Create account");
        solo.assertCurrentActivity("Wrong activity", RegisterUserActivity.class);
    }

    @Test
    public void checkRegisterEmptyPhone() {
        checkActivitySwitch();
        solo.enterText((EditText) solo.getView(R.id.register_username_editText), "testUsername");
        solo.enterText((EditText) solo.getView(R.id.register_password_editText), "testPassword");
        solo.enterText((EditText) solo.getView(R.id.register_email_editText), "testEmail@bookbox.com");
        solo.enterText((EditText) solo.getView(R.id.register_phone_editText), "");

        solo.clickOnButton("Create account");
        solo.assertCurrentActivity("Wrong activity", RegisterUserActivity.class);
    }

    @Test
    public void checkRegisterTakenUsername() {
        checkActivitySwitch();
        solo.enterText((EditText) solo.getView(R.id.register_username_editText), "correctUsername");
        solo.enterText((EditText) solo.getView(R.id.register_password_editText), "testPassword");
        solo.enterText((EditText) solo.getView(R.id.register_email_editText), "");
        solo.enterText((EditText) solo.getView(R.id.register_phone_editText), "000 000 0000");

        solo.clickOnButton("Create account");
        solo.assertCurrentActivity("Wrong activity", RegisterUserActivity.class);
    }

    @Test
    public void checkRegisterSuccess() {
        int count = 1;
        boolean isSuccess = false;
        checkActivitySwitch();

        solo.enterText((EditText) solo.getView(R.id.register_phone_editText), "000 000 0000");

        while (!isSuccess) {
            String username = "bookboxtest" + Integer.toString(count);
            String password = "bookboxtest" + Integer.toString(count);
            String email = "bookboxtest" + Integer.toString(count) + "@bookbox.com";
            solo.enterText((EditText) solo.getView(R.id.register_username_editText), username);
            solo.enterText((EditText) solo.getView(R.id.register_password_editText), password);
            solo.enterText((EditText) solo.getView(R.id.register_email_editText), email);
            solo.clickOnButton("Create account");

            try {
                solo.sleep(5000);
                solo.assertCurrentActivity("Still in RegisterUserActivity", HomeActivity.class);
                isSuccess = true;
            } catch (junit.framework.ComparisonFailure e) {
                // continue to next iteration to try to register again
                Log.d("TAG", "HERE");
                count = count + 1;
                solo.enterText((EditText) solo.getView(R.id.register_username_editText), "");
                solo.enterText((EditText) solo.getView(R.id.register_password_editText), "");
                solo.enterText((EditText) solo.getView(R.id.register_email_editText), "");
            }
        }
    }

    /**
     * Close activity after each test
     * @throws Exception
     */
    @After
    public void tearDown() throws  Exception {
        // the only time it won't be cancelled is when it succeeds ->
        // natural flow will proceed to HomeActivity as expected, and
        // then the code is irrevelant
        solo.getCurrentActivity().setResult(CommonStatusCodes.CANCELED);
        solo.finishOpenedActivities();
        FirebaseAuth.getInstance().signOut();
    }
}
