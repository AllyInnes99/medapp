package com.example.medapp;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

/**
 * Extended broadcast receiver for the medication refill events
 */
public class RefillReminder extends BroadcastReceiver {
    public static final int DEFAULT = -1;

    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "received", Toast.LENGTH_SHORT).show();
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        int medId = intent.getIntExtra("medId", DEFAULT);
        if (medId != DEFAULT) {
            MedicationModel med = databaseHelper.selectMedicationFromID(medId);
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

            Intent tapIntent = new Intent(context, UpdateMedActivity.class);
            tapIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            tapIntent.putExtra("medID", med.getMedicationId());
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, tapIntent, 0);


            String medName = med.getName();
            String title = medName + " supply low";
            String msg = String.format("Your supply of %s will run out soon. Please order new prescription.", medName);

            Notification notification = new NotificationCompat.Builder(context, App.REFILL_CHANNEL)
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
}
