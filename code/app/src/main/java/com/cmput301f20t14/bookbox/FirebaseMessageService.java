package com.cmput301f20t14.bookbox;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.cmput301f20t14.bookbox.activities.MainActivity;
import com.cmput301f20t14.bookbox.entities.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

/**
 * This class contains all the methods related to notifications
 * Resources consulted:
 *      https://firebase.google.com/docs/cloud-messaging/android/client
 * @author Carter Sabadash
 * @version 2020.11.15
 */
public class FirebaseMessageService extends FirebaseMessagingService {
    private final String TAG = "FCM Service";
    private final String CHANNEL_ID = "BookBox";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // we will not be using messages with a data payload
        // and all messages should have a notification payload
        if (remoteMessage.getNotification() != null) {
            createNotificationChannel();

            // for simplicity, make clicking the notification open the app (login -> if rememberMe
            // -> HomeActivity
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_notification_icon)
                    .setContentTitle(remoteMessage.getNotification().getTitle())
                    .setContentText(remoteMessage.getNotification().getBody())
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true);

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

            // https://stackoverflow.com/questions/25713157/generate-int-unique-id-as-android-notification-id
            // generate a unique id
            // auto-cancel is set so we don't need to save the id
            int id = Integer.parseInt(new SimpleDateFormat("ddHHmmss", Locale.CANADA).format(new Date()));
            notificationManager.notify(id, builder.build());
        }
    }

    // copied from https://developer.android.com/training/notify-user/build-notification
    // it is safe to call this multiple times
    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String name = "BookBox";
            String description = "BookBox Notification";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }


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
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() == null) { return; }
        String user = Objects.requireNonNull(mAuth.getCurrentUser().getDisplayName());
        HashMap<String, String> tokenInfo = new HashMap<>();
        tokenInfo.put("VALUE", token);
        FirebaseFirestore.getInstance().collection(User.USERS).document(user)
                .collection("TOKENS").document(token).set(tokenInfo);
    }
}