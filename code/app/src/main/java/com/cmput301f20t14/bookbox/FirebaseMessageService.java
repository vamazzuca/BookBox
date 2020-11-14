package com.cmput301f20t14.bookbox;

import android.util.Log;

import androidx.annotation.NonNull;

import com.cmput301f20t14.bookbox.entities.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Objects;

/**
 * This class contains all the methods related to notifications
 * Much of the code is taken from the samples at:
 *      https://firebase.google.com/docs/cloud-messaging/android/client
 * @author Carter Sabadash
 * @version 2020.11.12
 */
public class FirebaseMessageService extends FirebaseMessagingService {
    private final String TAG = "FCM Service";

    /**
     * Called if FCM registration token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the
     * FCM registration token is initially generated so this is where you would retrieve
     * the token.
     */
    @Override
    public void onNewToken(final String token) {
        Log.d(TAG, "Refreshed token: " + token);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // FCM registration token to your app server.

        // we need to update token in the database
        // get the user from displayname
        String user = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getDisplayName();
        assert user != null;
        FirebaseFirestore.getInstance().collection(User.USERS).document(user)
                .update("NOTIFICATION_TOKEN", token).addOnCompleteListener(
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Updated Token Successfully");
                            return;
                        } else {
                            // try again
                            Log.d(TAG, "Failed to set new Token");
                            onNewToken(token);
                        }
                    }
                }
        );
    }
}