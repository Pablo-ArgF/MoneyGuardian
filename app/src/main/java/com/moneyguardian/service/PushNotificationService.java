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
        String title = message.getNotification().getTitle();
        String text = message.getNotification().getBody();
        /**NotificationChannel channel = new NotificationChannel(
            CHANNEL_ID, "Heads up notification", NotificationManager.IMPORTANCE_HIGH
        );
        **/
        
        super.onMessageReceived(message);
    }
}
