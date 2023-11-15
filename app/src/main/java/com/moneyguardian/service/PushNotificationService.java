package com.moneyguardian.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class PushNotificationService extends FirebaseMessagingService {

    private final String CHANNEL_ID = "HEADS_UP_NOTIFICATION";

    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        if (message.getData().size() > 0) {
            // Aquí se pueden hacer jobs, como hacer que se añadan mensjaes, etc
            //if (/* Check if data needs to be processed by long running job */ true) {
                // For long-running tasks (10 seconds or more) use WorkManager.
                //scheduleJob();
            //} else {
                // Handle message within 10 seconds
                //handleNow();
            //}
            

        }

        super.onMessageReceived(message);
    }

    @Override
    public void onDeletedMessages() {
        super.onDeletedMessages();
    }
}