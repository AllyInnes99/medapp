package com.example.medapp;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

/**
 * Extension of broadcastReceiver for the med taking notifications
 */
public class AlertReceiver extends BroadcastReceiver {

    public int medId;
    public int doseId;

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent tapIntent = new Intent(context, MainActivity.class);
        tapIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, tapIntent, 0);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        medId = intent.getIntExtra("medID", 0);
        doseId = intent.getIntExtra("doseID", 0);

        Intent takeIntent = new Intent(context, TakeReceiver.class);
        takeIntent.setAction("action.intent.action.NOTIFY");
        takeIntent.putExtra("doseID", doseId);
        takeIntent.putExtra("medID", medId);
        PendingIntent takePendingIntent = PendingIntent.getBroadcast(context, medId, takeIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        MedicationModel medModel = databaseHelper.selectMedicationFromID(medId);
        DoseModel doseModel = databaseHelper.selectDoseFromID(doseId);

        int quantity = doseModel.getAmount();
        String name = medModel.getName();

        if(!doseModel.isTaken()) {
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
                    .addAction(R.drawable.ic_healing, context.getApplicationContext().getString(R.string.take), takePendingIntent)
                    .setAutoCancel(true)
                    .build();
            notificationManager.notify(doseModel.getDoseId(), notification);
        }
    }
}
