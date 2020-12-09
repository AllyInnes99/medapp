package com.example.medapp;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class AlertReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        Intent tapIntent = new Intent(context, MainActivity.class);
        tapIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, tapIntent, 0);

        Intent takeIntent = new Intent(context, TakeReceiver.class);
        takeIntent.setAction(Intent.ACTION_EDIT);
        takeIntent.putExtra("take", 0);


        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        int quantity = (int) intent.getSerializableExtra("quantity");
        String name = (String) intent.getSerializableExtra("name");

        String title = "MedApp: Take " + name;
        String msg = "Time to take " + quantity + " of " + name;

        Notification notification = new NotificationCompat.Builder(context, App.MED_TAKING_CHANNEL)
                .setSmallIcon(R.drawable.ic_healing)
                .setContentTitle(title)
                .setContentText(msg)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_REMINDER)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build();
        notificationManager.notify(1, notification);
    }
}
