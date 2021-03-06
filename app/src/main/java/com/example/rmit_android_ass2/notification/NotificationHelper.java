package com.example.rmit_android_ass2.notification;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.rmit_android_ass2.HomeActivity;
import com.example.rmit_android_ass2.R;
import com.example.rmit_android_ass2.SiteDetailActivity;
import com.google.firebase.messaging.FirebaseMessaging;

public class NotificationHelper {
    // Constant declaration
    public static final String CHANNEL_ID = "NOTIFICATION";
    public static final CharSequence CHANNEL_NAME = "NOTIFICATION";
    public static final String CHANNEL_DESC = "NOTIFICATION";

    /**
     *  Function to create new notification channel for User
     *  Check version and set notification
     *
     * @param context Activity context
     * */
    public static void createNotificationChannel(Context context) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance);
            channel.setDescription(CHANNEL_DESC);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    /**
     *  Function to create notification to display
     *  On click message will start new activity with the cleaningSiteId message
     *
     * @param context Activity context
     * @param title title of message
     * @param body body of message
     * @param cleaningSiteId cleaning site id get from message
     * */
    public static void displayNotification(Context context, String title, String body, String cleaningSiteId){
        // Set direction when clicked
        Intent intent = new Intent(context, SiteDetailActivity.class);
        intent.putExtra("cleaningSiteId",cleaningSiteId);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                100,
                intent,
                PendingIntent.FLAG_CANCEL_CURRENT
        );

        // Custom notification
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(context, CHANNEL_ID)
                        .setSmallIcon(R.drawable.logo)
                        .setContentTitle(title)
                        .setContentText(body)
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true)
                        .setOnlyAlertOnce(true)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        // Build notification
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }
}
