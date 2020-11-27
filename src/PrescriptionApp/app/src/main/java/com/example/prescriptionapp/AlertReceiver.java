package com.example.prescriptionapp;

import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class AlertReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        int quantity = (int) intent.getSerializableExtra("quantity");
        String name = (String) intent.getSerializableExtra("name");

        String title = "Take " + name;
        String msg = "Time to take " + quantity + " of " + name;

        Notification notification = new NotificationCompat.Builder(context, App.MED_TAKING_CHANNEL)
                .setSmallIcon(R.drawable.ic_healing)
                .setContentTitle(title)
                .setContentText(msg)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_REMINDER).build();
        notificationManager.notify(1, notification);
    }
}